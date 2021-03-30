package com.ssdown.detirbot.command;

import com.ssdown.detirbot.DetirBot;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HelpCommand extends Command {
    private HandlerClass handlerClass = new HandlerClass(DetirBot.getInstance());

    @Override
    public void executeCommand(String[] args, MessageReceivedEvent e, MessageSender chat) {
        Guild guild = e.getGuild();
        handlerClass.setChat(chat);

        if(e.getMember().getId().equals("")) {
            chat.sendMessage("");
        } else {
            chat.sendMessage("권한이 부족합니다.");
        }

        if(args.length > 0) {
            switch(args[0].toLowerCase()) {
                case "start":
                    if (handlerClass.stop) {
                        handlerClass.interrupt();
                        handlerClass = new HandlerClass(chat);
                    }
                    handlerClass.start();
                    break;
                case "stop":
//                handlerClass.interrupt();
                    handlerClass.setStop(true);
                    break;
                case "state":
                    chat.sendMessage("stop : " + handlerClass.stop);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public List<String> getAlias() {
        return Collections.singletonList("help");
    }

    @Setter
    class HandlerClass extends Thread {
        private MessageSender chat;
        private DetirBot detirBot;
        private boolean stop = false;

        public HandlerClass() {}

        public HandlerClass (MessageSender chat) {
            this.chat = chat;
        }

        public HandlerClass (DetirBot detirBot) {
            this.detirBot = detirBot;
        }

        @Override
        public void run() {
            int count = 0;
            while(!stop) {
                try {
                    count++;
                    Thread.sleep(2000);
                } catch(InterruptedException ex) {
                    ex.printStackTrace();
                    chat.sendMessage("스레드 멈췄음 : " + stop);
                }
            }
        }
    }
}
