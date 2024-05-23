package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Hotel {
    
    private int id;
    private String name;
    private String description;
    private String city;
    private String phone;
    private String[] services;
    private transient double rate = 0;
    private transient Map<String, Double> ratings = new HashMap<>();
    private static final transient ConcurrentHashMap< String, CopyOnWriteArrayList<Hotel>> hotels = new ConcurrentHashMap<>();
    private transient double totalScore;
    private transient int totalVote;
    private transient LocalDateTime timeReview;

    public int getId() {
        return id;
    }
    public void setTotalScore(double score) {
        this.totalScore = score;
    }

    public double getTotalScore() {
        return this.totalScore;
    }

    public int getTotalVote() {
        return this.totalVote;
    }

    public void setTotalVote(Integer vote){
        this.totalVote = vote;
    }

    public LocalDateTime getTimeReview(){
        return this.timeReview;
    }

    public void setTimeReview(LocalDateTime time){
       this.timeReview = time;
    }

    public void updateVote() {
        this.totalVote = this.totalVote + 1;
    }

    // Getter e setter per il nome
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter e setter per la descrizione
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Getter e setter per la città
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    // Getter e setter per il numero di telefono
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // Getter e setter per i servizi
    public String[] getServices() {
        return services;
    }

    public void setServices(String[] services) {
        this.services = services;
    }

    // Getter e setter per il tasso
    public double getRate() {
        return rate;
    }

    public void setRate(double rate){
        this.rate= rate/totalVote;
    }

    public void addRate(double rate) {
        //controlla che quanod viene chiamato totalVote è diverso da 0
        this.rate = (((this.rate*this.totalVote-1)+rate)/this.totalVote) ;
    }

        // Getter e setter per le valutazioni
    public Map<String, Double> getRatings() {
        return ratings;
    }

    public void setRatings(double[] ratings){
        this.ratings.put("cleaning", ratings[0]);
        this.ratings.put("service", ratings[1]);
        this.ratings.put("position", ratings[2]);
        this.ratings.put("quality", ratings[3]);
    }

    public void addRatings(double[] singleScores) {
        this.updateVote();
        this.ratings.put("cleaning", updateAverage(this.ratings.get("cleaning"), singleScores[0]));
        this.ratings.put("service", updateAverage(this.ratings.get("position"), singleScores[1]));
        this.ratings.put("position", updateAverage(this.ratings.get("service"), singleScores[2]));
        this.ratings.put("quality", updateAverage(this.ratings.get("quality"), singleScores[3]));
    }

    private double updateAverage(double currentAverage, double newScore) {
        return (double) Math.round(((currentAverage * (this.totalVote - 1) + newScore) / totalVote) * 100) /100;
    }

    public static List<Hotel> getAllHotels() {
        List<Hotel> allHotels = new ArrayList<>();
        for (CopyOnWriteArrayList<Hotel> cityHotels : hotels.values()) {
            allHotels.addAll(cityHotels);
        }
        return allHotels;
    }

    //non è case sensitive, si rinuncia all'ottimizzazione non usando containsekey
    public static Hotel searchHotel(String nome,String città){
        if(Hotel.hotels.containsKey(città)){
                CopyOnWriteArrayList<Hotel> cittàHotel = hotels.getOrDefault(città, null);
                for (Hotel hotel : cittàHotel) {
                    if (hotel.getName().equalsIgnoreCase(nome)){
                        return hotel;
                    }
                }
                //return searchAllHtel(città) e converti in stringa
            }
        return null;

    }

    public static CopyOnWriteArrayList<Hotel> searchAllHotels(String città){
        return Hotel.hotels.getOrDefault(città, null);
    }

    public void setHotel(){
        String city = this.getCity().toLowerCase();

        if (hotels.containsKey(city)) {
            CopyOnWriteArrayList<Hotel> cityHotels = hotels.get(city);
            this.initializeRatings();
            cityHotels.add(this);
        } else {
            System.out.println("City not supported: " + city);
        }
    }

    private void initializeRatings() {
        this.ratings.put("cleaning", 0.0);
        this.ratings.put("position", 0.0);
        this.ratings.put("service", 0.0);
        this.ratings.put("quality", 0.0);
    }

    public static List<String> getAllCity(){
        List<String> allCity = new ArrayList<>();
        allCity.addAll(hotels.keySet());
        return allCity;
    }

    public static void initializeHotels() {
        String listCity = "city.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(listCity))) {
            String city;
            while ((city = br.readLine()) != null) {
                hotels.put(city.trim().toLowerCase(), new CopyOnWriteArrayList<Hotel>());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sortHotel(String city){
        CopyOnWriteArrayList<Hotel> hotelsInCity = Hotel.hotels.getOrDefault(city, null);
        if (hotelsInCity != null) {
            hotelsInCity.sort(Comparator.comparingDouble(Hotel::getTotalScore).reversed());
        }
    }

    /**
     * @Override
     * Metodo per la costruzione di una versione pretty del toString
     * @return un pretty print di un istanza di hotel
     */
    public String toString(){
        String result = String.format("%s\n%s\ntelefono: %s\nGlobal Rate: %.2f\nServizi Offerti:\n",this.getName(),this.getDescription(),this.getPhone(),this.getRate());
        
        for(String service : this.getServices()){
            result = result + "\t" + service + "\n";
        }

        for (Map.Entry<String,Double>entry : this.getRatings().entrySet())
            result=result + String.format("%s: %.2f\n", entry.getKey(), entry.getValue());
        return result;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hotel hotel = (Hotel) o;
        return Objects.equals(name, hotel.name) &&
                Objects.equals(city, hotel.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, city);
    }

}
