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

public class Review {
    private double rate;
    private double[] ratings;
    private final String date;
    private final Hotel hotel;
    private final User user;
    private static final ConcurrentHashMap<Hotel, CopyOnWriteArrayList<Review>> hotelReview = new ConcurrentHashMap<Hotel, CopyOnWriteArrayList<Review>>();
    private static Integer MIN_DAYS_BETWEEN_REVIEWS = null;

    public Review(Hotel hotel, double rate, double[] ratings, String date, User user) {
        this.hotel = hotel;
        this.rate = rate;
        this.ratings = ratings;
        this.date = date;
        this.user = user;
    }

    public static void setDayReview(Integer min){
        MIN_DAYS_BETWEEN_REVIEWS = min;
    }

    public void setReview(){
        Hotel hotel = this.getHotel();
        if (hotelReview.containsKey(hotel)) {
            CopyOnWriteArrayList<Review> reviews = hotelReview.get(hotel);
            reviews.add(this);
        } else {
            System.out.println("Hotel not supported: " + hotel);
        }
    }

    public static void initializeHotelScore() {
        LocalDateTime now = LocalDateTime.now();
        for (Map.Entry<Hotel, CopyOnWriteArrayList<Review>> entry : hotelReview.entrySet()) {
            Hotel hotel = entry.getKey();
            CopyOnWriteArrayList<Review> reviews = entry.getValue();
            int totalReviews = reviews.size();
            hotel.setTotalVote(totalReviews);
            //variabile per il rate medio dell'hotel
            double totalRate = 0.0;
            //variabile per la media dei giorni di distanza tra oggi e le recensioni
            long totalDay = 0;
            //varibaile per la media dei punteggi singoli
            double[] ratings = new double[4];
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
                User user = review.getUser();
                if((User.getUser(user.getUsername()))!=null){
                    user.addBadge();
                }
            }
            if(totalReviews > 0) {
                hotel.setRate(totalRate);
                LocalDateTime date = now.minusDays(totalDay/totalReviews);
                hotel.setTimeReview(date);
                for(Integer i = 0; i < 4; i++) {
                    ratings[i] /= totalReviews;
                }
                hotel.setRatings(ratings);
                calculateHotelScore(hotel, totalRate/totalReviews,ratings,totalDay);
            }
        }
    }

    public static void calculateHotelScore(Hotel hotel, double rate, double[] ratings, long day) {
        // Pesi per ciascun parametro
        double weightRate = 0.5;
        double weightRatings = 0.4;
        double weightDaysDifference = -0.07; // Peso negativo per penalizzare recensioni vecchie
        double weightTotalReviews = 0.05;
        //normalizzo il rate da 0 a 100
        rate *= 20;
        double totRatings = 0;
        for(double rating : ratings){
            totRatings += rating;
        }
        double totalVote = hotel.getTotalVote();
        //normalizzo ratings da 0 a 100
        totRatings *= 5;
        // Calcolo finale del punteggio
        double finalScore = (weightRate * rate) +
                (weightRatings * totRatings) +
                (weightDaysDifference * day) +
                (weightTotalReviews * totalVote);
        //evitare punteggi negativi
        if(finalScore<0){finalScore=0;}
        hotel.setTotalScore(finalScore);
    }


    public static Review addReview(Hotel hotel, User user,double rate,double [] ratings) throws IOException {
        LocalDateTime now = LocalDateTime.now();
        String date = stringTime(now);
        if (hotelReview.containsKey(hotel)) {
            CopyOnWriteArrayList<Review> reviews = hotelReview.get(hotel);
            for (Review review : reviews) {
                if(review.getDate()!=null){
                    if (review.getUser().equals(user) && ChronoUnit.DAYS.between(dateTime(review.getDate()), now) < MIN_DAYS_BETWEEN_REVIEWS) {
                        System.out.println("Si può inserire solo una recensione ogni " + MIN_DAYS_BETWEEN_REVIEWS + " days.");

                        return null;
                    }
                }
            }
            Review review = new Review(hotel, rate, ratings, date, user);
            reviews.add(review);
            hotel.updateVote();
            hotel.addRate(rate);
            hotel.addRatings(ratings);
            LocalDateTime dateTime =hotel.getTimeReview();
            if(dateTime!= null){
            long day = ChronoUnit.DAYS.between(dateTime, now);
            day = day*hotel.getTotalVote()/(hotel.getTotalVote()+1);
            calculateHotelScore(hotel,rate,ratings,day);
            LocalDateTime avgDate = now.minusDays(day);
            hotel.setTimeReview(avgDate);
            }else {
                calculateHotelScore(hotel,rate,ratings,0);
                hotel.setTimeReview(now);
            }
            Hotel.sortHotel(hotel.getCity());
            Hotel best = Hotel.updateBest(hotel.getCity());
            if(best!= null){
                updateBestHotel.send(best.getCity(),best);
            }
            return review;
        } else {
           // System.out.println("Hotel not supported: " + hotel);
            return null;
        }
    }

    //metti  o tutti private o tutti public
    public Hotel getHotel(){
        return this.hotel;
    }

    private User getUser(){
        return this.user;
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


    public static List<Review> getAllReviews() {
        List<Review> allReview = new ArrayList<>();
        for (CopyOnWriteArrayList<Review> reviewHotel : hotelReview.values()) {
            allReview.addAll(reviewHotel);
        }
        return allReview;
    }

    public static void initializeReview() {
       List<Hotel> hotels = Hotel.getAllHotels();
       for(Hotel hotel:hotels){
           hotelReview.put(hotel, new CopyOnWriteArrayList<Review>());
       }
    }

    public static String stringTime(LocalDateTime time) {
        // Qui puoi utilizzare qualsiasi formato desiderato per la rappresentazione della data
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public static LocalDateTime dateTime(String dateString) {
        LocalDateTime time = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        return time;
    }

}
