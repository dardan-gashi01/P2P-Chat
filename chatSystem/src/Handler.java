import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Handler implements Runnable{

    // All client names, so we can check for duplicates upon registration.
    private static Set<String> names = new HashSet<>();

    // The set of all the print writers for all the clients, used for broadcast.
    private static Set<PrintWriter> writers = new HashSet<>();

    private String name;
    private Socket socket;
    private Scanner in;
    private PrintWriter out;

    /**
     * Constructs a handler thread, squirreling away the socket. All the interesting
     * work is done in the run method. Remember the constructor is called from the
     * server's main method, so this has to be as short as possible.
     */
    public Handler(Socket socket) {
        this.socket = socket;
    }

    /**
     * Services this thread's client by repeatedly requesting a screen name until a
     * unique one has been submitted, then acknowledges the name and registers the
     * output stream for the client in a global set, then repeatedly gets inputs and
     * broadcasts them.
     */
    @Override
    public void run() {
        try {
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);

            // Keep requesting a name until we get a unique one.
            while (true) {
                out.println("SUBMITNAME");
                name = in.nextLine();
                if (name == null) {
                    return;
                }
                synchronized (names) {
                    if (!name.isBlank() && !names.contains(name)) {
                        names.add(name);
                        break;
                    }
                }
            }

            // Now that a successful name has been chosen, add the socket's print writer
            // to the set of all writers so this client can receive broadcast messages.
            // But BEFORE THAT, let everyone else know that the new person has joined!
            out.println("NAMEACCEPTED " + name);
            for (PrintWriter writer : writers) {
                writer.println("MESSAGE " + name + " has joined");
            }
            writers.add(out);

            // Accept messages from this client and broadcast them.
            while (true) {
                String input = in.nextLine();
                if (input.toLowerCase().startsWith("/quit")) {
                    return;
                }
                for (PrintWriter writer : writers) {
                    writer.println("MESSAGE " + name + ": " + input);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (out != null) {
                writers.remove(out);
            }
            if (name != null) {
                names.remove(name);
            }


            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }
}

