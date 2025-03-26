package Server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 42069;
    private static Map<String, PrintWriter> clients = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("links start!(il server ora è attivo)");

        try(ServerSocket serverSocket = new ServerSocket(PORT);){
            while (true) { 
                new ClientHandler(serverSocket.accept()).start();

            }
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
        
    }
    private static class ClientHandler extends Thread{
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket){
            this.socket = socket;
        }

        public void run(){
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                
                do{
                username = in.readLine();
                }while (checkUsername(username));


                synchronized(clients){
                    clients.put(username, out);

                    System.out.println(username + " connected to the chat!");
                    broadcastMessage(username + " connected to the chat!");

                    UpdateListUser();
                }

                
                String message;

                while ((message = in.readLine()) != null){
                    if(message.startsWith("/", 0)){

                        String command = message.split(" ")[0];

                        switch (command) {
                            case "/ls":
                                commandList(message);
                                break;
                            case "/p":
                                commandPrivate(message.split(" ")[1]); //guarda la seconda parola della frase e controlla se è uno user
                                break;
                            case "/clear":
                                commandClear(out);
                                break;
                            default:
                                out.println("command not found ;(");
                        }

                        
                    }else{

                    System.out.println(message);

                    broadcastMessage(username + ": " + message);

                    }
                }
                  
                
            } catch (Exception ex) {
                ex.printStackTrace();

            } finally {
                try {
                    clients.remove(username);
                    UpdateListUser();
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    UpdateListUser();
                }
            }
        }

        private void broadcastMessage(String message){
            synchronized (clients) {
                for (PrintWriter printWriter : clients.values()) {
                    printWriter.println(message);
                    
                }
                
            }
        }

        private void UpdateListUser(){
            String list = "/users " + String.join(", ", clients.keySet());
            broadcastMessage(list);
        }

        private boolean checkUsername(String username){
            boolean result = false;
            synchronized (clients) {
                if (clients.containsKey(username)){
                    result = true;
                    out.println("this user already exist :/");
                } 
            }
            return result;
        }
        private void commandList(String message){
                synchronized (clients) {
                    out.println("user list: ");
                    for (String user : clients.keySet()) {
                        out.println(user);
                    }
                }             
        }
        private void commandPrivate(String user){

                try {

                    if(clients.containsKey(user)){
                        out.println("type the message: ");

                        synchronized (clients) {
                        try {
                            String message = in.readLine();
                            clients.get(user).println(message);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        
                    }
                }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                
            }
            
        
    
        private static void commandClear(PrintWriter out){
            synchronized (clients) {
                out.println("\033\143");          //per pulire il terminale
                
            }
        }


}
}


    

