package utils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Hotel {
    
    private int id;
    private String name;
    private String description;
    private String city;
    private String phone;
    private String[] services;
    private double rate;
    private Map<String, Double> ratings;
    private static transient ConcurrentHashMap< String, CopyOnWriteArrayList<Hotel>> hotels = new ConcurrentHashMap<>();
    private transient int totalScore;
    private transient int totalVote;
    private transient LocalDateTime lastTimeVote;
    private transient LocalDateTime avgTimeVote;

    public int getId() {
        return id;
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

    public void setRate(double rate) {
        this.rate = rate;
    }

    // Getter e setter per le valutazioni
    public Map<String, Double> getRatings() {
        return ratings;
    }

    public void setRatings(Map<String, Double> ratings) {
        this.ratings = ratings;
    }

    public Hotel searchHotel(String nome, String città){
            if(Hotel.hotels.containsKey(città)){
                CopyOnWriteArrayList<Hotel> cittàHotel = hotels.getOrDefault(città,null);
                for(Hotel hotel : cittàHotel){
                    if(hotel.getName().equalsIgnoreCase(nome)) return hotel;
                }
            }
            return null;
        }

    public CopyOnWriteArrayList<Hotel> searchAllHotels(String città){
        return Hotel.hotels.getOrDefault(città, null);
    }

    public void setHotel(){
        String city = this.getCity();
        hotels.computeIfAbsent( city, c -> new CopyOnWriteArrayList<Hotel>());
        CopyOnWriteArrayList<Hotel> cityHotels = Hotel.hotels.get(city);
        cityHotels.add(this);
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

}
