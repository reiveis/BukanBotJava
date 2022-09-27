package net.reiveis.bukanbot.commands;

import com.sedmelluq.discord.lavaplayer.container.mp3.Mp3AudioTrack;    // test audio track functionality
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.local.LocalSeekableInputStream;
import com.sedmelluq.discord.lavaplayer.source.youtube.*;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.http.HttpContextFilter;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface;
import com.sedmelluq.discord.lavaplayer.tools.io.SeekableInputStream;
import com.sedmelluq.discord.lavaplayer.track.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import net.reiveis.bukanbot.BukanBot;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.File;
import java.util.List;

public class Music {
    private enum SOURCE_SELECTOR {YOUTUBE, YOUTUBE_PLAYLIST, SPOTIFY, SPOTIFY_PLAYLIST, SEARCH};

    public static EmbedBuilder musicEm = new EmbedBuilder();

    private static final Logger logger = LogManager.getLogger(Music.class.getName());

    public static void handleMusic(MessageReceivedEvent event, ArrayList<String> command){
        SOURCE_SELECTOR source = getSource(command);
        AudioTrack track = null;

        switch (source){
            case YOUTUBE:
                break;
            case YOUTUBE_PLAYLIST:
                break;
            case SPOTIFY:
                break;
            case SPOTIFY_PLAYLIST:
                break;
            case SEARCH:
                command.remove(0);
                String query = String.join(" ", command);
                track = handleYoutube(query);
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

        player.playTrack(track);
        logger.info("Playing track: " + track.getInfo().title);
    }

    public static SOURCE_SELECTOR getSource(List<String> command){
        Pattern patternYT = Pattern.compile("http(?:s?):\\/\\/(?:www\\.)?youtu(?:be\\.com\\/watch\\?v=|\\.be\\/)([\\w\\-\\_]*)(&(amp;)?[\\w\\?=]*)?");
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
        YoutubeSearchProvider searchProvider = new YoutubeSearchProvider();
        BasicAudioPlaylist audioItem = (BasicAudioPlaylist) searchProvider.loadSearchResult(url, getTrack());
        YoutubeAudioTrack track = (YoutubeAudioTrack) audioItem.getTracks().get(0);
        return track;
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
