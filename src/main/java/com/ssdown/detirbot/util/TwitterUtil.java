package com.ssdown.detirbot.util;

import com.ssdown.detirbot.config.TwitterConfiguration;
import twitter4j.Twitter;

public class TwitterUtil {
    private final TwitterConfiguration twitterConfiguration;
    protected static Twitter twitter;
//    private final DetirBot detirBot;
    public TwitterUtil() {
        this.twitterConfiguration = new TwitterConfiguration();
        twitter = twitterConfiguration.getTf().getInstance();
    }

    public static Twitter getInstance() {
        return twitter;
    }
}
