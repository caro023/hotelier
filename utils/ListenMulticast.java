package utils;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class ListenMulticast extends Thread {
    private final String multicastIP;
    private final int multicastPort;
    private MulticastSocket socket;
    private InetAddress group;
    private boolean listening = true;

    public ListenMulticast(String multicastIP, int multicastPort) {
        this.multicastIP = multicastIP;
        this.multicastPort = multicastPort;
        this.listening = true;
    }

    public void run() {
        joinGroup();
        byte[] buffer = new byte[1024];
        //riceve e gestisce messaggi multicast finché l'utente è loggato
        while (this.listening && !Thread.currentThread().isInterrupted()) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.US_ASCII);
                System.out.println(message);
            } catch (IOException e) {
                if(!("Socket closed").equals(e.getMessage())) {
                    System.err.println("Errore durante la ricezione dei messaggi multicast: " + e.getMessage());
               }
            }
        }
    }

    private void joinGroup() {
        try {
            this.socket = new MulticastSocket(multicastPort);
            this.group = InetAddress.getByName(multicastIP);
            this.socket.joinGroup(new InetSocketAddress(group, multicastPort), NetworkInterface.getByInetAddress(group));
        } catch (IOException e) {
            System.err.println("Errore durante l'unione al gruppo multicast: " + e.getMessage());
        }
    }

    public void leaveGroup() {
        try {
            this.listening=false;
            this.socket.leaveGroup(new InetSocketAddress(group, multicastPort), NetworkInterface.getByInetAddress(group));
            this.socket.close();
        } catch (IOException e) {

            System.err.println("Errore durante l'uscita dal gruppo multicast: " + e.getMessage());
        }
    }
}
