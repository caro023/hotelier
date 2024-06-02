package utils;

import java.io.IOException;
import java.net.*;

public class updateBestHotel implements Runnable {
    private static int multicastPort;
    private static InetAddress group;

    public updateBestHotel (String multicastIP, int multicastPort) throws UnknownHostException {
        this.multicastPort = multicastPort;
        this.group = InetAddress.getByName(multicastIP);;
    }

    @Override
    public void run() {
        //controlla se è cambiato un hotel primo in classifica per città
            for (String city : Hotel.getAllCity()) {
                Hotel hotel = Hotel.updateBest(city);
                if (hotel != null) {
                    try {
                        send(city, hotel);
                    } catch (IOException e) {
                        System.err.println("Errore nell'invio del messaggio multicast: " + e.getMessage());
                    }
                }
            }
    }

    //crea e invia un messaggio sul gruppo
    public static void sendMessage(String message) throws IOException {
        DatagramSocket dataSocket = new DatagramSocket();
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, multicastPort);
        dataSocket.send(packet);
        dataSocket.close();
    }

    //crea la stringa da mandare
    public static void send(String city, Hotel hotel) throws IOException {
        String message = "Nuovo hotel migliore a " + city + ": " + hotel.getName();
        sendMessage(message);
    }

}

