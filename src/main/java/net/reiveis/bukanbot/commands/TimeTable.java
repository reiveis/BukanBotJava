package net.reiveis.bukanbot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.reiveis.bukanbot.BukanBot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeTable {
    private static final Logger logger = LogManager.getLogger(TimeTable.class.getName());
    public static void timeTableCommand(MessageReceivedEvent e) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEEE");
        String dayName = dateFormat.format(calendar.getTime());

        sendTimeTable(e, dayName);
        logger.info("Sent a current day timetable to " + e.getAuthor().getAsTag());
    }

    public static void timeTableCommand(MessageReceivedEvent e, String day) {
        day = Character.toUpperCase(day.charAt(0)) + day.substring(1);
        sendTimeTable(e, day);
        logger.info("Sent a timetable to " + e.getAuthor().getAsTag());
    }

    public static void sendTimeTable(MessageReceivedEvent e, String dayName) {
        JSONParser parser = new JSONParser();
        EmbedBuilder infoEm = new EmbedBuilder();

        infoEm.setTitle("Jadual Waktu - " + dayName);
        try {
            Object object = parser.parse(new FileReader("C:\\Users\\Lenovo Thinkbook\\IdeaProjects\\BukanBotJava\\src\\main\\java\\net\\reiveis\\bukanbot\\timetable.json"));
            JSONObject jsonObject = (JSONObject) object;
            JSONArray daySchedule = (JSONArray) jsonObject.get(dayName);

            for (Object o : daySchedule) {
                JSONObject sub = (JSONObject) o;
                String title = (String) sub.get("time");
                String cont = sub.get("code") + " - " + sub.get("name") + "\nat " + sub.get("location") + "\n" + sub.get("lecturer");

                infoEm.addField(title, cont, false);
            }
        }
        catch (Exception err) {
            err.printStackTrace();
        }
        infoEm.setColor(BukanBot.themeColor);

        e.getChannel().sendMessageEmbeds(infoEm.build()).queue();
    }
}
