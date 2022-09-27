package net.reiveis.bukanbot.commands;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.reiveis.bukanbot.BukanBot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Normals {
    private static final Logger logger = LogManager.getLogger(Normals.class.getName());
    public static void infoCommand(MessageReceivedEvent e){
        EmbedBuilder infoEm = new EmbedBuilder();
        infoEm.setTitle("Welcome to BukanBot Java Edition!");
        infoEm.setDescription("A remake of BukanBot");
        infoEm.setFooter("Created by Reiveis \nhttps://github.com/reiveis, Maven Version v1.0-SNAPSHOT");
        infoEm.setColor(BukanBot.themeColor);

        e.getChannel().sendMessageEmbeds(infoEm.build()).queue();
        infoEm.clear();
        logger.info("Sent info message to " + e.getMessage().getAuthor().getAsTag() + "!");
    }

    public static void helpCommand(MessageReceivedEvent e){
        EmbedBuilder infoEm = new EmbedBuilder();
        infoEm.setTitle("List of Commands");
        infoEm.addField("%help", "Gives a list of commands.", false);
        infoEm.addField("%info", "General information about the bot.", false);
        infoEm.addField("%jadual", "Sends a timetable for the current day, or a specific day, if given (i.e: %jadual monday)", false);
        infoEm.setColor(BukanBot.themeColor);

        e.getChannel().sendMessageEmbeds(infoEm.build()).queue();
        infoEm.clear();
    }
}
