package utils;
import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
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
            boolean f = true;
            do {
                cmd();
            } while (status != Status.INATTIVO);
            in.close();
            out.close();
            this.socket.close();
        } catch (Exception e) {
            System.err.printf("[WORKER] Errore: %s\n", e.getMessage());
        }
    }

    private void cmd() throws IOException {
        System.out.println("In attesa di un comando");
            String line = in.readLine().toLowerCase();
            String[] splitLine = line.split(" ");
            System.out.println(splitLine[0]);
            switch(splitLine[0]){
                case "register":
                    if(!(userInstance == null)) {
                        out.println("Comando non disponibile, utente già loggato");
                        return;
                    }
                    else {
                        User user = User.register(splitLine[1], splitLine[2]);
                        if (user == null) {
                            out.println("registrazione non riuscita");
                            return;
                        } else userInstance = user;
                        out.println("registrazione riuscita");
                    }
                    break;
                case "login":
                    //cambia metodo login che restituisce user e passarlo a userInstance
                    if(splitLine.length!=3) {
                        out.println("errore");
                        return;
                    }
                    else if((userInstance!=null)) {
                        out.println("Comando non disponibile, utente già loggato");
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
                    if(userInstance==null) {
                        out.println("Utente non loggato");
                        return;
                    }
                    else {
                        out.println("exit");
                        out.println(userInstance.logout());
                        status = Status.INATTIVO;}
                    break;
                case "searchhotel":

                    Hotel hotel = Hotel.searchHotel(splitLine[1], splitLine[2]);
                    if(hotel==null) out.println("Hotel non trovato");
                    else out.println(hotel);
                    break;
                case "searchallhotels":
                    Hotel.searchAllHotels(splitLine[1]);
                    break;
                case "insertreview":
                    if(userInstance==null){System.out.println("utente non loggato");}
                    else if(splitLine.length==8){
                        double[] SingleScores = new double[4];
                        for (int i = 4; i < 8; i++) {
                            SingleScores[i - 4] = Double.parseDouble(splitLine[i]);
                        }
                        userInstance.insertReview(splitLine[1], splitLine[2],Double.parseDouble(splitLine[3]), SingleScores);
                    }
                    break;
                case "showmybadges":
                    if(userInstance==null){out.println("utente non loggato");}
                    else {out.println(userInstance.showMyBadges());}
                    break;
                case "exit":
                    if(!(userInstance==null)) userInstance.logout();
                    out.println("exit");
                    status = Status.INATTIVO;
                    out.println("Grazie per aver utilizzato i nostri servizi");
                    break;
                default://scrivi qualcosa
                    out.println("Comando sconosciuto");
                    break;
            }
        }

    }


