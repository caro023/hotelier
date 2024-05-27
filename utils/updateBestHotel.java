package utils;

import java.io.IOException;
import java.net.*;

public class updateBestHotel implements Runnable {
    private volatile boolean run = true;
    private String multicastIP;
    private static int multicastPort;
    private static MulticastSocket socket;
    private static InetAddress group;

    public updateBestHotel (String multicastIP, int multicastPort) throws UnknownHostException {
        this.multicastIP = multicastIP;
        this.multicastPort = multicastPort;
        this.socket = null;
        this.group = InetAddress.getByName(multicastIP);;
    }

    @Override
    public void run() {
        while(run) {
            for (String city : Hotel.getAllCity()) {
                Hotel hotel = Hotel.updateBest(city);
                if (hotel != null) {
                    try {
                        String message = "Nuovo hotel migliore a " + city + ": " + hotel.getName();
                        sendMessage(message);
                    } catch (IOException e) {
                        System.err.println("Errore nell'invio del messaggio multicast: " + e.getMessage());
                    }
                }
            }
        }
    }
    public static void sendMessage(String message) throws IOException {
        DatagramSocket dataSocket = new DatagramSocket();
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, multicastPort);
        dataSocket.send(packet);
        dataSocket.close();
    }

    public static void send(String city, Hotel hotel) throws IOException {
        String message = "Nuovo hotel migliore a " + city + ": " + hotel.getName();
        sendMessage(message);
    }

    public void stop(){
        this.run = false;
    }
}

