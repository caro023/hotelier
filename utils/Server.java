package utils;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.*;
import utils.Server;
import utils.type;

public class Server implements Runnable {
    private Socket socket;
   // private Status status = Status.PLAYING;
    private BufferedReader in;
    private PrintWriter out;
    public Server(Socket socket){
        this.socket = socket;
    }

    public void run(){
        try (
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true)) {
            while(true){
                String line = in.readLine().toLowerCase();
                switch(line){
                    case "login":
                    break;
                    case "logout":
                    break;
                    case "searchhotels":
                    break;
                    case "searchallhotels":
                    break;
                    case "insertreview":
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
