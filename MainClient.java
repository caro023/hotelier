/*
 * register(user,pass) il server risponde con un codice, errore se user già presente o pass vuota
 * login(user,pass) codice di successo, se ha già effettuato il login e la password
 * logout(user)
 * searchHotel(nome,città) cerca dati di un hotel e li invia. Anche per utenti non loggati
 * searchAllHotels(città) ordinati per ranking. Anche per utenti non loggati
 * insertReview(nomeHotel, nomeCittà, GlobalScore,[ ]SingleScore) utente deve avere il login
 * showMyBadges() mostra i distintivo all'utente loggato
 * registrazione con connessione tcp con il server
 * dopo la registrazione subito il login. Dopo Il client si registra a un servizio di notifica multicast
 * CODICE BEN COMMENTATO
 * SOLO CODICE SORGENTE, NON FILE CREATI DALL'IDE PER GESTIRE IL PROGETTO
 * ALLEGARE LIBRERIE ESTERNE(GSON)
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;
import utils.type;

public class MainClient{
    // Percorso del file di configurazione del client.
    public static final String configFile = "client.properties";
    // Variabile globale che rappresenta lo stato corrente.
    public static type Type = type.RECENSORE;
    // Nome host e porta del server.
    public static String hostname; // localhost
    public static int port; // 12000

    // Socket e relativi stream di input/output.
    private static Scanner scanner = new Scanner(System.in);
    private static Socket socket;
    private static BufferedReader in;
    private static PrintWriter out;

    //multicasto

    public static void main(String[] args) {
    try {
        readConfig();
        socket = new Socket(hostname, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        while (true) {
                //invio il comando
                String cmd = scanner.nextLine();
                out.println(cmd);
                
                //risposta del server
                String line = in.readLine();
                System.out.printf(line);
            }
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
       // maxDelay = Integer.parseInt(prop.getProperty("maxDelay"));
        input.close();
    }
}