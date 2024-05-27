package utils;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientHandler implements Runnable {
    private final Socket socket;

    private User userInstance;
    private BufferedReader in;
    private PrintWriter out;
    private Status status = Status.ATTIVO;

    public ClientHandler(Socket socket){
        this.socket = socket;
    }

    public void run(){
        try{
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            command("welcome");
            do {
                cmd();
            } while (status != Status.INATTIVO);
            in.close();
            out.close();
            this.socket.close();
        } catch (Exception e) {
            if(userInstance != null && userInstance.isLogged()){
                userInstance.logout();
            }
            output("Si è verificato un errore");
            System.err.printf("[WORKER] Errore: %s\n", e.getMessage());
        }
    }

    public void command(String cmd) {
        String str = "";
        if(cmd.equals("welcome")){
            str = str + "Benvenuto, questi sono i possibili comandi:\n";
        }
        else{
             str = str + "Comando: \""+cmd+"\" non supportato\nQuesti sono i possibili comandi:\n";
        }
        str = str+ "Register <username> <password>\n"
                +"Login <username> <password>\n"
                +"Logout\n"
                +"SearchHotel \"Nome Hotel\" \"Nome Città\n" +"SearchAllHotels \"Nome Città\"\n"
                +"InsertReview\n"+"ShowMyBadges\n"+"Exit";
        output(str);
    }

    private void cmd() throws IOException {
        System.out.println("In attesa di un comando");
            String line = in.readLine().toLowerCase();
            String[] args = line.split("\"");
            String[] splitLine = args[0].split(" ");
            switch(splitLine[0]){
                case "register":
                    if(splitLine.length!=3) {
                        command(line);
                        return;
                    }
                    if(!(userInstance == null)) {
                        output("Comando non disponibile, utente già loggato");
                        return;
                    }
                    else {
                        User user = User.register(splitLine[1], splitLine[2]);
                        if (user == null) {
                            output("Registrazione non riuscita");
                            return;
                        } else userInstance = user;
                        output("Registrazione effettuata");
                    }
                    break;
                case "login":
                    //cambia metodo login che restituisce user e passarlo a userInstance
                    if(splitLine.length!=3) {
                        command(line);
                        return;
                    }
                    else if((userInstance!=null)) {
                        output("Comando non disponibile, utente già loggato");
                        return;
                    }
                    else{
                        User user = User.login(splitLine[1], splitLine[2], out);
                        if(user != null){
                            userInstance = user;
                        }
                    }
                    break;
                case "logout":
                    if(splitLine.length!=1) {
                        command(line);
                        return;
                    }
                    if(userInstance==null) {
                        output("Utente non loggato");
                        return;
                    }
                    else {
                        out.println("exit");
                        out.println(userInstance.logout());
                        status = Status.INATTIVO;}
                    break;
                case "searchhotel":
                    if(args.length!=4) {
                        command(line);
                        return;
                    }
                    Hotel hotel = Hotel.searchHotel(args[1],args[3]);
                    if(hotel==null) output("Hotel non trovato");
                    else output(hotel.toString());
                    break;
                case "searchallhotels":
                    if(args.length!=2) {
                        command(line);
                        return;
                    }
                    CopyOnWriteArrayList<Hotel> hotels = Hotel.searchAllHotels(args[1]);
                    if(hotels==null) {
                        output("Città non trovata");
                        return;
                    }
                    else{
                    String allHotel = "";
                    for (Hotel h : hotels) {
                        allHotel += h.toString()+"\n";
                    }
                    output(allHotel);
                    }
                    break;
                case "insertreview":
                    if(args.length!=5){
                        command(line);
                        return;
                    }
                    if(userInstance==null){output("Utente non loggato");}
                    hotel = Hotel.searchHotel(args[1].trim(),args[3].trim());

                    if(hotel==null) {
                        output("Hotel non trovato");
                        return;
                    }
                    //controllo se input giusto
                    double[] SingleScores = new double[4];
                    String[] review = args[4].trim().split(" ");
                    if(review.length!=5){
                        command(line);
                        return;
                    }
                    //controllo se non inserisce numeri
                    for (int i = 0; i < 4; i++) {
                         double rating = Double.parseDouble(review[i]);
                         if(rating<0|| rating>5) {
                             output("Si accettano solo recensioni tra 1 e 5");
                             return;
                         }
                         SingleScores[i] = Double.parseDouble(review[i]);
                    }
                    String c = userInstance.insertReview(hotel, Double.parseDouble(review[4]), SingleScores);
                    output(c);
                    break;
                case "showmybadges":
                    if(splitLine.length!=1) {
                        command(line);
                        return;
                    }
                    if(userInstance==null){
                        output("Utente non loggato");
                        return;
                    }
                    else {output(userInstance.showMyBadges().toString());}
                    break;
                case "exit":
                    if(!(userInstance==null)) userInstance.logout();
                    out.println("exit");
                    status = Status.INATTIVO;
                    output("Grazie per aver utilizzato i nostri servizi");
                    break;
                default://scrivi qualcosa
                    command(line);
                    break;
            }
        }

    private void output(String command){
        if (command == null || command.trim().isEmpty()) {
            return;
        }
        String  msg;
        out.println(command.replace("\n", "|"));
        out.flush();
    }
    }



