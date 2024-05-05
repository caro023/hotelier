package utils;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.*;
import utils.type;
import utils.JsonHandler;

public class ClientHandler implements Runnable {
    private Socket socket;
    private User userInstance;
    private JsonHandler jsonHandler;
    private BufferedReader in;
    private PrintWriter out;
    public ClientHandler(Socket socket, JsonHandler jsonHandler){
        this.socket = socket;
        this.jsonHandler = jsonHandler;
    }

    public void run(){
        try (
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true)) {
            while(true){
                String line = in.readLine().toLowerCase();
                switch(line){
                    case "register": 
                    User user = register(username, password);
                    if(user==null){
                        System.out.println("errore");
                    }
                    else userInstance = user;
                    break;
                    case "login":  
                     userInstance.login(username, password);
                    break;
                    case "logout":
                     userInstance.logout(username);
                    break;
                    case "searchhotels":
                    break;
                    case "searchallhotels":
                    break;
                    case "insertreview":
                    if(userInstance==null){
                     userInstance.insertReview();
                    }
                    break;
                    case "showmybadges":
                    break;
                    default://scrivi qualcosa
                    break;
                }

            }
        } catch (Exception e) {
            System.err.printf("[WORKER] Errore: %s\n", e.getMessage());
        }
    }

}
