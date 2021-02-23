package org.ws2021;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import javax.crypto.SecretKey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.ws2021.maintain.AppConfig;
import org.ws2021.maintain.HealthMonitor;
import org.ws2021.maintain.MonitorListener;
import org.ws2021.repository.AirportRepository;
import org.ws2021.repository.BookingRepository;
import org.ws2021.repository.FlightRepository;
import org.ws2021.repository.UserRepository;
import org.ws2021.service.AirportService;
import org.ws2021.service.AuthService;
import org.ws2021.service.BookService;
import org.ws2021.service.FlightService;
import org.ws2021.service.UserService;
import org.ws2021.sql.Database;
import org.ws2021.util.Extractor;
import org.ws2021.util.JwtAuth;

import com.google.gson.Gson;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

@SpringBootApplication
public class Application {
    private static HealthMonitor health;
    private static Database database;
    
    private AirportRepository airportRepository;
    private BookingRepository bookingRepository;
    private FlightRepository flightRepository;
    private UserRepository userRepository;
    
    private AuthService authService;
    private AirportService airportService;
    private BookService bookService;
    private FlightService flightService;
    private UserService userService;
    
    private JwtAuth jwt;
    
    @Bean
    public AirportRepository airportRepository() {
        if (airportRepository == null) {
            airportRepository = new AirportRepository(database);
        }        
        return airportRepository;
    }
    
    @Bean
    public BookingRepository bookingRepository() {
        if (bookingRepository == null) {
            bookingRepository = new BookingRepository(database);
        }        
        return bookingRepository;
    }
    
    @Bean
    public FlightRepository flightRepository() {
        if (flightRepository == null) {
            flightRepository = new FlightRepository(database);
        }      
        return flightRepository;
    }
    
    @Bean
    public UserRepository userRepository() {
        if (userRepository == null) {
            userRepository = new UserRepository(database);
        }        
        return userRepository;
    }
    
    @Bean
    public AuthService authService() {
        if (authService == null) {
            authService = new AuthService(health, userRepository());
        }
        return authService;
    }
    
    
    @Bean
    public AirportService airportService() {
        if (airportService == null) {
            airportService = new AirportService(health, airportRepository());
        }
        return airportService;
    }
    
    @Bean
    public BookService bookService() {
        if (bookService == null) {
            bookService = new BookService(health, bookingRepository());
        }
        return bookService;
    }
    
    @Bean
    public FlightService flightService() {
        if (flightService == null) {
            flightService = new FlightService(health, flightRepository());
        }
        return flightService;
    }
    
    @Bean
    public UserService userService() {
        if (userService == null) {
            userService = new UserService(health, userRepository(), bookingRepository());
        }
        return userService;
    }

    @Bean
    public JwtAuth jwtAuth() {
        if (jwt == null) {
            SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            //SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode("NrJH/VAsLdgFW7daOQ5r1p2jLGGmu/9BOTUOQ+l2RpQ="));
            jwt = new JwtAuth(secretKey);

            String secretString = Encoders.BASE64.encode(secretKey.getEncoded());
            health.log(secretString);
        }
        
        return jwt;
    }
    
    public static void main(String[] args) {
        System.out.println(Arrays.toString(args));
        Extractor.init(Application.class.getClassLoader());
        
        Gson gson = new Gson();
        AppConfig dbcfg = gson.fromJson(args[0], AppConfig.class);
        
        health = new HealthMonitor();
        health.listen(new MonitorListener() {

            @Override
            public void onError(String text, Throwable t) {
                StringWriter errors = new StringWriter();
                t.printStackTrace(new PrintWriter(errors));
                System.err.printf("ERROR =(\n%s: %s\n", text, errors.toString());
            }

            @Override
            public void onPanic(String text, Throwable t) {
                StringWriter errors = new StringWriter();
                t.printStackTrace(new PrintWriter(errors));
                System.err.printf("PANIC!\n%s: %s\n", text, errors.toString());
                System.exit(0);
            }

            @Override
            public void onTaskStart(String text) {
                System.err.printf("Task start: %s\n", text);
            }

            @Override
            public void onTaskEnd(String text) {
                System.err.printf("Task end: %s\n", text);
            }
            
        });
        
        try {
            database = new Database(health, dbcfg.toJDBC(), dbcfg.getUser(), dbcfg.getPassword(), dbcfg.isUpdateDatabase());
            
            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 0, newArgs, 0, args.length - 1);
            
            SpringApplication.run(Application.class, newArgs);
        } catch(Throwable t) {
            health.panic("Application", t);
        }
    }

}
