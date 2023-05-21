package Server.ChatComponents;

import Server.ClientRequestHandler;


/*
* This interface defines the contract for sending messages.
* It has a single method sendMessage that takes the sender's username, recipient's username,
* and the message content as parameters.
* */
public interface MessageService {
//    void sendMessageToOtherUser(String sender, String recipient, String message);
    void directMessage(Message message, ClientRequestHandler sender , ClientRequestHandler recipient);
}