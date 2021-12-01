package com.ssdown.detirbot.command;

import com.ssdown.detirbot.config.Configuration;
import com.ssdown.detirbot.util.TwitterUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import twitter4j.*;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class BlueArchiveCommand extends Command{
    @Override
    public void executeCommand(String[] args, MessageReceivedEvent e, MessageSender chat) {
        Guild guild = e.getGuild();

        if(args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "recent":
//                    if(!e.getMember().getId().equals(Configuration.getInstance().getAuthorId())) {
//                        chat.sendMessage("메롱");
//                    } else {
                        e.getMessage().delete().queue();
                        Twitter twitter = TwitterUtil.getInstance();
                        try {
                            String twitterId = "Blue_ArchiveJP";
                            ResponseList<Status> targetUserTimeline = twitter.getUserTimeline(twitterId);

                            Status recentTweetInfo = targetUserTimeline.get(0);
                            User targetUser = twitter.showUser(twitterId);

                            EmbedBuilder embedBuilder = new EmbedBuilder();
//                            embedBuilder.setTitle("Latest " + targetUser.getName() + " Official Tweet");
                            embedBuilder.setColor(Color.CYAN);
                            embedBuilder.setAuthor(targetUser.getName(), targetUser.getURL(), targetUser.getProfileImageURL());
                            embedBuilder.addField("Retweet Count", String.valueOf(recentTweetInfo.getRetweetCount()), true);
                            embedBuilder.addField("Favorite Count", String.valueOf(recentTweetInfo.getFavoriteCount()), true);
                            embedBuilder.addField("Content", recentTweetInfo.getText(), false);
//                            embedBuilder.setImage(targetUser.getMiniProfileImageURL());
                            embedBuilder.setFooter("Created By : " + recentTweetInfo.getCreatedAt(), targetUser.getMiniProfileImageURL());

                            e.getChannel().sendMessage(embedBuilder.build()).queue();
                        } catch (TwitterException twitterException) {
                            twitterException.printStackTrace();
                        }
//                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public List<String> getAlias() {
        return Collections.singletonList("blue");
    }
}
