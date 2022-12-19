import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Server {
    public static void main(String[] args) throws IOException {

        // Port the Server/ Client connection
        args = new String[]{"30121"};
        int portNumber = Integer.parseInt(args[0]);

        //port error
        if (args.length != 1) {
            System.err.println("Usage: java EchoServer <port number>");
            System.exit(1);
        }

        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
             Socket clientSocket = serverSocket.accept();
             PrintWriter responseWriter = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader requestReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            System.out.println("Server created connection.\n");
            String[] packets = extractMessages();
            intialPacketSend(responseWriter, packets);
            responseWriter.println("Last Packet. Packet #: " + packets.length);
            sendMissingPackets(responseWriter, requestReader, packets);
        } catch (IOException e) {
            System.out.println(
                    "Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }

    }

    /**
     * sends over the client requested packets to the client
     * by reading the sting of requested pocket numbers
     * then sending over those packets (with the number attached) tp the client - with 80% probability
     * @param responseWriter1 --- writer to the client
     * @param requestReader1 --- reader from the client
     * @param packets --- String array full of the message in separate words
     * @throws IOException
     */
    private static void sendMissingPackets(PrintWriter responseWriter1, BufferedReader requestReader1, String[] packets) throws IOException {
        String userRequestMissingPackets;
        while ((userRequestMissingPackets = requestReader1.readLine()) != null) {
            String[] request = userRequestMissingPackets.split(" ");
            for (String s : request) {
                if (Math.random() < .80) {
                    System.out.println("Sending missing packet #: \" " + s + "\"");
                    responseWriter1.println(packets[Integer.parseInt(s)] + " Packet #: " + s);
                }
            }
            responseWriter1.println("Last Packet. Packet #: " + packets.length);
        }
    }

    /**
     * sends over the message by packaging each word separately with a packet number
     * messages are sent with 80% probability
     * @param responseWriter1 --- writer to the client
     * @param packets --- String array full of the message in separate words
     */
    private static void intialPacketSend(PrintWriter responseWriter1, String[] packets) {
        for (int i = 0; i < packets.length; i++) {
            if (Math.random() < .80) {
                System.out.println("Sending: \" " + packets[i] + "\"");
                responseWriter1.println(packets[i] + " Packet #: " + i);
            }
        }
    }

    /**
     * spleits the message string into an array of separate words
     * @return - a String array of teh message in separate words
     */
    private static String[] extractMessages() {
        String message = "When Mr. Bilbo Baggins of Bag End announced that he would shortly be celebrating his eleventy-first birthday with a party of special magnificence, there was much talk and excitement in Hobbiton.";
        String[] packets = message.split(" ");
        return packets;
    }
}
