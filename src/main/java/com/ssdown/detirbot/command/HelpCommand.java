package com.ssdown.detirbot.command;

import com.ssdown.detirbot.DetirBot;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;

@Slf4j
public class HelpCommand extends Command {

    private HandlerClass handlerClass = new HandlerClass(DetirBot.getInstance());

    @Override
    public void executeCommand(String[] args, MessageReceivedEvent e, MessageSender chat) {
        Guild guild = e.getGuild();
        handlerClass.setChat(chat);

//        if(e.getMember().getId().equals("")) {
//            chat.sendMessage("");
//        } else {
//            chat.sendMessage("권한이 부족합니다.");
//        }

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

    public void requestURL(MessageSender chat) throws IOException {
        URL url = new URL("https://twitter.com/Blue_ArchiveJP");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0");

        int status = con.getResponseCode();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        log.info(content.toString());

        con.disconnect();
    }

    @Override
    public List<String> getAlias() {
        return Collections.singletonList("help");
    }

    @Setter
    public static class HandlerClass extends Thread {
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
                    chat.sendMessage(String.valueOf(count));
                    Thread.sleep(2000);
                } catch(InterruptedException ex) {
                    ex.printStackTrace();
                    chat.sendMessage("스레드 멈췄음 : " + stop);
                }
            }
        }
    }
}
