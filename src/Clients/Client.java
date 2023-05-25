package Clients;

import Server.Server;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket; // the socket to communicate with the server
    private BufferedReader bufferedReader; // the reader to read messages from the server
    private BufferedWriter bufferedWriter; // the writer to send messages to the server
    private String username; // the username of the client

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter); // close all resources if an exception occurs
        }
    }

    public void sendMessage() {
        try {
            bufferedWriter.write(username); // send the username to the server
            bufferedWriter.newLine();
            bufferedWriter.flush();

            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            while (socket.isConnected()) { // while the socket is connected
                String messageToSend = consoleReader.readLine(); // read a message from the console
                bufferedWriter.write(messageToSend); // send the message to the server
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter); // close all resources if an exception occurs
        }
    }

    public void listenForMessage() {
        new Thread(new Runnable() { // create a new thread to listen for messages from the server
            @Override
            public void run() {
                String msgFromChat;

                while (socket.isConnected()) { // while the socket is connected
                    try {
                        msgFromChat = bufferedReader.readLine(); // read a message from the server
                        System.out.println(msgFromChat); // print the message to the console
                    } catch (IOException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter); // close all resources if an exception occurs
                    }
                }
            }
        }).start(); // start the thread
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) { // if the reader is not null
                bufferedReader.close(); // close it
            }
            if (bufferedWriter != null) { // if the writer is not null
                bufferedWriter.close(); // close it
            }
            if (socket != null) { // if the socket is not null
                socket.close(); // close it
            }
        } catch (IOException e) {
            e.printStackTrace(); // print the stack trace of the exception
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter your username for the chat: "); // prompt the user to enter a username
        String username = consoleReader.readLine(); // read the username from the console
        Socket socket = new Socket("localhost", 1234); // create a socket with localhost and port 1234
        Client client = new Client(socket, username); // create a client object with the socket and username
        client.listenForMessage(); // start listening for messages from the server
        client.sendMessage(); // start sending messages to the server
    }

    public String getUsername() { // getter method for username
        return username;
    }

    public void setUsername(String username) { // setter method for username
        this.username = username;
    }
}