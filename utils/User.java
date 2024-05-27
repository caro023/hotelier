package utils;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class User {
    private static JsonHandler JsonHandler;
    //ricorda gli utenti nel sistema
    private static final ConcurrentHashMap<String, User> registerUser = new ConcurrentHashMap<String, User>();
    private final String username;
    private final String password;
    private type badges;
    private int nReview=0;
    private transient boolean isLog=false;
    //il costruttore va chiamato solo durante la registrazione
    private User(String userName, String password){
            this.username = userName;
            this.badges = type.RECENSORE;
            this.password = password;
            this.isLog = false;
    }

    public static User getUser(String username){
        return registerUser.getOrDefault(username, null);
    }

    public void setUser(){
        String user = this.getUsername();
        registerUser.putIfAbsent(user, new User(user, password));
    }

    public static List<User> getAllUsers(){
        List<User> users = new ArrayList<User>();
        users.addAll(registerUser.values());
        return users;
    }

    public String getUsername(){
        return this.username;
    }

    private String getPassword(){
        return this.password;
    }

    public boolean isLogged(){
        return this.isLog;
    }

    //ritorna errore se non è presente
    public static User register(String username, String password){
        //ritorna errore
           if(registerUser.containsKey(username) || password == null) return null;
            User newUser = new User(username,password);
            registerUser.put(username, newUser);
            login(username,password,null);
            return newUser;
    }

    public static User login(String username, String password, PrintWriter out){
        User user = getUser(username);
        String str = null;
        if(user==null){
            str = "Utente non registrato\n";
        }
        else if(user.isLogged()){
            str = "Utente loggato da un altro dispositivo\n";
        }
        else if(!(user.getPassword().equals(password))){
             str = "Password errata o mancante\n";
        }
        if(str!= null && out !=null){
            out.println(str);
            return null;
        }
        //se è chiamata da register user è sempre non nulla
        user.isLog = true;
        user.setBadge();
        if(out != null){
            out.println("Login effettuato\n");
        }

        return user;
    }

    public String logout(){
        if(!(this.isLogged())){
            return "Utente non loggato";
        }
        this.isLog = false;
        //leave multicast
        return "Logout eseguito. Grazie per aver utilizzato i nostri servizi.";
    }

    public String insertReview(Hotel hotel,double GlobalScore,double[] SingleScores) throws IOException {
        if (!(this.isLogged())) return "Utente non loggato";
        /*
         * inserire il punteggio
         * aggiornare il rank
         */
        Review rev = Review.addReview(hotel, this, GlobalScore, SingleScores);
        if(rev != null) {
            this.nReview = this.nReview + 1;
            this.setBadge();
            //aggiorna il rank
            return "Recensione inserita con successo";
        }
        return "Recensione non inserita";
    }

    public void addBadge(){
        this.nReview = this.nReview + 1;
    }


    public void setBadge(){
        if(this.isLogged()){
            if(this.nReview<2){
                this.badges = type.RECENSORE;}
            else if (this.nReview<4) {
                this.badges = type.RECENSORE_ESPERTO;
            } else if (this.nReview<6) {
                this.badges = type.CONTRIBUTORE;
            } else if (this.nReview<8) {
                this.badges = type.CONTRIBUTORE_ESPERTO;
            } else{
                this.badges = type.CONTRIBUTORE_SUPER;
            }
            }
        //else { System.in.out("non sei loggato"); };
    }

    public type showMyBadges(){
        System.out.println(this.nReview);
        this.setBadge();
        return this.badges;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
