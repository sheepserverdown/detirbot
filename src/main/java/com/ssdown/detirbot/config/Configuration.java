package com.ssdown.detirbot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Properties;

public class Configuration {
    public static final Logger log = LoggerFactory.getLogger(Configuration.class);

    private final String fileName;
    private File folder;

    //생성자 초기화
    public Configuration(File folder, String fileName) {
        this.folder = folder;
        this.fileName = fileName;
    }


}
