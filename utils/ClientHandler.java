package utils;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;
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
            System.err.printf("[WORKER] Errore: %s\n", e.getMessage());
        }
    }

    public void command(String cmd) {
        String str = "";
        if(cmd.equals("welcome")){
            str = str + "Benvenuto, questi sono i possibili comandi:\n";
        }
        else{
             str = str + "Comando"+cmd+"non supportato\n questi sono i possibili comandi:\n";
        }
        str = str+ "Register <username> <password>\n"
                +"Login <username> <password>\n"
                +"Logout\n"
                +"SearchHotel \"Nome Hotel\" \"Nome Città\n" +"SearchAllHotels \"Nome Città\"\n"
                +"InsertReview\n"+"ShowMyBadges\n"+"Exit\n";
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
                        output("Comando non disponibile, utente già loggato\n");
                        return;
                    }
                    else {
                        User user = User.register(splitLine[1], splitLine[2]);
                        if (user == null) {
                            output("Registrazione non riuscita\n");
                            return;
                        } else userInstance = user;
                        output("Benvenuto " + splitLine[1]+"\n");
                    }
                    break;
                case "login":
                    //cambia metodo login che restituisce user e passarlo a userInstance
                    if(splitLine.length!=3) {
                        command(line);
                        return;
                    }
                    else if((userInstance!=null)) {
                        output("Comando non disponibile, utente già loggato\n");
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
                        output("Utente non loggato\n");
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
                    if(hotel==null) output("Hotel non trovato\n");
                    else output(hotel.toString());
                    break;
                case "searchallhotels":
                    if(args.length!=2) {
                        command(line);
                        return;
                    }
                    CopyOnWriteArrayList<Hotel> hotels = Hotel.searchAllHotels(args[1]);
                    if(hotels==null) output("Città non trovata\n");
                    else output(hotels.toString());
                    break;
                case "insertreview":
                    //inrew "hotel" "città" 1 2 3 4 5
                    if(userInstance==null){output("Utente non loggato\n");}
                    hotel = Hotel.searchHotel(args[1].trim(),args[3].trim());

                    if(hotel==null) {
                        output("Hotel non trovato\n");
                        break;
                    }
                    //controllo se input giusto
                    double[] SingleScores = new double[4];
                    String[] review = args[4].trim().split(" ");
                    if(review.length!=5) command(line);
                    //controllo se non inserisce numeri
                    for (int i = 0; i < 4; i++) {
                         double rating = Double.parseDouble(review[i]);
                         if(rating<0|| rating>5) {
                             output("Si accettano solo recensioni tra 1 e 5\n");
                             break;
                         }
                         SingleScores[i] = Double.parseDouble(review[i]);
                    }
                    output(userInstance.insertReview(hotel, Double.parseDouble(review[4]), SingleScores));
                    break;
                case "showmybadges":
                    if(splitLine.length!=1) {
                        command(line);
                        return;
                    }
                    if(userInstance==null){output("utente non loggato\n");}
                    else {out.println(userInstance.showMyBadges());}
                    break;
                case "exit":
                    if(!(userInstance==null)) userInstance.logout();
                    out.println("exit");
                    status = Status.INATTIVO;
                    output("Grazie per aver utilizzato i nostri servizi\n");
                    break;
                default://scrivi qualcosa
                    command(line);
                    break;
            }
        }
    private void output(String command){
        String  msg;
        msg = ("'" + command  );
      //  System.out.println("[WORKER: "+this.clientName()+"] " + command + " - " + ret.toString());
        out.println(command.replace("\n", "|"));
        out.flush();
    }
    }


