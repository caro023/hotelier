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
        System.out.println("in attesa di un comando");
            System.out.println("Waiting");
            String line = in.readLine().toLowerCase();
            String[] splitLine = line.split(" ");
            switch(splitLine[0]){
                case "register":
                    User user = User.register(splitLine[1], splitLine[2]);
                    System.out.println("registrazione riuscita");
                    if(user==null){
                        System.out.println("errore");
                    }
                    else userInstance = user;
                    break;
                case "login":
                    //cambia metodo login che restituisce user e passarlo a userInstance
                    if(splitLine.length!=3) System.out.println("errore");
                    else{
                        System.out.println(User.login(splitLine[1], splitLine[2]));
                    }
                    break;
                case "logout":
                    //controlla se user è nulla
                    System.out.println( userInstance.logout());
                    break;
                case "searchhotels":
                    Hotel.searchHotel(splitLine[1], splitLine[2]);
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
                    if(userInstance==null){System.out.println("utente non loggato");}
                    else {userInstance.showMyBadges();}
                    break;
                default://scrivi qualcosa
                    break;
            }
        }

    }


