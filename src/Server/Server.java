package Server;

import Server.ChatComponents.Message;
import Server.ChatComponents.MessageService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
* Class that describes the logic of the server
* */

public class Server {

    // port that our server will listen on
    static final int PORT = 1234;
    private Socket clientSocket;
    private ServerSocket serverSocket;
    // online users list
    public static ArrayList<ClientRequestHandler> clientRequestHandlerList;

    //userChatHistory
    private MessageHistoryManager messageHistoryManager;
    public Server() {
        this.clientSocket = null;
        this.serverSocket = null;
        this.clientRequestHandlerList = new ArrayList<>();
        this.messageHistoryManager = new MessageHistoryManager();
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.startServer();
    }

    //Method responsible for keeping our server running
    public void startServer() {
        try {

            serverSocket = new ServerSocket(PORT);
            System.out.println("Server is running...");
            while (!serverSocket.isClosed()) {
                clientSocket = serverSocket.accept();
                /*----------------------------------------*/
                /* Server is waiting for a connection with the client, other processes are blocked*/
                ClientRequestHandler clientRequestHandler = new ClientRequestHandler(clientSocket, this);
                clientRequestHandlerList.add(clientRequestHandler);
                Thread thread = new Thread(clientRequestHandler);
                thread.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastMessage(String msg) {
        for (ClientRequestHandler clientRqstHndlr : clientRequestHandlerList) {
            clientRqstHndlr.sendMessageFromServer(msg);
        }
    }

    public ClientRequestHandler findClient(String clientUsername) {
        for (ClientRequestHandler client : clientRequestHandlerList) {
            if (client.getUsername().equals(clientUsername)) {
                return client;
            }
        }
        return null;
    }
    public void addToMessageHistory(String sender, String recipient, Message message) {
        messageHistoryManager.addToMessageHistory(sender, recipient, message);
    }

    public List<Message> getMessageHistory(String sender, String recipient) {
        return messageHistoryManager.getMessageHistory(sender, recipient);
    }
}
