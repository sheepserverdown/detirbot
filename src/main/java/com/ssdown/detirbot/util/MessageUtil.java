package com.ssdown.detirbot.util;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

public class MessageUtil {
    public static boolean canNotTalk(TextChannel channel) {
        if(channel == null) return true;
        Member member = channel.getGuild().getSelfMember();
        return member == null
                || !member.hasPermission(channel, Permission.MESSAGE_READ)
                || !member.hasPermission(channel, Permission.MESSAGE_WRITE);
    }

    private static void sendMessage(Message message, MessageChannel channel) {
        if(channel instanceof TextChannel && canNotTalk((TextChannel) channel)) return;
        channel.sendMessage(message).queue(null, null);
    }

    public static void sendMessage(MessageEmbed embed, MessageChannel channel) {
        sendMessage(new MessageBuilder().setEmbed(embed).build(), channel);
    }

    public static void sendMessage(String message, MessageChannel channel) {
        sendMessage(new MessageBuilder().append(filter(message)).build(), channel);
    }

    private static String filter(String msgContent) {
        return msgContent.length() > 2000
                ? "메세지가 2000글자를 넘습니다."
                : msgContent.replace("@everyone", "@\u180Eeveryone").replace("@here", "@\u180Ehere");
    }

    public static String userDiscrimSet(User u) {
        return stripFormatting(u.getName() + "#" + u.getDiscriminator());
    }

    //markdown 문법 방지용
    public static String stripFormatting(String s) {
        return s.replace("*", "\\*")
                .replace("`", "\\`")
                .replace("_", "\\_")
                .replace("~~", "\\~\\~")
                .replace(">", "\u180E>");
    }
}
