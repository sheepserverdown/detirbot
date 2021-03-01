package com.ssdown.detirbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

public class TrackManager extends AudioEventAdapter {
    private final AudioPlayer player;
    private final Queue<AudioInfo> queue;

    public TrackManager(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingDeque<>();
    }

    public void queue(AudioTrack track, Member member) {
        AudioInfo info = new AudioInfo(track, member);
        queue.add(info);

        if(player.getPlayingTrack() == null) {
            player.playTrack(track);
        }
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        AudioInfo info = queue.element();
        VoiceChannel voiceChannel = info.getMember().getVoiceState().getChannel();
        // 유저가 모든 음챗에서 떠났을 경우
        if(voiceChannel == null) {
            player.stopTrack();
        } else { // 사람이 있으면
            info.getMember().getGuild().getAudioManager().openAudioConnection(voiceChannel);
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        Guild guild = queue.poll().getMember().getGuild();
        if(queue.isEmpty()) { // 대기 리스트가 비었으면
            guild.getAudioManager().closeAudioConnection();
        } else {
            player.playTrack(queue.element().getTrack());
        }
    }

    public void shuffleQueue() {
        List<AudioInfo> trackQueue = new ArrayList<>(getQueuedTracks());
        AudioInfo currentInfo = trackQueue.get(0);
        trackQueue.remove(0);
        Collections.shuffle(trackQueue);
        trackQueue.add(0, currentInfo);
        trackQueue.clear();
        queue.addAll(trackQueue);
    }

    public Set<AudioInfo> getQueuedTracks() {
        return new LinkedHashSet<>(queue);
    }

    public void purgeQueue() {
        queue.clear();
    }

    public void remove(AudioInfo info) {
        queue.remove(info);
    }

    public AudioInfo getTrackInfo(AudioTrack track) {
        return queue.stream().filter(audioInfo -> audioInfo.getTrack().equals(track)).findFirst().orElse(null);
    }
}
