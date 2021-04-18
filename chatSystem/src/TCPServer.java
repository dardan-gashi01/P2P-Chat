import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;


public class TCPServer {
    //setting port to 20111
    private static final int port = 20111;
    /**
     * Initialise a new server. To run the server, call run().
     */
    public TCPServer() {}

    public static void main(String[] args) throws Exception {
        System.out.println("Opening the server socket on port " + port);
        var pool = Executors.newFixedThreadPool(500);
        try (ServerSocket listener = new ServerSocket(port)) {
            while (true) {
                System.out.println("Server waiting for client...");
                //this is used to make threads so we can have more than 1 client
                //therefore we call the Handler class and this is what adds more clients
                pool.execute(new Handler(listener.accept()));
                System.out.println("Client connected!");
            }
        }
    }
}