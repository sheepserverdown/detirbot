package com.ssdown.detirbot.command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HelpCommand extends Command {
    @Override
    public void executeCommand(String[] args, MessageReceivedEvent e, MessageSender chat) {
        Guild guild = e.getGuild();

//        chat.sendMessage(Arrays.toString(args));

        Thread thread = new Thread(new Runnable() {
            int count = 0;
            @Override
            public void run() {
                while(true) {
                    chat.sendMessage(Integer.toString(count));
                    count++;
                    try {
                       Thread.sleep(1000);
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        switch(args[0].toLowerCase()) {
            case "start":
                thread.start();
                break;
            case "stop":
                thread.interrupt();
                break;
        }
    }

    @Override
    public List<String> getAlias() {
        return Collections.singletonList("help");
    }
}
