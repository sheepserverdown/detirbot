package com.ssdown.detirbot;

import com.ssdown.detirbot.config.Configuration;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

@Getter
public class DetirBot {
    private static final Logger log = LoggerFactory.getLogger(DetirBot.class);

    static DetirBot detirBot;

    private String SECRET_TOKEN = "";
    private final Configuration config;
    private JDA jda;

    public DetirBot() {
        DetirBot.detirBot = this;
        config = new Configuration(null,"botConfig.yml");

        try {
            jda = JDABuilder.createDefault(SECRET_TOKEN)
                    .setActivity(Activity.playing("봇 만들어지는중"))
                    .setStatus(OnlineStatus.ONLINE)
                    .addEventListeners(new MessageListener())
                    .build();

        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public static Logger getLogger() {
        return log;
    }

}
