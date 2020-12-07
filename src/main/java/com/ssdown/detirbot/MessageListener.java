package com.ssdown.detirbot;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MessageListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        String content = message.getContentRaw();
        if(content.equals("안녕")) {
            MessageChannel channel = event.getChannel();
            channel.sendMessage("하세요").queue();
        }
    }
}
