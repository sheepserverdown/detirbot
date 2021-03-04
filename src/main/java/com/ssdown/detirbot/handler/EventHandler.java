package com.ssdown.detirbot.handler;

import com.ssdown.detirbot.DetirBot;

public abstract class EventHandler {
    // detirbot 클래스 인스턴스는 다른 애플리케이션에서 액세스하고 상호작용 하기위함
    protected final DetirBot detirBot;

    protected EventHandler(DetirBot detirBot) {
        this.detirBot = detirBot;
    }
}
