package com.ssdown.detirbot;

import com.ssdown.detirbot.command.HelpCommand;
import com.ssdown.detirbot.command.MusicCommand;
import com.ssdown.detirbot.config.Configuration;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class DetirBot {
    private static final Logger logger = LoggerFactory.getLogger(DetirBot.class);

    //Singleton 패턴으로 접근 가능하게
    protected static DetirBot detirBot;

    private String SECRET_TOKEN;
    private Configuration config;
    private JDABuilder jdaBuilder;
    private JDA jda;

    public DetirBot() {
        DetirBot.detirBot = this;
        config = new Configuration();

        if(!config.exists()) {
            logger.info("에러가 발생했습니다.");
            System.exit(Constants.EXIT_CODE_NORMAL);
        } else {
            this.SECRET_TOKEN = config.getToken();
        }

        List<ListenerAdapter> commandList = new ArrayList<>();
        commandList.add(new MusicCommand());
        commandList.add(new HelpCommand());

        try {
            jdaBuilder = JDABuilder.createDefault(SECRET_TOKEN)
                    .setActivity(Activity.playing("봇 만들어지는중"))
                    .setStatus(OnlineStatus.ONLINE);
            for(ListenerAdapter listenerAdapter : commandList) {
                jdaBuilder.addEventListeners(listenerAdapter);
            }

            jda = jdaBuilder.build();

        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public static Logger getLogger() {
        return logger;
    }

    public static DetirBot getInstance() {return detirBot;}

}
