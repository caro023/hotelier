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

    private static User getUser(String username){
        return registerUser.getOrDefault(username, null);
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
        //ritorna errore
            if(registerUser.containsKey(username) || password == null) return null;
            User newUser = new User(username,password);
            registerUser.put(username, newUser);
            updateFileUser(registerUser);
            login(username,password);
            return newUser;
    }

    public static String login(String username, String password){
        User user = getUser(username);
        if(user==null){
            return "Utente non registrato";
        }
        else if(user.isLogged()){
            return "Utente gia loggato";
        }
        else if(password==null || user.getPassword()!=password){
            return "Password errata o mancante";
        }
        user.isLog= true;
        return "Utente loggato con successo";
    }

    public static String logout(String username){
        User user = getUser(username);
        if(user==null || !(user.isLogged())){
            return "Utente non loggato";
        }
        user.isLog = false;
        return "Logout eseguito con successo";
    }

    public String insertReview(String nomeHotel,String nomeCittà, double GlobalScore,double[] SingelScores){
        if (this.isLogged()){
        nReview = nReview+1;
        setBadge();
        return "";
        }
    }

    private void setBadge(){
        if(this.isLogged()){
            int n = Math.floorDiv(nReview,2);
            switch(n){
                case 0: this.badges = type.RECENSORE;
                break;
                case 1: this.badges = type.RECENSORE_ESPERTO;
                break;
                case 2:this.badges = type.CONTRIBUTORE;
                break;
                case 3:this.badges = type.CONTRIBUTORE_ESPERTO;
                break;
                default:this.badges = type.CONTRIBUTORE_SUPER;
                break;
            }
        }
        else { }
    }

    public type showMyBadges(){
        return this.badges;
    }

    
}
