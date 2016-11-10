package net.frozenbit.plugin5zig.cubecraft.commands.handlers;

import com.google.common.base.Joiner;
import eu.the5zig.mod.The5zigAPI;
import net.frozenbit.plugin5zig.cubecraft.commands.CommandHandler;
import net.frozenbit.plugin5zig.cubecraft.commands.CommandOutputPrinter;
import net.frozenbit.plugin5zig.cubecraft.commands.UsageException;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import static java.lang.String.format;


public class RespondCommandHandler extends CommandHandler {
    public static final int MSG_HISTORY_SIZE = 5;
    private Deque<Message> messageHistory = new ArrayDeque<>();

    public RespondCommandHandler() {
        super("r", "Respond to the last message you have received");
    }

    @Override
    public void run(String cmd, List<String> args, CommandOutputPrinter printer) throws UsageException {
        if (messageHistory.isEmpty()) {
            printer.printErrln("No message received yet!");
            return;
        }
        String message = Joiner.on(' ').join(args);
        Message lastMessage = messageHistory.getLast();
        The5zigAPI.getAPI()
                .sendPlayerMessage(format(lastMessage.type.responseCommand, lastMessage.sender, message));
    }

    @Override
    public void printUsage(String cmd, CommandOutputPrinter printer) {
        printer.println(".%s <message> - respond to the last message you received", cmd);
    }

    public void onMessageReceived(MessageType type, String sender) {
        messageHistory.add(new Message(type, sender));
        if (messageHistory.size() > MSG_HISTORY_SIZE) {
            messageHistory.removeFirst();
        }
    }

    public enum MessageType {
        PM("/tell %s %s"), FRIEND("/fmsg %s %s");

        public final String responseCommand;

        MessageType(String responseCommand) {
            this.responseCommand = responseCommand;
        }
    }

    private static class Message {
        public final MessageType type;
        public final String sender;

        public Message(MessageType type, String sender) {
            this.sender = sender;
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Message message = (Message) o;

            return sender.equals(message.sender) && type == message.type;
        }

        @Override
        public int hashCode() {
            int result = sender.hashCode();
            result = 31 * result + type.hashCode();
            return result;
        }
    }
}
