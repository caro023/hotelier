package utils;
import java.io.*;
import java.util.List;
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
        userReader();
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

    private void userReader(){
        try{
            FileReader reader = new FileReader(fileUser);
            Gson gson = new Gson();
            // Hotel hotels = gson.fromJson();
            for(User user : gson.fromJson(reader, User[].class)){
                user.setUser();
            }

        }catch(Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

 /*   public void hotelWriter(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File jsonFile  = new File(fileHotel);

        try(OutputStream outputStream = new FileOutputStream(jsonFile)){
            List<Hotel> allHotels = Hotel.getAllHotels();
            String json = gson.toJson(allHotels);
            outputStream.write(json.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
           }

    }*/
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

        try(OutputStream outputStream = new FileOutputStream(jsonFile)){
            String json = gson.toJson(all);
            outputStream.write(json.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
        //public get User

        //listof users è una hashmap con tutti gli utenti
        public static synchronized void updateFileUser(ConcurrentHashMap<String,User> user){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File jsonFile = new File(fileUser);

       try(OutputStream outputStream = new FileOutputStream(jsonFile)){
         outputStream.write(gson.toJson(user.values().toArray()).getBytes());
         outputStream.flush();

      }catch(IOException e){e.printStackTrace();}
   }
}
