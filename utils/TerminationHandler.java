package utils;

import java.io.*;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.*;

public class TerminationHandler extends Thread {
    private final int maxDelay;
    private final ExecutorService pool;
    private final ServerSocket serverSocket;
    private final JsonHandler JsonHandler;
    private final ScheduledExecutorService update;
    public TerminationHandler(int maxDelay, ExecutorService pool, ServerSocket serverSocket, JsonHandler JsonHandler, ScheduledExecutorService update)
        {
            this.maxDelay = maxDelay;
            this.pool = pool;
            this.serverSocket = serverSocket;
            this.JsonHandler = JsonHandler;
            this.update = update;
        }
        public void run() {
            //avvia la procedura di terminazione del server
            System.out.println("[SERVER] Avvio terminazione...");
            //chiusura socket
            try {serverSocket.close();}
                catch (IOException e) {
                     System.err.printf("[SERVER] Errore: %s\n", e.getMessage());
                }
                //terminare il pool
                pool.shutdown();
                //termina lo scheduler
                update.shutdown();
                try {
                    if (!pool.awaitTermination(maxDelay, TimeUnit.MILLISECONDS)) {
                        pool.shutdownNow();
                    }
                    if (!update.awaitTermination(maxDelay, TimeUnit.MILLISECONDS)) {
                        update.shutdownNow();
                    }
            }
            catch (InterruptedException e) {
                    pool.shutdownNow();
                    update.shutdownNow();
                }

                System.out.println("[SERVER] Terminato.");
                //serializza le informazioni presenti di hotel, utenti e recensioni
                this.JsonHandler.infoWriter("hotel");
                List<User> all = User.getAllUsers();
                for(User u: all){
                    //forza il logout
                    if(u.isLogged()){
                        u.logout();
                    }
                }
                this.JsonHandler.infoWriter("user");
                this.JsonHandler.infoWriter("review");
    }
}