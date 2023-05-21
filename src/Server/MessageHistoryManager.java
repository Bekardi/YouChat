package Server;
import Server.ChatComponents.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageHistoryManager {

    private Map<String, List<Message>> messageHistory;

    public MessageHistoryManager() {
        this.messageHistory = new HashMap<>();
    }

    public void addToMessageHistory(String sender, String recipient, Message message) {
        String dialogKey = getDialogKey(sender, recipient);
        List<Message> dialogHistory = messageHistory.getOrDefault(dialogKey, new ArrayList<>());
        dialogHistory.add(message);
        messageHistory.put(dialogKey, dialogHistory);
    }

    public List<Message> getMessageHistory(String sender, String recipient) {
        String dialogKey = getDialogKey(sender, recipient);
        return messageHistory.getOrDefault(dialogKey, new ArrayList<>());
    }

    private String getDialogKey(String user1, String user2) {
        return user1 + "_" + user2;
    }
}