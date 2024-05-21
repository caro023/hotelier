package utils;

import java.io.BufferedReader;
import java.io.FileReader;
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
    private static final long MIN_DAYS_BETWEEN_REVIEWS = 30;

    public Review(Hotel hotel, double rate, double[] ratings, String date, User user) {
        this.hotel = hotel;
        this.rate = rate;
        this.ratings = ratings;
        this.date = date;
        this.user = user;
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

    public static Review addReview(Hotel hotel, User user,double rate,double [] ratings){
        LocalDateTime now = LocalDateTime.now();
        String date = stringTime(now);
        if (hotelReview.containsKey(hotel)) {
            CopyOnWriteArrayList<Review> reviews = hotelReview.get(hotel);
            for (Review review : reviews) {
                if (review.getUser().equals(user) && ChronoUnit.DAYS.between(dateTime(review.getDate()), now) < MIN_DAYS_BETWEEN_REVIEWS) {
                   // System.out.println("Si può inserire solo una recensione ogni " + MIN_DAYS_BETWEEN_REVIEWS + " days.");
                    return null;
                }
            }
            Review review = new Review(hotel, rate, ratings, date, user);
            reviews.add(review);
            hotel.setRate(rate);
            hotel.setRatings(ratings);
            return review;
        } else {
           // System.out.println("Hotel not supported: " + hotel);
            return null;
        }
    }

    public Hotel getHotel(){
        return this.hotel;
    }

    private User getUser(){
        return this.user;
    }

    public String getDate() {
        return date;
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
