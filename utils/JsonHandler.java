package utils;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import com.google.gson.*;


public class JsonHandler {
    private static String fileHotel;
    private static String fileUser;
    
    public JsonHandler(String fileHotel, String fileUser){
        //mettere direttamente qui il nome dei file
        JsonHandler.fileHotel = fileHotel;
        JsonHandler.fileUser = fileUser;
        hotelReader();
    }

    //mettere hotel e user reader nella stessa funzione
        private void hotelReader(){
            try{
                FileReader reader = new FileReader(fileHotel);
                Gson gson = new Gson();
              // Hotel hotels = gson.fromJson();
                for(Hotel hotel : gson.fromJson(reader, Hotel[].class)){
                    hotel.setHotel();
                }

            }catch(Exception e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }    

        //public get User

        //mettere update user e hotel nella stessa funzione
        //definizione di get alla hotels
        public void updateHotelInfo(){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            File jsonFile  = new File(fileHotel);

            try(OutputStream outputStream = new FileOutputStream(jsonFile)){

               // outputStream.write(gson.toJson(this.getAllHotels()).getBytes());
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
