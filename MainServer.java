/*
 * carica da file json la descrizione degli hotels
 * gestisce registrazione e login utenti
 * effettua ricerche
 * ha info su hotel e utenti
 * memorizza recensioni e aggiorna il calcolo delle recensioni dell'utente per attribuzione distintivi
 * aggiorna il ranking(parametro di input tempo tra due aggiornamenti)
 * quando cambia il primo classificato in un ranking locale invia notifica agli utenti loggati---> secondo connessione UDP su un gruppo multicast
 * si può implementare con NIO o java I/O e threadpool
 * i file per la memorizzazione degli utenti e dei contenuti memorizzano le info in formato json
 * CODICE BEN COMMENTATO
 * CLASSI CON MAIN LO DEVONO AVERE NEL NOME
 * CONSEGNARE FILE JAR PER OGNI APPLICAZIONE
 * PARAMETRI DI INPPUT LETTI AUTONOMAMENTE DA FILE TESTUALI
 * COLLEGARE LIBRERIE ESTERNE
 * */
import utils.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.*;
//import utils.TerminationHandler;
//import utils.JsonHandler;
//import utils.ClientHandler;


 public class MainServer{
    // Percorso del file di configurazione del client.
    public static final String configFile = "server.properties";
    // Nome host e porta del server.
   // public static String hostname; // localhost
    private static int port; // 12000
    private static int maxDelay;
    private static int minDayReview;
    private static final ExecutorService pool = Executors.newCachedThreadPool();
     private static final String fileHotel = "Hotels.json";
     private static final String fileUser = "User.json";
     private static final String fileReview = "Review.json";
    private static final String fileCity = "city.txt";
    private static final JsonHandler jsonHandler = new JsonHandler(fileHotel, fileUser, fileReview, fileCity);
    //multicast
    private static int updateRank;
    public static int multicastPort;
    public static final String multicastIP= "224.0.0.0";

    public static void main(String[] args) {
    try {
        readConfig();
        /*inizializzare gli hotel nel file json
         * inizializzare gli utenti nel file json
         * inizializzare servizio multicast
         */

        ServerSocket serverSocket = new ServerSocket(port);
        Review.setDayReview(minDayReview);
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        updateBestHotel update = new updateBestHotel(multicastIP,multicastPort);
        scheduler.scheduleAtFixedRate(update,10, updateRank, TimeUnit.MINUTES);
        Runtime.getRuntime().addShutdownHook(new TerminationHandler(maxDelay,pool, serverSocket, jsonHandler,update));
        System.out.printf("[SERVER] In ascolto sulla porta: %d\n", port);
        while (true) {
            Socket socket;
            // Accetto le richieste provenienti dai client.
            try {socket = serverSocket.accept();}
            catch (SocketException e) {break;}
            pool.execute(new ClientHandler(socket));
            }

        }
        catch (Exception e) {
            System.err.printf("Errore: %s\n", e.getMessage());
            System.exit(1);
            }

    }


    public static void readConfig() throws FileNotFoundException, IOException {
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