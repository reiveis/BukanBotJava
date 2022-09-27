package net.reiveis.bukanbot.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.LinkedList;
import java.util.Queue;

public class TrackScheduler extends AudioEventAdapter {
    private AudioPlayer audioPlayer;

    private Queue<AudioTrack> audioTracks = new LinkedList<>();

    public TrackScheduler(AudioPlayer p){
        audioPlayer = p;
    }

    public void queue(AudioTrack track) {
        audioTracks.add(track);
    }
}
