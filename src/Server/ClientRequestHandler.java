package Server;

import Server.ChatComponents.ChatService;
import Server.ChatComponents.Message;
import Server.ChatComponents.MessageService;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class ClientRequestHandler implements Runnable {

    private String clientUsername;
    private static int clients_count = 0;
    private Server server;
    private Socket clientSocket;
    // incoming message
    private BufferedReader bufferedReader;
    // outgoing message
    private BufferedWriter bufferedWriter;
    private MessageService messageService;



    //----Output Templates
    private String userDoesNotExist_ERROR = "==ERROR== \n " +
            "--> USER NOT FOUND! \n" +
            "USER: ";
    private final String dmInstructionForUser =
            "1) --> To DM write: /msg Username YourMessage \n" +
                    "2) --> To get the list of online users, type: /showOnlineUsers \n" +
                    "3) --> To get the chatHistory with a user, type: /showHistory Username \n" +
                    "4) --> To get the menu options output again, type: /help \n";

    public ClientRequestHandler(Socket clientSocket, Server server) {
        try {
            clients_count++;
            this.server = server;
            this.clientSocket = clientSocket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            this.messageService = new ChatService(server);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Override the run() method, which is called in Server class when
    // we call new Thread(clientRequestHandler).start();
    @Override
    public void run() {
        //user connects to the server
        while (clientSocket.isConnected()) {
            server.broadcastMessage("Client: " + clientUsername + " has connected! \n + " +
                    "Clients Online : " + clients_count);
            break;
        }
        //after, user gets instructions from server:
        showInstructions();
        //if user types smth into the console
        String messageFromClient;
        while (clientSocket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                CheckUserInput(messageFromClient);

            } catch (IOException e) {
                closeEverything(clientSocket, bufferedReader, bufferedWriter);
                break;
            }
        }

    }

    //Method decomposes client's input and depending on the input will run the operation
    public void CheckUserInput(String messageToSend) {
        if (messageToSend.startsWith("/")) {

            String[] parts = messageToSend.split(" ", 3);
            // 1) send private message
            if (parts.length == 3 && parts[0].equals("/msg")) {

                String recipient = parts[1];
                String text = parts[2];
                ClientRequestHandler recipientClient = server.findClient(recipient);
                if (recipientClient == null) { //if recipient is not found, send the error msg

                    this.sendMessageFromServer(userDoesNotExist_ERROR + recipient);

                }else {
                    //else create message object and send it to the recipient
                    Message message = new Message(clientUsername, recipient, text);
                    // messageService.sendMessageToOtherUser(clientUsername, recipient, message);
                    messageService.directMessage(message, this, recipientClient);
                    return;
                }
            }
            //2) show Online users
            if (parts.length == 1 && parts[0].equalsIgnoreCase("/showOnlineUsers")) {
                showOnlineUsers();
                return;
            }

            //3) show ChatHistory
            if (parts.length == 2 && parts[0].equalsIgnoreCase("/showHistory")) {
                String username = parts[1];
                showHistory(username);
                return;
            }
            //show Instructions
            if (parts.length == 1 && parts[0].equalsIgnoreCase("/help")) {
                showInstructions();
                return;
            }
        }
    }

    //---------------------------------CLIENT OPERATIONS----------------------------------------------------------------
    public void showOnlineUsers() {
        sendMessageFromServer("Total Online Users: " + Server.clientRequestHandlerList.size());
        for (ClientRequestHandler user : Server.clientRequestHandlerList) {
            sendMessageFromServer("User: " + user.getUsername() + " is ONLINE");
        }
    }

    public void showInstructions() {
        sendMessageFromServer("Server: \n" + dmInstructionForUser);
    }

    public void showHistory(String username) {
        List<Message> messageHistory = server.getMessageHistory(this.clientUsername, username);
        if (!messageHistory.isEmpty()) {
            for (Message message : messageHistory) {
                sendMessageFromServer(message.toString());
            }
        } else {
            sendMessageFromServer("No message history found for user: " + username);
        }
    }

    //------------------------------SERVER METHODS----------------------------------------------------------------------
    // Sending the message from server
    public void sendMessageFromServer(String message) {
        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything(clientSocket, bufferedReader, bufferedWriter);
        }
    }

    // removing the client from online users list
    public void removeClientHandler() {
        Server.clientRequestHandlerList.remove(this);
        clients_count--;
        server.broadcastMessage("Server: " + clientUsername + " has left the chat!");
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Returning username
    public String getUsername() {
        return clientUsername;
    }

}
