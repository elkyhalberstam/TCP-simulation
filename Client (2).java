import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Client {
    public static void main(String[] args) throws IOException {

        // Hardcode in IP and Port here if required
        args = new String[]{"127.0.0.1", "30121"};
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
                Socket clientSocket = new Socket(hostName, portNumber);
                PrintWriter requestWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            System.out.println("Established connection to server\n");

            String serverResponse;
            ArrayList<String> nonSortedMessages = new ArrayList<>();
            String missingPackets = "";

            do {
                serverResponse = initialRetrieval(responseReader, nonSortedMessages);
                int lastPacketNumber = Integer.parseInt(serverResponse.split(" Packet #: ")[1]);

                String[] sortedPackets = new String[lastPacketNumber];
                processPackets(nonSortedMessages, sortedPackets);
                missingPackets = "";
                missingPackets = findMissingPackets(missingPackets, sortedPackets);
                completeMessageRetrieval(requestWriter, missingPackets, sortedPackets);// get missing packets, once receive packets, print message
            } while (!missingPackets.equals(""));

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }
    }

    /**
     * gets the list of missing packets
     * if the list is empty it prints out the complete packet
     * else if the list has packets it sends the string of missing packet numbers to the serve
     * @param requestWriter --- file writer to write to the server
     * @param missing --- string of missing packet numbers
     * @param messageInput --- String array of competed message to print
     */
    private static void completeMessageRetrieval(PrintWriter requestWriter, String missing, String[] messageInput) {
        if (missing.equals("")) {
            System.out.println("\nAll packets were received! ");
            System.out.println("Full message from user\n");
            for (String s : messageInput) {
                System.out.print(s + " ");
            }
        } else {
            System.out.println("Requesting missing packets from server");
            System.out.println("Below are the retrieved packets");
            requestWriter.println(missing);
        }
    }

    /**
     * parses though the message list to find the spaces where a packet is missing
     * if there is a missing packet it adds the packet number onto the missing list
     * @param missing --- string of missing packet numbers
     * @param messageInput --- String array of sorted message
     * @return -- missing, this is a string of the current missing packet numbers
     */
    private static String findMissingPackets(String missing, String[] messageInput) {
        for (int i = 0; i < messageInput.length; i++) {
            if (messageInput[i] == null) {
                missing += i + " ";
            }
        }
        return missing;
    }

    /**
     * this sorts the String Arraylist of hte inital packets sent from the server
     * into a Strign array in packet number order
     * @param beforeSorting --- Arraylist of unsorted packets
     * @param messageInput --- String array of sorted message
     */
    private static void processPackets(ArrayList<String> beforeSorting, String[] messageInput) {
        for (String message : beforeSorting) {
            int packetIndex = Integer.parseInt(message.split(" Packet #: ")[1]);
            String packetMessage = message.split(" Packet #: ")[0];
            messageInput[packetIndex] = packetMessage;
        }
    }

    /**
     * while there is a new line/packet coming from the server
     * add the packet to the array list and print it out
     * @param responseReader --- reader from the server
     * @param messages --- Arraylist of the received packets form the server
     * @return -
     * @throws IOException
     */
    private static String initialRetrieval(BufferedReader responseReader, ArrayList<String> messages) throws IOException {
        String serverResponse;
        while (!(serverResponse = responseReader.readLine()).startsWith("Last Packet")) {
            messages.add(serverResponse);
            System.out.println(serverResponse);
        }
        return serverResponse;
    }
}
