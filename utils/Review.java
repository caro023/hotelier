package utils;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

//classe per rappresentare le recensioni
public class Review {
    //attributi relativi a una recensione
    private double rate;
    private double[] ratings;
    private final String date;
    private transient Hotel hotel;
    private transient User user;
    private final String userName;
    private final String nameHotel;
    private final String city;
    //HashMap statica con tutte le recensioni
    private static final ConcurrentHashMap<Hotel, CopyOnWriteArrayList<Review>> hotelReview = new ConcurrentHashMap<Hotel, CopyOnWriteArrayList<Review>>();
    //tempo minimo tra due recensioni dello stesso utente per lo stesso hotel
    private static Integer MIN_DAYS_BETWEEN_REVIEWS = null;
    private static final Object lock = new Object();

    public Review(double rate, double[] ratings, String date, String userName, String nameHotel, String city) {
        this.rate = rate;
        this.ratings = ratings;
        this.date = date;
        this.userName = userName;
        //inizializza l'utente
        this.user = User.getUser(userName);
        this.nameHotel = nameHotel;
        this.city = city;
        //inizializza l'hotel
        this.hotel = Hotel.searchHotel(this.nameHotel,this.city);
    }

    //metodi getter e setter
    public static void setDayReview(Integer min){
        MIN_DAYS_BETWEEN_REVIEWS = min;
    }

    public static int getDayReview(){
        return MIN_DAYS_BETWEEN_REVIEWS;
    }

    public User getUser(){
        return this.user;
    }
    private void setUser(User user) {
        this.user=user;
    }

    public Hotel getHotel(){
        return this.hotel;
    }

    private void setHotel(Hotel hotel){
        this.hotel = hotel;
    }

    private String getUsername() {
        return this.userName;
    }

    public String getDate() {
        return this.date;
    }

    public double getRate(){
        return this.rate;
    }

    public double[] getRatings(){
        return this.ratings;
    }

    //restituisce tutte le recensioni
    public static List<Review> getAllReviews() {
        List<Review> allReview = new ArrayList<>();
        for (CopyOnWriteArrayList<Review> reviewHotel : hotelReview.values()) {
            allReview.addAll(reviewHotel);
        }
        return allReview;
    }

    //inserisce una recensione nell'HashMap
    public void setReview(){
        Hotel hotel = Hotel.searchHotel(this.nameHotel.toLowerCase(),this.city.toLowerCase());
        if(hotel != null) {
            this.setHotel(hotel);
            if (hotelReview.containsKey(hotel)) {
                CopyOnWriteArrayList<Review> reviews = hotelReview.get(hotel);
                reviews.add(this);
            } else {
                System.out.println("Hotel not supported: " + hotel);
            }
        }
    }

    //inizializza il punteggio degli hotel
    public static void initializeHotelScore() {
        LocalDateTime now = LocalDateTime.now();
        for (Map.Entry<Hotel, CopyOnWriteArrayList<Review>> entry : hotelReview.entrySet()) {
            Hotel hotel = entry.getKey();
            CopyOnWriteArrayList<Review> reviews = entry.getValue();
            //totale recensioni per hotel
            int totalReviews = reviews.size();
            hotel.setTotalVote(totalReviews);
            //variabile per il rate medio dell'hotel
            double totalRate = 0.0;
            //variabile per la media dei giorni di distanza tra oggi e le recensioni
            long totalDay = 0;
            //varibaile per la media dei punteggi singoli
            double[] ratings = new double[4];
            //somma attributi di tutte le recensioni per hotel
            for (Review review : reviews) {
                totalRate += review.getRate();
                String date = review.getDate();
                LocalDateTime dateTime = dateTime(date);
                long day = ChronoUnit.DAYS.between(dateTime, now);
                totalDay += day;
                double[] rat = review.getRatings();
                for (int i = 0; i < 4; i++) {
                    ratings[i] = ratings[i] + rat[i];
                }
                String UserName = review.getUsername();
                review.setUser(User.getUser(UserName));
                User user = review.getUser();
                if(user!=null){
                    //aggiorna il numero di recensioni per utente
                    user.addReview();
                }
            }
            //se esistono recensioni setta gli attributi calcolati all'hotel
            if(totalReviews > 0) {
                hotel.setRate(totalRate);
                LocalDateTime date = now.minusDays(totalDay/totalReviews);
                hotel.setTimeReview(date);
                for(Integer i = 0; i < 4; i++) {
                    ratings[i] /= totalReviews;
                }
                hotel.setRatings(ratings);
                //calcola il punteggio
                calculateHotelScore(hotel, totalRate/totalReviews,ratings,totalDay);
            }
        }
    }

