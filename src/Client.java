import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Client{

    public static void main(String[] arguments)throws Exception {
        Scanner scanner=new Scanner(System.in);
        //Create a DatagramSocket object for carrying the data.
        DatagramSocket datagramSocket= new DatagramSocket();
        /*
        DatagramSocket --> This class represents a socket for sending and receiving datagram packets.
        A datagram socket is the sending or receiving point for a packet delivery service.
        Each packet sent or received on a datagram socket is individually addressed and routed.
        Multiple packets sent from one machine to another may be routed differently, and may arrive in any order.
        DatagramSocket() --> Constructs a datagram socket and binds it to any available port on the local host machine.
         */
        InetAddress inetAddress =InetAddress.getLocalHost();
        /*
        InetAddress --> This class represents an Internet Protocol (IP) address. serverin ip adresini almak icin kullaniliyor
        getLocalHost() --> Returns the address of the local host.
        */
        byte[] buffer; //data typeini bytea donusturmesi gerekiyor
        //Create the DatagramPacket for sending the data.
        DatagramPacket request,reply;
        /*
        Datagram packets are used to implement a connectionless packet delivery service. Each message is routed from one
        machine to another based solely on information contained within that packet. Multiple packets sent from one
        machine to another might be routed differently, and might arrive in any order. Packet delivery is not guaranteed.
         */

        String clientOutgoing; //clienttan giden
        String serverIncoming; //serverdan gelen

        ArrayList<String> words = new ArrayList(); //kelimeleri burda tutuyoruz

        System.out.println("\nYou will have 15 seconds to respond to every word." +
                " \nIf you don't answer during this time,you will lose even if you get the right answer."+
                " \nPlease write 'begin' to start the game.");

        while(true){
            clientOutgoing = scanner.nextLine();

            if (clientOutgoing. equals("begin")) {

                System.out.println("\nEnter the first word that will send to the server:");
                clientOutgoing = scanner.nextLine();
                buffer=clientOutgoing.getBytes();
                reply=new DatagramPacket(buffer,buffer.length,inetAddress,1234);
                /*
                DatagramPacket(byte[] buf, int length, InetAddress address, int port)
                Constructs a datagram packet for sending packets of length length to the specified port number on the
                specified host.
                */

                datagramSocket.send(reply);
                /*
                send(DatagramPacket p) --> Sends a datagram packet from this socket.
                */

                while (true) {
                    if (clientOutgoing.isEmpty() || clientOutgoing.contains(" ")) {
                        System.out.println("You have entered an empty string. Please enter another word.");
                        clientOutgoing = scanner.nextLine();
                    }
                    else if(clientOutgoing.length() < 2){
                        System.out.println("You word must at least 2 character long. Please enter another word.");
                        clientOutgoing = scanner.nextLine();
                    }

                    else if (clientOutgoing.matches(".*\\d.*")) {
                        System.out.println("Your word must not contain any numbers.");
                        clientOutgoing = scanner.nextLine();
                    }
                    else if ( clientOutgoing.matches(".*[A-Z].*")){
                        System.out.println("Your word must be in lowercase.");
                        clientOutgoing = scanner.nextLine();
                    }
                    else
                        break;
                }
                System.out.println("\nPlease wait for server to respond...");
                words.add(clientOutgoing);
                break;
            }
            else{
                System.out.println("\nYou entered a wrong word. Please write 'begin' to start the game.");
            }
        }

        while(true){
            buffer = new byte[65535];
            request = new DatagramPacket(buffer, buffer.length);
            /*
            DatagramPacket(byte[] buf, int length) --> Constructs a DatagramPacket for receiving packets of length length.
            */

            datagramSocket.receive(request);
            /*
            receive(DatagramPacket p) --> Receives a datagram packet from this socket.
            */

            String receive = new String(request.getData());
            //serverdan gelen kelimeyi okur

            byte[] data = new byte[request.getLength()];
            System.arraycopy(request.getData(), request.getOffset(), data, 0, request.getLength());
            serverIncoming = new String(data);
            //serverdan gelen datanin typeini bytea ceviriyor

            if (serverIncoming.equals("\nServer entered a wrong word. GAME OVER. YOU WON!")
                    || serverIncoming.equals("\nServer couldn't respond on time. GAME OVER. YOU WON!")){
                System.out.println(serverIncoming);
                System.exit(0);
            }

            words.add(serverIncoming); //kelimeyi arrayliste ekliyor

            System.out.println("\nThe word that comes from Server = " + receive+
                    "\nEnter a word that will send to Server:");


            double firstTime = System.currentTimeMillis();
            String clientOutgoing2 = scanner.nextLine(); //zaman ve duplicate kontrolu icin
            double lastTime = System.currentTimeMillis();
            double respondTime = lastTime - firstTime;
            double time = respondTime/1000;

            if (time>15.0){
                System.out.println("\nYou respond in " + time + " seconds. GAME OVER. YOU LOST!");
                String timeOut = "\nClient couldn't respond on time. GAME OVER. YOU WON!";
                buffer = timeOut.getBytes();
                reply = new DatagramPacket(buffer, buffer.length, inetAddress, 1234);
                /*
                DatagramPacket(byte[] buf, int length, InetAddress address, int port)
                Constructs a datagram packet for sending packets of length length to the specified port number on the
                specified host.
                */
                datagramSocket.send(reply); //yukarida yaziyor satir 56
                System.exit(0);
            }
            //Rewrite the input if it met any if statement.
            while (true) {
                if (clientOutgoing2.isEmpty() || clientOutgoing2.contains(" ")) {
                    System.out.println("You have entered an empty string. Please enter another word.");
                    clientOutgoing2 = scanner.nextLine();
                }
                else if(clientOutgoing2.length() < 2){
                    System.out.println("You word must at least 2 character long. Please enter another word.");
                    clientOutgoing2 = scanner.nextLine();
                }
                else if (clientOutgoing2.matches(".*\\d.*")) {
                    System.out.println("Your word must not contain any numbers.");
                    clientOutgoing2 = scanner.nextLine();
                }
                else if ( clientOutgoing2.matches(".*[A-Z].*")){
                    System.out.println("Your word must be in lowercase.");
                    clientOutgoing2 = scanner.nextLine();
                }
                else if(words.contains(clientOutgoing2)){
                    System.out.println("This word has been used before! Please enter another word.");
                    clientOutgoing2 = scanner.nextLine();
                }
                else
                    break;
            }



            String endOfWord = serverIncoming.substring(serverIncoming.length() - 2); //kelimenin son iki harfi
            String beginningOfWord = clientOutgoing2.substring(0, 2); //kelimenin ilk iki harfi

            if (endOfWord.equals(beginningOfWord)) {
                System.out.println("\nYou entered a correct word. Waiting for Server's response.");
                buffer = clientOutgoing2.getBytes();
                reply = new DatagramPacket(buffer, buffer.length, inetAddress, 1234); //yukarida satir 133
                datagramSocket.send(reply); //yukarida satir 56
                words.add(clientOutgoing2);
            }
            else {
                System.out.println("\nYou entered a wrong word. GAME OVER. YOU LOST!");
                String message = "\nClient entered a wrong word. GAME OVER. YOU WON!";
                buffer = message.getBytes();
                reply = new DatagramPacket(buffer, buffer.length, inetAddress, 1234); //yukarida satir 133
                datagramSocket.send(reply);
                /*
                send(DatagramPacket p) Sends a datagram packet from this socket.
                */
                System.exit(0);
            }
        }
    }
}
