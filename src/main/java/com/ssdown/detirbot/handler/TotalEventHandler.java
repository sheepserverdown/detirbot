package com.ssdown.detirbot.handler;

import com.ssdown.detirbot.DetirBot;
import com.ssdown.detirbot.command.MusicCommand;

public class TotalEventHandler extends EventHandler {
    private final MusicCommand musicCommand;

    public TotalEventHandler(DetirBot detirBot) {
        super(detirBot);

        this.musicCommand = new MusicCommand();
    }
}
