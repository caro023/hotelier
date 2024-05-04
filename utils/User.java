package utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class User {

    private static JsonHandler JsonHandler;
    //ricorda gli utenti nel sistema
    private static ConcurrentHashMap<String, User> registerUser;
    private String username;
    private String password;
    private type badges;
    private int nReview;
    private boolean isLog;
    //il costruttore va chiamato solo durante la registrazione
    private User(String userName, String password){
            this.username = userName;
            this.badges = type.RECENSORE;
            this.password = password;
            this.nReview = 0;
            this.isLog = false;
    }

    private String getUsername(){
        return this.username;
    }

    private String getPassword(){
        return this.password;
    }

    public boolean isLogged(){
        return this.isLog;
    }
    //ritorna errore se non è presente
    public static User register(String username, String password){
            User newUser = new User(username,password);
            if(registerUser.containsKey(username) || password == null) return null;
            registerUser.put(username, newUser);
            updateFileUser(registerUser);
    }

    
}
