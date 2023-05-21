package Server.ChatComponents;

public class Message {
    private String sender;
    private String recipient;
    private String text;

    public Message(String sender, String recipient, String text) {
        this.sender = sender;
        this.recipient = recipient;
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
