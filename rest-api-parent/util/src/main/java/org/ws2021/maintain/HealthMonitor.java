package org.ws2021.maintain;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.ws2021.util.Async;

public class HealthMonitor {
    private static final String REPOSITORY_ERROR_FORMAT = "Repository %s throws error while process method %s";
    private static final String TASK_FORMAT = "Task %s in %s";
    
    private List<MonitorListener> listeners;
    
    private Map<UUID, String> tasks;
    
    public HealthMonitor() {
        listeners = new ArrayList<>();
        tasks = new HashMap<>();
    }
    
    public void repositoryError(String repository, String method, Throwable t) {
        Async.run(() -> {
            listeners.forEach(e -> e.onError(format(REPOSITORY_ERROR_FORMAT, repository, method), t));
        });
    }
    
    public void panic(String where, Throwable t) {
        Async.run(() -> {
            listeners.forEach(e -> e.onPanic(where, t));
        });
    }
    
    public UUID taskStart(String where, String name) {
        UUID id = UUID.randomUUID();
        String text = format(TASK_FORMAT, name, where);
        tasks.put(id, text);
        
        Async.run(() -> {
            listeners.forEach(e -> e.onTaskStart(text));
        });
        
        return id;
    }
    
    public void taskEnd(UUID id) {
        String text = tasks.remove(id);
        
        Async.run(() -> {
            listeners.forEach(e -> e.onTaskEnd(text));
        });
    }
    
    public void log(String message) {
        System.out.println("LOG: " + message);
    }
    
    public void listen(MonitorListener listener) {
        listeners.add(listener);
    }
}
