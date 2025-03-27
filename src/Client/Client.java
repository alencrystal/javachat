package Client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import javax.swing.*;

public class Client {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int PORT = 42069;

    private PrintWriter out;
    private BufferedReader in;


    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JList<String> userList;
    private DefaultListModel userListModel;
    private JButton darkModeButton;


    public Client(){
        frame = new JFrame("Spaichat");
        chatArea = new JTextArea(20, 40);
        chatArea.setEditable(false);
        messageField = new JTextField("Enter text...", 30);
        messageField.setForeground(Color.gray);
        sendButton = new JButton("send");
        darkModeButton = new JButton("mode");
        

        JPanel panel = new JPanel();
        panel.add(messageField);
        panel.add(sendButton);
        panel.add(darkModeButton);

        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);

        JPanel userListPanel = new JPanel();
        userListPanel.add(userList);
        userListPanel.setPreferredSize(new Dimension(100, 0));
        userListPanel.setBackground(Color.LIGHT_GRAY);

        frame.getContentPane().add(chatArea, BorderLayout.CENTER);
        frame.getContentPane().add(panel, BorderLayout.SOUTH);
        frame.getContentPane().add(userListPanel, BorderLayout.EAST);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        chatArea.setFont(new Font("Inter", Font.ITALIC, 15));

        messageField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e){
                if(messageField.getForeground() == Color.gray){
                messageField.setForeground(Color.black);
                messageField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {

                if(messageField.getText().equals("")){
                messageField.setForeground(Color.gray);
                messageField.setText("Enter text...");
                }
                
            }
        });
        sendButton.addActionListener(event -> sendMessage());
        messageField.addActionListener(event -> sendMessage());
        darkModeButton.addActionListener(event -> {
            if(chatArea.getBackground() == Color.WHITE){
                chatArea.setBackground(Color.BLACK);
                chatArea.setForeground(Color.WHITE);
                userListPanel.setBackground(Color.DARK_GRAY);
                userListPanel.setForeground(Color.WHITE);
                panel.setBackground(Color.DARK_GRAY);
            }
            else {
                chatArea.setBackground(Color.WHITE);
                chatArea.setForeground(Color.BLACK);
                userListPanel.setBackground(Color.LIGHT_GRAY);
                userListPanel.setForeground(Color.BLACK);
                panel.setBackground(Color.LIGHT_GRAY);
            }
        });

        


        connectToServer();

        sendButton.addActionListener(event -> sendMessage());
        messageField.addActionListener(event -> sendMessage());

    }

    private void sendMessage(){
        String message = messageField.getText().trim(); //trim per togliere gli spazi vuoti alla fine

        if (!message.isEmpty() && !(messageField.getForeground() == Color.gray)) {
           out.println(message);
           messageField.setText("");       
        }
    }


    private void connectToServer(){
        try {
           Socket socket = new Socket(SERVER_IP, PORT);
           out = new PrintWriter(socket.getOutputStream(), true);
           in = new BufferedReader(new InputStreamReader(socket.getInputStream())); 

           boolean findUser = true;
           String message;
           String username;

           do { 
                username = JOptionPane.showInputDialog(frame, "enter username");
                
                if (username == null) {

                    System.exit(0);
                }

                username = username.trim();

                if (!username.isEmpty()) {
                    out.println(username);
                    findUser = !Objects.equals(message = in.readLine(), "this user already exist :/"); 
                }   
           } while (!findUser);
        

           
           new Thread(() -> {
            try {
                String serverMessage;
                while((serverMessage = in.readLine()) != null){
                    if(serverMessage.startsWith("/users ")){

                        updateUserList(serverMessage.replace("/users ", ""));
                    } else {
                    chatArea.append(serverMessage +  "\n");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
           }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Client::new);
    }


    private void updateUserList(String serverMessage){
        userListModel.clear();
        for(String username : serverMessage.split(", ")){
            userListModel.addElement(username);
        }
    }

}