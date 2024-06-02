import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

import utils.ListenMulticast;

public class MainClient{
    //percorso del file di configurazione del client.
    public static final String configFile = "client.properties";
    public static String hostname; // localhost
    public static int port;
    //scanner per leggere l'input
    private static final Scanner scanner = new Scanner(System.in);
    //attributi per il multicast
    private static int multicastPort;
    private static final String multicastIP = "224.0.0.0";

    public static void main(String[] args) {
    try {
        //lettura file properties
        readConfig();
        //creazione socket
        Socket socket = new Socket(hostname, port);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ListenMulticast listener = null;
        //stampa il comando di benvenuto
        System.out.println(in.readLine().replace("|", "\n"));

        while (true) {
                //invio il comando
                String cmd = scanner.nextLine();
                out.println(cmd);

                //risposta del server
                String line = in.readLine();
                //comando di uscita
                if(line.equals("exit")) {
                    if(listener != null){
                        //interrompe thread per il gruppo multicast
                        listener.leaveGroup();
                        listener.interrupt();
                        listener.join();
                        listener = null;
                    }
                    System.out.println(in.readLine().replace("|", "\n"));
                    break;
                }
                line = line.replace("|", "\n");
                if(line.equals("Login effettuato")||line.equals("Registrazione effettuata")){
                    if(listener != null){
                        listener.interrupt();
                        listener.join();
                    }
                    //join al gruppo multicast
                    listener = new ListenMulticast(multicastIP,multicastPort);
                    listener.start();
                }
              System.out.println(line);
                System.out.flush();
            }
        in.close();
        out.close();
        socket.close();
        }
        catch (Exception e) {
            System.err.printf("Errore: %s\n", e.getMessage());
            System.exit(1);
            }
        
    }

    public static void readConfig() throws FileNotFoundException, IOException {
        InputStream input = MainClient.class.getResourceAsStream(configFile);
        Properties prop = new Properties();
        prop.load(input);
        port = Integer.parseInt(prop.getProperty("port"));
        multicastPort = Integer.parseInt(prop.getProperty("multicastPort"));
        input.close();
    }
}