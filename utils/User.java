package utils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

//classe per rappresentare gli utenti
public class User {
    //HashMap statica con tutti gli utenti
    private static final ConcurrentHashMap<String, User> registerUser = new ConcurrentHashMap<String, User>();
    //attributi relativi a un utente
    private final String username;
    private final String password;
    private type badges;
    private int nReview=0;
    private transient boolean isLog=false;
    private User(String userName, String password){
            this.username = userName;
            this.badges = type.RECENSORE;
            this.password = password;
            this.isLog = false;
    }

    //metodi getter e setter
    public static User getUser(String username){
        return registerUser.getOrDefault(username, null);
    }

    //inserisce un utente nell'HashMap
    public void setUser(){
        String user = this.getUsername();
        registerUser.putIfAbsent(user, new User(user, password));
    }

    //restituisce tutti gli utenti nel sistema
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

    public static User register(String username, String password){
           if(registerUser.containsKey(username) || password == null) return null; //username già presente
            User newUser = new User(username,password);
            registerUser.put(username, newUser);
            //effettua il login
            login(username,password,null);
            return newUser;
    }

    public static User login(String username, String password, PrintWriter out){
        //cerca l'utente
        User user = getUser(username);
        String str = null;
        if(user==null){
            str = "Utente non registrato";
        }
        else if(user.isLogged()){
            str = "Utente loggato da un altro dispositivo";
        }
        else if(!(user.getPassword().equals(password))){
             str = "Password errata o mancante";
        }
        if(str!= null && out !=null){
            out.println(str);
            return null;
        }
        user.isLog = true;
        //inizializza il badge
        user.setBadge();
        //se chiamata da register out non è presente
        if(out != null){
            out.println("Login effettuato");
        }
        return user;
    }

    public String logout(){
        if(!(this.isLogged())){
            return "Utente non loggato";
        }
        this.isLog = false;
        return "Logout eseguito. Grazie per aver utilizzato i nostri servizi.";
    }

    public String insertReview(Hotel hotel,double GlobalScore,double[] SingleScores) throws IOException {
        if (!(this.isLogged())) return "Utente non loggato";
        Review rev = Review.addReview(hotel, this, GlobalScore, SingleScores);
        if(rev != null) {
            //aggiorna il badge
            this.addReview();
            this.setBadge();
            return "Recensione inserita con successo";
        }
        return "Hotel mancante o recensione per l'hotel già inserita prima di "+Review.getDayReview()+" giorni fà";
    }

    public void addReview(){
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
    }

    public type showMyBadges(){
        this.setBadge();
        return this.badges;
    }

//override dei metodi per consentire il contronto per username
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
