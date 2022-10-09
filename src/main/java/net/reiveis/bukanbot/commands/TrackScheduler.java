package net.reiveis.bukanbot.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    private AudioPlayer audioPlayer;

    private BlockingQueue<AudioTrack> audioTracks;

    public TrackScheduler(AudioPlayer p){
        audioPlayer = p;
        this.audioTracks = new LinkedBlockingQueue<>();
    }

    public void queue(AudioTrack track) {
        audioTracks.offer(track);
        if(audioPlayer.getPlayingTrack() == null){
            audioPlayer.playTrack(audioTracks.remove());
        }
    }

    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason){
        if(endReason.mayStartNext){
            audioPlayer.playTrack(audioTracks.remove());
        }
    }
}
