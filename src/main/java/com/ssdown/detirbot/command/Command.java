package com.ssdown.detirbot.command;

import com.ssdown.detirbot.config.Configuration;
import com.ssdown.detirbot.util.MessageUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

abstract class Command extends ListenerAdapter {

    // 커맨드 실행
    public abstract void executeCommand(String[] args, MessageReceivedEvent e, MessageSender chat);

    public abstract List<String> getAlias();

    public boolean allowsPrivate() {
        return false;
    }

    public boolean authorExclusive() {
        return false;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent e) {
        // 이벤트와 연관된 객체를 전부 체크, 동시성 문제 방지
        if(e.getAuthor() == null || e.getChannel() == null) return;
        // 봇이 보낸 메세지가 아닌 경우 거절
        if(e.getAuthor().isBot() || !isValidCommand(e.getMessage())) return;
        // 명령어가 소유자만 사용하도록 의도된 경우 거절
        if(authorExclusive() && !e.getAuthor().getId().equals(Configuration.getInstance().getAuthorId())) return;
        // 만약 채널에서 대화를 못하게 됐을 경우에도 거절
        if(e.isFromType(ChannelType.TEXT) && MessageUtil.canNotTalk(e.getTextChannel())) return;

        String[] args = commandArgs(e.getMessage());
        MessageSender chat = new MessageSender(e);

        // 특정 서버에 대한 커맨드인지 체크
        if(e.isFromType(ChannelType.PRIVATE) && !allowsPrivate()) {
            chat.sendMessage("** 이 커맨드는 정해진 서버 내에서만 사용 가능한 메세지 입니다!! **");
        } else {
            try {
                executeCommand(args, e, chat);
            } catch (Exception ex){
                ex.printStackTrace();
                String msg = "User : **" + MessageUtil.userDiscrimSet(e.getAuthor())
                        + " **\nMessage:\n" + MessageUtil.stripFormatting(e.getMessage().getContentDisplay())
                        + " *\n\nError:```java\n" + ex.getMessage() + "```";
                if(msg.length() <= 2000) {
                    chat.sendPrivateMessageToUser(msg, e.getJDA().getUserById(Configuration.getInstance().getAuthorId()));
                }
            }
        }
    }

    private boolean isValidCommand(Message msg) {
        String prefix = Configuration.getInstance().getPrefix();
        //접두사로 시작하는 말이 아닌 경우는 커맨드가 아니다.
        if(!msg.getContentRaw().startsWith(prefix))
            return false;
        String cmdName = msg.getContentRaw().substring(prefix.length());
        if(cmdName.contains(" ")) {
            cmdName = cmdName.substring(0, cmdName.indexOf(" ")); // 다른 인수가 있으면 제거
        }
        if(cmdName.contains("\n")) {
            cmdName = cmdName.substring(0, cmdName.indexOf("\n"));
        }
        return getAlias().contains(cmdName.toLowerCase());
    }

    private String[] commandArgs(Message msg) {
        String noPrefix = msg.getContentRaw().substring(Configuration.getInstance().getPrefix().length());
        if(!noPrefix.contains(" ")) { // 공백 없음 = 인수 없음
            return new String[]{};
        }
        return noPrefix.substring(noPrefix.indexOf(" ") + 1).split("\\s+");
    }

    class MessageSender {
        private final MessageReceivedEvent event;

        public MessageSender(MessageReceivedEvent event) {
            this.event = event;
        }

        void sendMessage(String msgContent, MessageChannel tChannel) {
            if(tChannel == null) return;
            MessageUtil.sendMessage(msgContent, tChannel);
        }

        void sendMessage(String msgContent) {
            sendMessage(msgContent, event.getChannel());
        }

        void sendEmbed(String title, String description) {
            if(event.isFromType(ChannelType.TEXT) &&
                    event.getGuild().getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_EMBED_LINKS)) {
                MessageUtil.sendMessage(new EmbedBuilder().setTitle(title, null).setDescription(description).build(), event.getChannel());
            } else {
                sendMessage("봇에게 권한 \"Embed Links\"를 부여해주세요.");
            }
        }

        void sendPrivateMessageToUser(String content, User user) {
            user.openPrivateChannel().queue(c -> sendMessage(content, c));
        }
    }
}
