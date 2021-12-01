package com.ssdown.detirbot;

import com.ssdown.detirbot.command.BlueArchiveCommand;
import com.ssdown.detirbot.command.HelpCommand;
import com.ssdown.detirbot.command.MusicCommand;
import com.ssdown.detirbot.config.Configuration;
import com.ssdown.detirbot.util.TwitterUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class DetirBot {
    private static final Logger logger = LoggerFactory.getLogger(DetirBot.class);

    protected static DetirBot detirBot;

    private String SECRET_TOKEN;
    private Configuration config;
    private JDABuilder jdaBuilder;
    private TwitterUtil twitterUtil;
    private JDA jda;

    public DetirBot() {
        DetirBot.detirBot = this;
        config = new Configuration();

        if(!config.exists()) {
            logger.info("에러가 발생했습니다.");
            System.exit(Constants.EXIT_CODE_NORMAL);
        } else {
            this.SECRET_TOKEN = config.getToken();
            this.twitterUtil = new TwitterUtil();
        }

        List<ListenerAdapter> commandList = new ArrayList<>();
        commandList.add(new MusicCommand());
        commandList.add(new HelpCommand());
        commandList.add(new BlueArchiveCommand());

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

    // 현재 인스턴스 불러올때 사용
    public static DetirBot getInstance() {return detirBot;}

}
