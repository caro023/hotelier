package utils;
import java.net.*;

/*
per il multicast:
fare join, leave e recezione nello stesso thread per il client
leave si può fare guardando se l'utente è loggato. Nel thread dare l'utente

fare il sender in un altro thread per il server
si aggiorna il ranking quando si inserisce una recensione(???)-->c'è un metodo per capire quando cambia il primo?
oppure si aggiorna ogni tot

 */
/*
public class Multicast {
    public static void main (String args[]) {
        try
        {DatagramSocket clientsocket = new DatagramSocket();
            byte[] buffer="1234567890abcdefghijklmnopqrstuvwxyz".getBytes("US-
                    ASCII");
                    InetAddress address = InetAddress.getByName("Localhost");
            for (int i = buffer.length; i >0; i--) {
                DatagramPacket mypacket = new DatagramPacket(buffer,i,address,
                        40000);
                clientSocket.send(mypacket);
                Thread.sleep(200); }
            System.exit(0);}
        catch (Exception e) {e.printStackTrace();}}}
public void join(){
    try {
        this.multicastSocket = new MulticastSocket(this.multicastPort);
        this.group= InetAddress.getByName(this.multicastIP);
        this.multicastSocket.joinGroup(this.group);
    }catch (IOException ex) {System.err.println("=== errore joinMulticast ===");}
}

public void leave(){
    try{
        this.multicastSocket.leaveGroup(this.group);
        this.continueListening = false;
    }catch(Exception ex){System.err.println("=== errore leaveMulticast ===");}
}
public ReturnCode sendNotification(String msg) throws IOException{
    DatagramSocket socket = new DatagramSocket();
    InetAddress group = InetAddress.getByName(this.multicastIP);
    byte[] buf = msg.getBytes();

    DatagramPacket packet = new DatagramPacket(buf, buf.length, group, this.multicastPort);
    socket.send(packet);
    socket.close();
    return ReturnCode.SUCCESS;
}

}
*/