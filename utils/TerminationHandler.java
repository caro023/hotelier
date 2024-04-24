package utils;
import java.io.*;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.*;

public class TerminationHandler extends Thread {
    private int maxDelay;
    private ExecutorService pool;
    private ServerSocket serverSocket;
    public TerminationHandler(int maxDelay, ExecutorService pool, ServerSocket serverSocket)
        {
            this.maxDelay = maxDelay;
            this.pool = pool;
            this.serverSocket = serverSocket;
        }
        public void run() {
            // Avvio la procedura di terminazione del server.
            System.out.println("[SERVER] Avvio terminazione...");
            // Chiudo la ServerSocket in modo tale da non accettare piu' nuove richieste.
            try {serverSocket.close();}
                catch (IOException e) {
                     System.err.printf("[SERVER] Errore: %s\n", e.getMessage());
                }
                // Faccio terminare il pool di thread.
                pool.shutdown();
                try {
                    if (!pool.awaitTermination(maxDelay, TimeUnit.MILLISECONDS))
                    pool.shutdownNow();
            }
            catch (InterruptedException e) {pool.shutdownNow();}
                 System.out.println("[SERVER] Terminato.");
    }
}