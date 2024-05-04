package utils;
import utils.Hotel;
import utils.User;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import com.google.gson.*;


public class JsonHandler {

    private ConcurrentHashMap< String, CopyOnWriteArrayList<Hotel>> hotels;
    private static String fileHotel;
    private static String fileUser;
    
    public JsonHandler(String fileHotel, String fileUser){
        this.hotels = new ConcurrentHashMap<>();
        JsonHandler.fileHotel = fileHotel;
        JsonHandler.fileUser = fileUser;
        hotelReader();
    }

    //mettere hotel e user reader nella stessa funzione

        private void hotelReader(){
            try{
                FileReader reader = new FileReader(fileHotel);
                Gson gson = new Gson();

                for(Hotel hotel : gson.fromJson(reader, Hotel[].class)){
                    this.addHotel(hotel);
                }

            }catch(Exception e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }Gson gson = new Gson();
            Hotel hotels = gson.fromJson();
        }

        public Hotel getHotel(String name, String city){
            if(this.hotels.containsKey(city)){
                CopyOnWriteArrayList<Hotel> cityHotel = hotels.getOrDefault(city,null);
                for(Hotel hotel : cityHotel){
                    if(hotel.getName().equalsIgnoreCase(name)) return hotel;
                }
            }
            return null;
        }

        public String[] getListOfCity(){
            return this.hotels.keySet().toArray(new String[0]);
        }
        //public get User

        //mettere update user e hotel nella stessa funzione
        //definizione di get alla hotels
        public void updateHotelInfo(){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            File jsonFile  = new File(fileHotel);

            try(OutputStream outputStream = new FileOutputStream(jsonFile)){

                outputStream.write(gson.toJson(this.getAllHotels()).getBytes());
                outputStream.flush();

            }catch(IOException e){e.printStackTrace();}
        }

        //listof users è una hashmap con tutti gli utenti
        public static synchronized void updateFileUser(ConcurrentHashMap<String,User> user){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File jsonFile  = new File(fileUser);

        try(OutputStream outputStream = new FileOutputStream(jsonFile)){

            outputStream.write(gson.toJson(user.values().toArray()).getBytes());
            outputStream.flush();

        }catch(IOException e){e.printStackTrace();}
    }
}
