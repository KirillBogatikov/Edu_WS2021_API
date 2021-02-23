package org.ws2021.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.ref.WeakReference;
import java.net.URL;

public class Extractor {
    private static WeakReference<ClassLoader> loader;
    
    public static void init(ClassLoader loader) {
        Extractor.loader = new WeakReference<>(loader);
    }
    
    public static String readTextSilent(String resource) {
        try {
            return readText(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readText(String resource) throws IOException {
        try(Reader reader = openReader(resource);
            BufferedReader bufferedReader = new BufferedReader(reader)) {
            
            StringBuilder result = new StringBuilder();
            String temp;
            while((temp = bufferedReader.readLine()) != null) {
                result.append(temp).append("\n");
            }
            
            return result.toString();
        }
    }
    
    public static Reader openReader(String resource) throws IOException {
        return new InputStreamReader(openStream(resource));
    }
    
    public static InputStream openStream(String resource) throws IOException {
        if (loader == null) {
            throw new RuntimeException("Loader does not initiated!");
        }
        
        return loader.get().getResourceAsStream(resource);
    }
    
    public static URL openURL(String resource) throws IOException {
        if (loader == null) {
            throw new RuntimeException("Loader does not initiated!");
        }
        
        return loader.get().getResource(resource);
    }
}
