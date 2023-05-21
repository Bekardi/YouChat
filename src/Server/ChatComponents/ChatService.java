package Server.ChatComponents;

import Server.ClientRequestHandler;

import java.util.ArrayList;
import java.util.List;
import Server.Server;

public class ChatService implements MessageService {
    private ClientRequestHandler client;
    private Server server;
    private ArrayList<ClientRequestHandler> clientRequestHandlerList;

    public ChatService(ArrayList<ClientRequestHandler> clientRequestHandlerList, ClientRequestHandler client, Server server) {
        this.clientRequestHandlerList = clientRequestHandlerList;
        this.client = client;
        this.server = server;
    }

    @Override
    public void directMessage(Message message, ClientRequestHandler sender, ClientRequestHandler recipient) {

        recipient.sendMessageFromServer(sender.getUsername() + ": " + message.getText());

        // Add the message to the sender's message history
        server.addToMessageHistory(sender.getUsername(), recipient.getUsername(), message);
        // Add the message to the recipient's message history
        server.addToMessageHistory(recipient.getUsername(), sender.getUsername(), message);
    }

}


