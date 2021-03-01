package com.ssdown.detirbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

public class AudioPlayerSendHandler implements AudioSendHandler {

    private final AudioPlayer audioPlayer;
    private AudioFrame audioFrame;

    public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }

    @Override
    public boolean canProvide() {
        if(audioFrame == null) {
            audioFrame = audioPlayer.provide();
        }
        return audioFrame != null;
    }

    @Nullable
    @Override
    public ByteBuffer provide20MsAudio() {
        if(audioFrame == null) {
            audioFrame = audioPlayer.provide();
        }
        ByteBuffer data = audioFrame != null ? ByteBuffer.wrap(audioFrame.getData()) : null;
        audioFrame = null;

        return data;
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}
