import utils.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.*;


 public class MainServer{
    //percorso del file di configurazione del client.
    public static final String configFile = "server.properties";
    public static String hostname; // localhost
    private static int port;
    private static int maxDelay; //tempo terminazione
    private static int minDayReview;
    private static final ExecutorService pool = Executors.newCachedThreadPool(); //pool ClientHandler
     private static final String fileHotel = "Hotels.json";
     private static final String fileUser = "User.json";
     private static final String fileReview = "Review.json";
    private static final String fileCity = "city.txt";
    private static final JsonHandler jsonHandler = new JsonHandler(fileHotel, fileUser, fileReview, fileCity);
    //attributi per il multicast
    private static int updateRank; //ogni quanto si controlla il primo classificato
    public static int multicastPort;
    public static final String multicastIP= "224.0.0.0";

    public static void main(String[] args) {
    try {
        //lettura file properties
        readConfig();
        //creazione welcome socket
        ServerSocket serverSocket = new ServerSocket(port);
        //minimo necessario tra due recensioni dello stesso utente per lo stesso albergo
        Review.setDayReview(minDayReview);
        //thread per notifiche sul canale multicast
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        updateBestHotel update = new updateBestHotel(multicastIP,multicastPort);
        scheduler.scheduleAtFixedRate(update,0, updateRank, TimeUnit.MINUTES);
        //thread per la terminazione del server
        Runtime.getRuntime().addShutdownHook(new TerminationHandler(maxDelay,pool, serverSocket, jsonHandler,scheduler));
        System.out.printf("[SERVER] In ascolto sulla porta: %d\n", port);
        while (true) {
            Socket socket;
            //Accetta le richieste provenienti dai client e crea una connection socket
            try {socket = serverSocket.accept();}
            catch (SocketException e) {break;}
            //crea un thread per gestire la richiesta
            pool.execute(new ClientHandler(socket));
            }
        }
        catch (Exception e) {
            System.err.printf("Errore: %s\n", e.getMessage());
            System.exit(1);
            }
    }

    public static void readConfig() throws IOException {
        InputStream input = MainServer.class.getResourceAsStream(configFile);
        Properties prop = new Properties();
        prop.load(input);
        port = Integer.parseInt(prop.getProperty("port"));
        maxDelay = Integer.parseInt(prop.getProperty("maxDelay"));
        minDayReview = Integer.parseInt(prop.getProperty("minDayReview"));
        updateRank = Integer.parseInt(prop.getProperty("updateRank"));
        multicastPort = Integer.parseInt(prop.getProperty("multicastPort"));
        input.close();
    }
}