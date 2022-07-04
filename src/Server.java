import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {

    public static void main(String[] arguments)throws Exception {
        Scanner scanner = new Scanner(System.in);
        //Create a socket to listen at port 1234.
        DatagramSocket datagramSocket = new DatagramSocket(1234); //UDP TCP farki
        InetAddress inetAddress = InetAddress.getLocalHost();
        /*
        InetAddress --> This class represents an Internet Protocol (IP) address. serverin ip adresini almak icin kullaniliyor
        getLocalHost() --> Returns the address of the local host.
        */

        byte[] buffer; //data typeini bytea donusturmesi gerekiyor
        //Create a DatagramPacket to receive the data.
        DatagramPacket request, reply;
        /*
        Datagram packets are used to implement a connectionless packet delivery service. Each message is routed from one
        machine to another based solely on information contained within that packet. Multiple packets sent from one
        machine to another might be routed differently, and might arrive in any order. Packet delivery is not guaranteed.
        */
        String clientIncoming; //clienttan gelen
        String serverOutgoing; //serverdan giden

        ArrayList<String> wordsMirror = new ArrayList(); //kelimeleri burda store ediyoruz

        System.out.println("\nServer is waiting for Client for connection....");
        System.out.println("\nYou will have 15 seconds to respond to every word." +
                "\nIf you don't answer during this time,you will lose even if you get the right answer.");



        while (true) {
            buffer = new byte[65535];
            request = new DatagramPacket(buffer, buffer.length);
            /*
                DatagramPacket(byte[] buf, int length, InetAddress address, int port)
                Constructs a datagram packet for sending packets of length length to the specified port number on the
                specified host.
            */
            datagramSocket.receive(request);
            /*
                DatagramSocket() Constructs a datagram socket and binds it to any available port on the local host machine.

            */
            String receive = new String(request.getData());

            byte[] data = new byte[request.getLength()];
            System.arraycopy(request.getData(), request.getOffset(), data, 0, request.getLength());
            //65535 bytelik datada olusan bosluklari telafi ediyor.
            clientIncoming = new String(data);

            if (clientIncoming.equals("\nClient entered a wrong word. GAME OVER. YOU WON!") ||
                    clientIncoming.equals("\nClient couldn't respond on time. GAME OVER. YOU WON!")) {
                System.out.println(clientIncoming);
                System.exit(0);
            }

            wordsMirror.add(clientIncoming);

            System.out.println("\nThe word that comes from Client = " + receive +
                    "\nEnter a word that will send to Client:");


            double firstTime = System.currentTimeMillis();
            serverOutgoing = scanner.nextLine();
            double lastTime = System.currentTimeMillis();
            double respondTime = lastTime - firstTime;
            double time = respondTime / 1000;

            if (time > 15.0) {
                System.out.println("\nYou respond in " + time + " seconds. GAME OVER. YOU LOST!");
                String timeOut = "\nServer couldn't respond on time. GAME OVER. YOU WON!";
                buffer = timeOut.getBytes();
                reply = new DatagramPacket(buffer, buffer.length, inetAddress , request.getPort());
                /*
                DatagramPacket(byte[] buf, int length, InetAddress address, int port)
                Constructs a datagram packet for sending packets of length length to the specified port number on the
                specified host.
                */
                datagramSocket.send(reply);
                System.exit(0);
            }
            //Rewrite the input if it met any if statement.
            while (true) {
                if (serverOutgoing.isEmpty() || serverOutgoing.contains(" ")) {
                    System.out.println("You have entered an empty string. Please enter another word.");
                    serverOutgoing = scanner.nextLine();
                } else if (serverOutgoing.length() < 2) {
                    System.out.println("You word must at least 2 character long. Please enter another word.");
                    serverOutgoing = scanner.nextLine();
                } else if (serverOutgoing.matches(".*\\d.*")) {
                    System.out.println("Your word must not contain any numbers.");
                    serverOutgoing = scanner.nextLine();
                } else if (serverOutgoing.matches(".*[A-Z].*")) {
                    System.out.println("Your word must be in lowercase.");
                    serverOutgoing = scanner.nextLine();
                } else if (wordsMirror.contains(serverOutgoing)) {
                    System.out.println("This word has been used before! Please enter another word.");
                    serverOutgoing = scanner.nextLine();
                } else
                    break;
            }


            String endOfWord = clientIncoming.substring(clientIncoming.length() - 2); //kelimenin son iki harfi
            String beginningOfWord = serverOutgoing.substring(0, 2); //kelimenin ilk iki harfi

            if (endOfWord.equals(beginningOfWord)) {
                System.out.println("\nYou entered a correct word. Waiting for Client's response.");
                buffer = serverOutgoing.getBytes();
                reply = new DatagramPacket(buffer, buffer.length, request.getAddress(), request.getPort());
                /*
                DatagramPacket(byte[] buf, int length, InetAddress address, int port)
                Constructs a datagram packet for sending packets of length length to the specified port number on the
                specified host.
                */
                datagramSocket.send(reply);
                wordsMirror.add(serverOutgoing);
            } else {
                System.out.println("\nYou entered a wrong word. GAME OVER. YOU LOST!");
                String message = "\nServer entered a wrong word. GAME OVER. YOU WON!";
                buffer = message.getBytes();
                reply = new DatagramPacket(buffer, buffer.length, request.getAddress(), request.getPort());
                /*
                DatagramPacket(byte[] buf, int length, InetAddress address, int port)
                Constructs a datagram packet for sending packets of length length to the specified port number on the
                specified host.
                */
                datagramSocket.send(reply);
                System.exit(0);
            }
        }
    }
}