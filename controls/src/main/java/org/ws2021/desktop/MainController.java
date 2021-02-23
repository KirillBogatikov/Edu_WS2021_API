package org.ws2021.desktop;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ws2021.maintain.AppConfig;
import org.ws2021.util.Async;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;

public class MainController {
    private static MainController instance;
    
    public static MainController getInstance() {
        return instance;
    }
    
    @FXML
    private Parent root;
    
    @FXML
    private ListView<String> serversList;
    @FXML
    private TextField serverName;
    @FXML
    private TextField serverPort;
    @FXML
    private TextField dbHost;
    @FXML
    private TextField dbPort;
    @FXML
    private TextField dbName;
    @FXML
    private TextField dbUser;
    @FXML
    private TextField dbPassword;
    @FXML
    private ToggleButton updateDatabase;
    @FXML
    private ListView<String> serverErrors;
        
    private Map<String, AppConfig> servers; 
    private Map<String, ByteArrayOutputStream> logs;
    private Map<String, Process> processes;
    private Gson gson = new Gson();
    
    @FXML
    public void initialize() {
        instance = this;
        servers = new HashMap<>();
        logs = new HashMap<>();
        processes = new HashMap<>();
        serversList.getSelectionModel().selectedItemProperty().addListener((c, o, n) -> showItem(n));
        
        try {
            importData();
            Set<String> keys = servers.keySet();
            if (keys.size() > 0) {
                showItem(keys.iterator().next());
            }
        } catch(IOException e) {
            System.err.println(e);
        }
    }
    
    public void exportData() throws IOException {
        String serversJson = gson.toJson(servers);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("servers.json"))) {
            writer.write(serversJson);
        }
    }
    
    public void importData() throws IOException {
        String json = String.join("\n", Files.readAllLines(Paths.get("servers.json")));
        servers = gson.fromJson(json, new TypeToken<Map<String, AppConfig>>(){}.getType());
        
        for (String name : servers.keySet()) {
            serversList.getItems().add(name);
            logs.put(name, new ByteArrayOutputStream());
        }
    }
    
    private void showItem(String name) {
        AppConfig cfg = servers.get(name);
        serverName.setText(name);
        serverPort.setText(cfg.getHttpPort());
        dbHost.setText(cfg.getDatabaseHost());
        dbUser.setText(cfg.getUser());
        dbPassword.setText(cfg.getPassword());
        dbPort.setText(cfg.getDatabasePort());
        dbName.setText(cfg.getDatabaseName());
        updateDatabase.setSelected(cfg.isUpdateDatabase());
    }
    
    private void lock() {
        root.setDisable(true);
    }
    
    private void unlock() {
        root.setDisable(false);
    }
    
    public void saveServer() {
        lock();
        ObservableList<String> list = serversList.getItems();
        String name = serverName.getText();
        
        AppConfig cfg = new AppConfig();
        cfg.setDatabaseName(dbName.getText());
        cfg.setDatabaseHost(dbHost.getText());
        cfg.setDatabasePort(dbPort.getText());
        cfg.setPassword(dbPassword.getText());
        cfg.setUser(dbPassword.getText());
        cfg.setHttpPort(serverPort.getText());
        cfg.setUpdateDatabase(updateDatabase.isSelected());
        
        if (!servers.containsKey(name)) {
            list.add(name);
            logs.put(name, new ByteArrayOutputStream());
        }
        
        servers.put(name, cfg);
        
        unlock();
    }
    
    public void startAllServers() throws IOException {
        lock();
        
        for (String name : servers.keySet()) {
            startServer(name);
        }
        
        unlock();
    }
    
    public void startServer() throws IOException {
        startServer(serverName.getText());
    }
    
    private void startServer(String name) throws IOException {
        AppConfig cfg = servers.get(name);        
        
        ProcessBuilder pb = new ProcessBuilder("java", "-server", "-jar", "-Dserver.port=" + cfg.getHttpPort(), "web-1.0.0.jar", gson.toJson(cfg));
        pb.redirectErrorStream(true);
        
        final Process process = pb.start();
        processes.put(name, process);
        
        Async.run(() -> {
            ByteArrayOutputStream bytes = logs.get(name);
            
            try {
                InputStream input = process.getInputStream();
                int b;
                while ((b = input.read()) > 0) {
                    bytes.write(b);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    public void stopAllServers() {
        lock();
        
        for (String name : servers.keySet()) {
            stopServer(name);
        }
        
        unlock();
    }
    
    public void stopServer() {
        stopServer(serverName.getText());
    }
    
    private void stopServer(String name) {
        ByteArrayOutputStream bytes = logs.get(name);
        if (bytes != null) {
            bytes.reset();
        }
        
        Process proc = processes.get(name);
        if (proc != null) {
            System.out.println("shutdown " + name);
            proc.destroyForcibly();
        }
    }
    
    public void openConsole() {
        openConsole(serverName.getText());
    }
    
    private void openConsole(String name) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Console of " + name);
        
        VBox dialogPaneContent = new VBox();
        final TextArea textArea = new TextArea();
        dialogPaneContent.getChildren().add(textArea);

        alert.getDialogPane().setContent(dialogPaneContent);
         
        Async.run(() -> {
            ByteArrayOutputStream bytes = logs.get(name);
            
            String last = null;
            while(alert.isShowing()) {
                String current = bytes.toString();
                
                if (!current.equals(last)) {
                    last = current;
                    Platform.runLater(() -> textArea.setText(current));
                }
                
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {}
            }
        });
 
        alert.show();
    }
}
