package com.ssdown.detirbot.config;

import com.ssdown.detirbot.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Properties;

public class Configuration extends PropertyConfiguration {
    public static final Logger log = LoggerFactory.getLogger(Configuration.class);

    private static Configuration instance;

    private File configFile;
    public final String privateToken;

    public Configuration() {
        this.configFile = new File(Constants.propertyDirectory, Constants.propertyFilename);

        getProperty(getClass().getClassLoader(), Constants.propertyFilename);

        this.privateToken = properties.getProperty("token");
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
