package net.reiveis.bukanbot.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.*;
import com.sedmelluq.discord.lavaplayer.tools.http.HttpContextFilter;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpClientTools;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface;
import com.sedmelluq.discord.lavaplayer.track.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import net.reiveis.bukanbot.BukanBot;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.List;

public class Music {
    private static final Pattern patternYT = Pattern.compile("http(?:s?):\\/\\/(?:www\\.)?youtu(?:be\\.com\\/watch\\?v=|\\.be\\/)([\\w\\-\\_]*)(&(amp;)?[\\w\\?=]*)?");

    private enum SOURCE_SELECTOR {YOUTUBE, YOUTUBE_PLAYLIST, SPOTIFY, SPOTIFY_PLAYLIST, SEARCH};

    public static EmbedBuilder musicEm = new EmbedBuilder();

    private static final Logger logger = LogManager.getLogger(Music.class.getName());

    public static void handleMusic(MessageReceivedEvent event, ArrayList<String> command){
        SOURCE_SELECTOR source = getSource(command);
        AudioTrack track = null;

        switch (source){
            case YOUTUBE:
                try{
                    track = handleYoutube(command.get(1));
                }
                catch(NullPointerException e){
                    logger.info("Invalid Youtube video URL");
                }
                break;
            case YOUTUBE_PLAYLIST:

                break;
            case SPOTIFY:
                //
                break;
            case SPOTIFY_PLAYLIST:

                break;
            case SEARCH:
                command.remove(0);
                String query = String.join(" ", command);
                track = handleYoutubeSearch(query);
                break;
            default:
                track = null;
        }
        try{
            startPlayer(event, track);
        }
        catch(NullPointerException e){
            logger.info("Not a valid track");
        }
    }
    public static void startPlayer(MessageReceivedEvent event, AudioTrack track){
        connectToChannel(event);
        Guild guild = event.getGuild();
        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioPlayer player = playerManager.createPlayer();

        guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));
        TrackScheduler trackScheduler = new TrackScheduler(player);
        player.addListener(trackScheduler);

        trackScheduler.queue(track);
        logger.info("Playing track: " + track.getInfo().title);

        musicEm.setTitle("Added a song!");
        musicEm.addField(track.getInfo().title, track.getInfo().author, false);
        musicEm.setColor(BukanBot.themeColor);
        event.getChannel().sendMessageEmbeds(musicEm.build()).queue();
        musicEm.clear();
    }

    public static SOURCE_SELECTOR getSource(List<String> command){

        Pattern patternYTPlaylist = Pattern.compile("/^.*(youtu.be\\/|list=)([^#\\&\\?]*).*/");
        Pattern patternSP = Pattern.compile("");
        Pattern patternSPPlaylist = Pattern.compile("");
        Matcher matchYT = patternYT.matcher(command.get(1));
        Matcher matchSP = patternSP.matcher(command.get(1));

        Matcher matchYTPlaylist = patternYTPlaylist.matcher(command.get(1));
        Matcher matchSPPlaylist = patternSPPlaylist.matcher(command.get(1));

        if(matchYT.find()){
            return SOURCE_SELECTOR.YOUTUBE;
        }
        else{
            return SOURCE_SELECTOR.SEARCH;
        }
    }

    private static Function<AudioTrackInfo, AudioTrack> getTrack(){
        return (AudioTrackInfo t) -> {
            YoutubeAudioSourceManager sourceManager = new YoutubeAudioSourceManager();
            YoutubeAudioTrack track = new YoutubeAudioTrack(t, sourceManager);
            return track;
        };
    }

    private static YoutubeAudioTrack handleYoutube(String url){
        CloseableHttpClient client = HttpClientTools.createSharedCookiesHttpBuilder().build();
        HttpClientContext context = new HttpClientContext();
        HttpContextFilter contextFilter = new BaseYoutubeHttpContextFilter();
        HttpInterface httpInterface = new HttpInterface(client, context, true, contextFilter);

        Matcher matcher = patternYT.matcher(url);
        String videoID = url.substring("https://www.youtube.com/watch?v=".length());
        System.out.println(videoID);
        DefaultYoutubeTrackDetailsLoader loader = new DefaultYoutubeTrackDetailsLoader();
        DefaultYoutubeTrackDetails trackDetails = (DefaultYoutubeTrackDetails) loader.loadDetails(httpInterface, videoID, false);

        return new YoutubeAudioTrack(trackDetails.getTrackInfo(), new YoutubeAudioSourceManager());
    }

    private static YoutubeAudioTrack handleYoutubeSearch(String query){
        YoutubeSearchProvider searchProvider = new YoutubeSearchProvider();
        //loadSearchResult returns a list instead of an AudioTrack
        BasicAudioPlaylist audioItem = (BasicAudioPlaylist) searchProvider.loadSearchResult(query, getTrack());
        YoutubeAudioTrack track = (YoutubeAudioTrack) audioItem.getTracks().get(0);
        return track;
    }

    public static void handleDisconnect(MessageReceivedEvent event){
        AudioManager audioManager = event.getGuild().getAudioManager();
        audioManager.closeAudioConnection();
        logger.info("Disconnected from voice channel " + event.getMember().getVoiceState().getChannel().getName());
        musicEm.addField("Bot has been disconnected", "", false);
        musicEm.setColor(BukanBot.themeColor);
        event.getChannel().sendMessageEmbeds(musicEm.build()).queue();
        musicEm.clear();
    }
    private static void connectToChannel(MessageReceivedEvent event){
        try {
            VoiceChannel target = (VoiceChannel) event.getMember().getVoiceState().getChannel();
            if(target == null)
                throw new NullPointerException();
            AudioManager audioManager = event.getGuild().getAudioManager();
            audioManager.openAudioConnection(target);
            logger.info("The bot has joined a voice channel " + target.getName());
        }
        catch(NullPointerException e){
            musicEm.addField("You are not connected to a voice channel!", "Please connect to a voice channel before requesting a track!", false);
            musicEm.setColor(BukanBot.themeColor);
            event.getChannel().sendMessageEmbeds(musicEm.build()).queue();
            musicEm.clear();

            logger.info("User is not in a voice channel!", e);
        }
    }
}
