package net.reiveis.bukanbot;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.reiveis.bukanbot.commands.TimeTable;
import net.reiveis.bukanbot.commands.Normals;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

public class Commands extends ListenerAdapter {
    private static final Logger logger = LogManager.getLogger(Commands.class.getName());

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        String content = event.getMessage().getContentRaw();
        logger.info("Message received from " + event.getMessage().getAuthor().getAsTag() + ": " + event.getMessage().getContentRaw());
        var command = checkRemovePrefixAndSplitArgs(content);

        // Checks if the user entered a command or not, since List is empty only when there is no command prefix present
        if(!command.isEmpty()){
            handleCommand(command, event);
        }
    }

    private List<String> checkRemovePrefixAndSplitArgs(String content){
        if(content.length() > 0 && content.charAt(0) == (BukanBot.prefix)){
            content = content.substring(1);
            return Arrays.asList(content.split(" "));
        }
        else {
            return List.of();
        }
    }

    private void handleCommand(List<String> command, MessageReceivedEvent e){
        logger.info("The " + command.get(0) + " command has been invoked!");
        switch (command.get(0)){
            case "info":
            case "i":
                Normals.infoCommand(e);
                break;
            case "help":
            case "h":
                Normals.helpCommand(e);
                break;
            case "jadual":
            case "jd":
                if(command.size() > 1){
                    TimeTable.timeTableCommand(e, command.get(1));
                }
                else {
                    TimeTable.timeTableCommand(e);
                }
                break;
            default:
                break;
        }
    }
}

