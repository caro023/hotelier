package utils;
import java.io.*;
import java.util.List;
import com.google.gson.*;

//si crea un'unica istanza
public class JsonHandler {
    private static String fileHotel;
    private static String fileUser;
    private static String fileReview;

    public JsonHandler(String fileHotel, String fileUser, String fileReview, String fileCity){
        JsonHandler.fileHotel = fileHotel;
        JsonHandler.fileUser = fileUser;
        JsonHandler.fileReview = fileReview;
        //deserializza utenti
        userReader();
        //inizializza l'HashMap per gli hotels
        Hotel.initializeHotels(fileCity);
        //deserializza hotels e recensioni
        hotelReader();
    }

        private void hotelReader(){
            try{
                FileReader reader = new FileReader(fileHotel);
                Gson gson = new Gson();
                for(Hotel hotel : gson.fromJson(reader, Hotel[].class)){
                    //inserisce hotel nel sistema
                    hotel.setHotel();
                }
                //inizializza l'HashMap per le recensioni
                Review.initializeReview();
                //deserializza recensioni
                reviewReader();
                //inizializza il punteggio degli hotel
                Review.initializeHotelScore();
                for(String city: Hotel.getAllCity()){
                    //ordina hotel per città
                    Hotel.sortHotel(city);
                }
                //inizializza i migliori hotel per città
                Hotel.initializeBest();
            }catch(Exception e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }

    private void userReader(){
        try{
            FileReader reader = new FileReader(fileUser);
            Gson gson = new Gson();
            for(User user : gson.fromJson(reader, User[].class)){
                //inserisce utenti nel sistema
                user.setUser();
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void reviewReader(){
        try{
            FileReader reader = new FileReader(fileReview);
            Gson gson = new Gson();
            for(Review review : gson.fromJson(reader, Review[].class)){
                //inserisce recensioni nel sistema
                review.setReview();
            }

        }catch(Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    //metodo per la serializzazione
    public void infoWriter(String tipo){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File jsonFile = null;
        List<?> all =  null;

        if(tipo.equals("hotel")){
            jsonFile  = new File(fileHotel);
            all = Hotel.getAllHotels();
        }
        else if(tipo.equals("user")){
            jsonFile  = new File(fileUser);
            all = User.getAllUsers();
        }
        else if(tipo.equals("review")){
            jsonFile  = new File(fileReview);
            all = Review.getAllReviews();
        }

        try(OutputStream outputStream = new FileOutputStream(jsonFile)){
            String json = gson.toJson(all);
            outputStream.write(json.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
