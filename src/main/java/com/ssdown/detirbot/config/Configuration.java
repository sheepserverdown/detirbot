package com.ssdown.detirbot.config;

import com.ssdown.detirbot.Constants;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Properties;

@Getter
public class Configuration extends PropertyConfiguration {
    public static final Logger log = LoggerFactory.getLogger(Configuration.class);

    private static Configuration instance;

    private final File configFile;
    private final String token;
    private final String prefix;
    private final String authorId;

    private final String consumerKey;
    private final String consumerSecret;
    private final String bearerToken;
    private final String accessToken;
    private final String accessTokenSecret;

    public Configuration() {
        getProperty(getClass().getClassLoader(), Constants.propertyFilename);

        this.configFile = getFile();

        this.token = properties.getProperty("token");
        this.prefix = properties.getProperty("prefix");
        this.authorId = properties.getProperty("authorId");

        this.consumerKey = properties.getProperty("oauth.consumerKey");
        this.consumerSecret = properties.getProperty("oauth.consumerSecret");
        this.bearerToken = properties.getProperty("bearerToken");
        this.accessToken = properties.getProperty("oauth.accessToken");
        this.accessTokenSecret = properties.getProperty("oauth.accessTokenSecret");
    }

    //인스턴스 생성 후 값을 받아오기 위함
    public static Configuration getInstance() {
        if(instance == null) {
            instance = new Configuration();
        }

        return instance;
    }

    //파일 존재 여부 확인
    public boolean exists() {
        return configFile != null && configFile.exists() && configFile.isFile();
    }
}
