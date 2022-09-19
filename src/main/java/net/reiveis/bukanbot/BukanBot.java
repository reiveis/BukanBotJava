package net.reiveis.bukanbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BukanBot {
    private static final Logger logger = LogManager.getLogger(BukanBot.class.getName());
    public static JDA jda;
    public static char prefix = '%';
    private static final String token = System.getenv("BB_TOKEN");
    public static int themeColor = 0xfcba03;

    public static void main(String[] args) throws InterruptedException {
        jda = JDABuilder.createDefault(BukanBot.token, GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS).build();
        jda.awaitReady();
        jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
        jda.getPresence().setActivity(Activity.watching("absolutely nothing"));
        logger.info("Logged in as " + jda.getSelfUser().getAsTag());

        jda.addEventListener(new Commands());
    }
}
