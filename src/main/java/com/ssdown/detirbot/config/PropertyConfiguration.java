package com.ssdown.detirbot.config;

import com.ssdown.detirbot.exception.FailedLoadPropertyException;

import java.io.IOException;
import java.util.Properties;


public class PropertyConfiguration {

    //오버라이딩 금지
   protected final Properties properties = new Properties();

   protected void getProperty(ClassLoader classLoader, String fileName) {
        try {
            properties.load(classLoader.getResourceAsStream(fileName));
        } catch (IOException e) {
            throw new FailedLoadPropertyException(fileName + "파일을 불러오는데 실패하였습니다.", e);
        }
   }


}
