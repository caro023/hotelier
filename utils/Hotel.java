package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

//classe per rappresentare gli hotels
public class Hotel {
    //attributi relativi a un hotel
    private int id;
    private String name;
    private String description;
    private String city;
    private String phone;
    private String[] services;
    private transient double rate = 0;
    private transient double totalScore;
    private transient int totalVote;
    private transient LocalDateTime timeReview = null;
    private transient ConcurrentHashMap<String, Double> ratings = new ConcurrentHashMap<>();
    //HashMap statica con tutti gli hotels divisi per città
    private static final ConcurrentHashMap< String, CopyOnWriteArrayList<Hotel>> hotels = new ConcurrentHashMap<>();
    //HashMap statica con i migliori hotels per città
    private static final HashMap<String,Hotel> bestHotel = new HashMap<>();

    //metodi getter e setter
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String[] getServices() {
        return services;
    }

    public void setServices(String[] services) {
        this.services = services;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate){
        this.rate= rate/totalVote;
    }

    //ricalcola la media aggiungendo un punteggio
    public void addRate(double rate) {
        //quando è chiamato totalVote è diverso da 0
        this.rate = (((this.rate*(this.totalVote-1))+rate)/this.totalVote) ;
    }

    public Map<String, Double> getRatings() {
        return ratings;
    }

    public void setRatings(double[] ratings){
        this.ratings.put("cleaning", ratings[0]);
        this.ratings.put("service", ratings[1]);
        this.ratings.put("position", ratings[2]);
        this.ratings.put("quality", ratings[3]);
    }

    //ricalcola la media aggiungendo dei punteggi
    public void addRatings(double[] singleScores) {
        this.ratings.put("cleaning", updateAverage(this.ratings.get("cleaning"), singleScores[0]));
        this.ratings.put("service", updateAverage(this.ratings.get("position"), singleScores[1]));
        this.ratings.put("position", updateAverage(this.ratings.get("service"), singleScores[2]));
        this.ratings.put("quality", updateAverage(this.ratings.get("quality"), singleScores[3]));
    }

    //inizializza i punteggi a 0
    private void initializeRatings() {
        this.ratings.put("cleaning", 0.0);
        this.ratings.put("position", 0.0);
        this.ratings.put("service", 0.0);
        this.ratings.put("quality", 0.0);
    }

    private double updateAverage(double currentAverage, double newScore) {
        return (double) Math.round((((currentAverage*(this.totalVote-1)) + newScore) / this.totalVote) * 100) /100;
    }

    //restituisce tutti gli hotel presenti
    public static List<Hotel> getAllHotels() {
        List<Hotel> allHotels = new ArrayList<>();
        for (CopyOnWriteArrayList<Hotel> cityHotels : hotels.values()) {
            allHotels.addAll(cityHotels);
        }
        return allHotels;
    }

    public static Hotel searchHotel(String nome,String città){
        if(Hotel.hotels.containsKey(città)){
                CopyOnWriteArrayList<Hotel> cittàHotel = hotels.getOrDefault(città, null);
                for (Hotel hotel : cittàHotel) {
                    if (hotel.getName().equalsIgnoreCase(nome)){
                        return hotel;
                    }
                }
            }
        return null;

    }

    public static CopyOnWriteArrayList<Hotel> searchAllHotels(String città){
        return Hotel.hotels.getOrDefault(città, null);
    }

    //inserisce un hotel nell'HashMap
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

    //restituisce le città in cui sono presenti hotels
    public static List<String> getAllCity(){
        List<String> allCity = new ArrayList<>();
        allCity.addAll(hotels.keySet());
        return allCity;
    }

    //inizializza l'HashMap con colo le città supportate
    public static void initializeHotels(String listCity) {
        try (BufferedReader br = new BufferedReader(new FileReader(listCity))) {
            String city;
            while ((city = br.readLine()) != null) {
                hotels.put(city.trim().toLowerCase(), new CopyOnWriteArrayList<Hotel>());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //ordina gli hotel in una città
    public static void sortHotel(String city){
        city = city.toLowerCase();
        CopyOnWriteArrayList<Hotel> hotelsInCity = Hotel.hotels.getOrDefault(city, null);
        if (hotelsInCity != null) {
            hotelsInCity.sort(Comparator.comparingDouble(Hotel::getTotalScore).reversed());
        }
    }

    //inizializza i migliori hotel dopo l'avvio
    public static void initializeBest() {
        for (Map.Entry<String, CopyOnWriteArrayList<Hotel>> entry : hotels.entrySet()) {
            String city = entry.getKey();
            CopyOnWriteArrayList<Hotel> cityHotels = entry.getValue();
            if (!cityHotels.isEmpty()) {
                bestHotel.put(city, cityHotels.getFirst());
            }
            else{
                bestHotel.put(city,null);
            }
        }
    }

    //aggiorna il migliore hotel di una città
    public static Hotel updateBest(String city) {
        city = city.trim().toLowerCase();
        if (!hotels.containsKey(city)) {
            System.out.println("City not supported: " + city);
            return null;
        }
        Hotel newHotel = Hotel.hotels.get(city).getFirst();
        synchronized (bestHotel) {
            Hotel currentBestHotel = bestHotel.get(city);
            if (currentBestHotel == null || !currentBestHotel.equals(newHotel)) {
                bestHotel.put(city, newHotel);
                return newHotel;
            }
            return null;
        }
    }

    //override per creare una stringa di un'istanza di hotel
     @Override
    public String toString(){
        String result = String.format("%s\n%s\nTelefono: %s\nGlobal Score: %.2f\nServizi Offerti:\n",this.getName(),this.getDescription(),this.getPhone(),this.getRate());
        
        for(String service : this.getServices()){
            result = result + "\t" + service + "\n";
        }

        for (Map.Entry<String,Double>entry : this.getRatings().entrySet())
            result=result + String.format("%s: %.2f\n", entry.getKey(), entry.getValue());
        return result;
    }

    //override dei metodi per consentire il confronto per nome e città
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
