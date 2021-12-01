package com.ssdown.detirbot.config;

import lombok.Getter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@Getter
public class TwitterConfiguration {
    private final TwitterFactory tf;
    public TwitterConfiguration() {
        ConfigurationBuilder cb = new ConfigurationBuilder();

        cb.setDebugEnabled(true)
        .setOAuthConsumerKey(Configuration.getInstance().getConsumerKey())
        .setOAuthConsumerSecret(Configuration.getInstance().getConsumerSecret())
        .setOAuthAccessToken(Configuration.getInstance().getAccessToken())
        .setOAuthAccessTokenSecret(Configuration.getInstance().getAccessTokenSecret());

        tf = new TwitterFactory(cb.build());
    }
}
