package net.reiveis.bukanbot;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.reiveis.bukanbot.commands.Music;
import net.reiveis.bukanbot.commands.TimeTable;
import net.reiveis.bukanbot.commands.Misc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Commands extends ListenerAdapter {
    private static final Logger logger = LogManager.getLogger(Commands.class.getName());

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        if(event.getAuthor().isBot()){
            return;
        }
        String content = event.getMessage().getContentRaw();
        logger.info("Message received from " + event.getMessage().getAuthor().getAsTag() + ": " + event.getMessage().getContentRaw());
        ArrayList<String> command = checkRemovePrefixAndSplitArgs(content);

        // Checks if the user entered a command or not, since List is empty only when there is no command prefix present
        if(!command.isEmpty()){
            handleCommand(command, event);
        }
    }

    private ArrayList<String> checkRemovePrefixAndSplitArgs(String content){
        if(content.length() > 0 && content.charAt(0) == (BukanBot.prefix)){
            content = content.substring(1);
            return new ArrayList<>(Arrays.asList(content.split(" ")));
        }
        else {
            return new ArrayList<>(List.of());
        }
    }

    private void handleCommand(ArrayList<String> command, MessageReceivedEvent e){
        logger.info("The " + command.get(0) + " command has been invoked!");
        switch (command.get(0)){
            case "info":
            case "i":
                Misc.infoCommand(e);
                break;
            case "help":
            case "h":
                Misc.helpCommand(e);
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
            case "play":
                Music.handleMusic(e, command);
                break;
            case "disconnect":
            case "d":
                Music.handleDisconnect(e);
                break;
            default:
                break;
        }
    }
}