    //aggiorna il punteggio di un hotel
    public static void calculateHotelScore(Hotel hotel, double rate, double[] ratings, long day) {
        //Pesi per ciascun parametro
        double weightRate = 0.5;
        double weightRatings = 0.5;
        double weightDaysDifference = -0.2; // Peso negativo per penalizzare recensioni vecchie
        double weightTotalReviews = 0.2;
        //normalizzo il rate da 0 a 100
        rate *= 20;
        double totRatings = 0;
        for(double rating : ratings){
            //somma punteggi parziali
            totRatings += rating;
        }
        //normalizzo ratings da 0 a 100
        totRatings *= 5;
        double totalVote = hotel.getTotalVote();
        //calcolo del punteggio
        double finalScore = (weightRate * rate) +
                (weightRatings * totRatings) +
                (weightDaysDifference * day) +
                (weightTotalReviews * totalVote);
        //evita punteggi negativi
        if(finalScore<0){finalScore=0;}
        hotel.setTotalScore(finalScore);
    }

    //aggiunge una nuova recensione
    public static Review addReview(Hotel hotel, User user,double rate,double [] ratings) throws IOException {
        //giorno e ora in cui viene inserita
        LocalDateTime now = LocalDateTime.now();
        String date = stringTime(now);
        if (hotelReview.containsKey(hotel)) {
            CopyOnWriteArrayList<Review> reviews = hotelReview.get(hotel);
            for (Review review : reviews) {
                if(review.getDate()!=null){
                    //controlla se l'utente ha già inserito una recensione
                    if (review.getUser().equals(user) && ChronoUnit.DAYS.between(dateTime(review.getDate()), now) < MIN_DAYS_BETWEEN_REVIEWS) {
                        return null;
                    }
                }
            }
            String userName= user.getUsername();
            //crea una nuova recensione
            Review review = new Review(rate, ratings, date, userName, hotel.getName(),hotel.getCity());
            reviews.add(review);
            //ricalcola i punteggi dell'hotel
            hotel.updateVote();
            hotel.addRate(rate);
            hotel.addRatings(ratings);
            //si sincronizza per evitare letture e scritture sbagliate sulla data
            synchronized (lock) {
                LocalDateTime dateTime = hotel.getTimeReview();
                if (dateTime != null) { //hotel con altre recensioni
                    long day = ChronoUnit.DAYS.between(dateTime, now);
                    //ricalcolo media dei giorni
                    day = day * hotel.getTotalVote() / (hotel.getTotalVote() + 1);
                    //calcolo punteggio
                    calculateHotelScore(hotel, rate, ratings, day);
                    LocalDateTime avgDate = now.minusDays(day);
                    hotel.setTimeReview(avgDate);
                } else {
                    //calcolo punteggio
                    calculateHotelScore(hotel, rate, ratings, 0);
                    hotel.setTimeReview(now);
                }
                //riordina gli hotel della città
                Hotel.sortHotel(hotel.getCity());
            }
                //controlla se è cambiato il primo in classifica
                Hotel best = Hotel.updateBest(hotel.getCity());
            if(best!= null){
                //lo notifica al gruppo multicast
                updateBestHotel.send(best.getCity(),best);
            }
            return review;
        } else {
            return null;
        }
    }

    //inizializza HashMap con gli hotel supportati
    public static void initializeReview() {
       List<Hotel> hotels = Hotel.getAllHotels();
       for(Hotel hotel:hotels){
           hotelReview.put(hotel, new CopyOnWriteArrayList<Review>());
       }
    }

    //metodo per la trasformazione da data a stringa
    public static String stringTime(LocalDateTime time) {
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    //metodo per la trasformazione da stringa a data
    public static LocalDateTime dateTime(String dateString) {
        LocalDateTime time = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        return time;
    }
}
