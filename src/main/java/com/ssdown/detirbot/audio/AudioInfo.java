package com.ssdown.detirbot.audio;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.util.HashSet;
import java.util.Set;

public class AudioInfo {
    private final AudioTrack track;
    private final Set<String> skips;
    private final Member member;

    public AudioInfo(AudioTrack track, Member member) {
        this.track = track;
        this.skips = new HashSet<>();
        this.member = member;
    }

    public AudioTrack getTrack() {
        return track;
    }

    public int getSkips() {
        return skips.size();
    }

    public void addSkip(User user) {
        skips.add(user.getId());
    }

    public boolean hasVoted(User user) {
        return skips.contains(user.getId());
    }

    public Member getMember() {
        return member;
    }
}
