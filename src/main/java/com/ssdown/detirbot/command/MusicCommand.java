package com.ssdown.detirbot.command;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.ssdown.detirbot.audio.AudioInfo;
import com.ssdown.detirbot.audio.AudioPlayerSendHandler;
import com.ssdown.detirbot.audio.TrackManager;
import com.ssdown.detirbot.config.Configuration;
import com.ssdown.detirbot.util.MessageUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.*;

public class MusicCommand extends Command{
    private static final int PLAYLIST_LIMIT = 100; // 큐 갯수 제한
    private static final AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();
    private static final Map<String, Map.Entry<AudioPlayer, TrackManager>> players = new HashMap<>();

    private static final String CD = "\uD83D\uDCBF";
    private static final String DVD = "\uD83D\uDCC0";
    private static final String MIC = "\uD83C\uDFA4 **|>** ";

    private static final String QUEUE_TITLE = "__%s 가 새로운 트랙 %d를 %s 큐에 추가했습니다 : __";
    private static final String QUEUE_DESCRIPTION = "%s **|>** %s\n%s\n%s %s\n%s";
    private static final String QUEUE_INFO = "대기중인 큐 정보 : %d";
    private static final String ERROR = "로딩중 에러가 발생했습니다. : \"%s\"";

    public MusicCommand() {
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
    }

    @Override
    public void executeCommand(String[] args, MessageReceivedEvent e, MessageSender sender) {
        Guild guild = e.getGuild();
        switch(args.length) {
            case 0: // help 메세지
                sendHelpMessage(sender);
                break;

            case 1:
                switch(args[0].toLowerCase()) {
                    case "commands":
                        sendHelpMessage(sender);
                        break;
                    case "info": // 곡 정보 보여주기
                        if(!hasPlayer(guild) || getPlayer(guild).getPlayingTrack() == null) {// 재생중인 음악 없음
                            sender.sendMessage("현재 재생중인 음악이 없습니다.");
                        } else {
                            AudioTrack track = getPlayer(guild).getPlayingTrack();
                            sender.sendEmbed("트랙 정보 : ", String.format(QUEUE_DESCRIPTION, CD, getOrNull(track.getInfo().title),
                                    "\n\u23F1 **|>** `[ " + getTimestamp(track.getPosition()) + " / " + getTimestamp(track.getInfo().length) + " ]`",
                                    "\n" + MIC, getOrNull(track.getInfo().author),
                                    "\n\uD83C\uDFA7 **|>** " + MessageUtil.userDiscrimSet(getTrackManager(guild).getTrackInfo(track).getMember().getUser())));
                        }
                        break;

                    case "queue":
                        if(!hasPlayer(guild) || getTrackManager(guild).getQueuedTracks().isEmpty()) {
                            sender.sendMessage("플레이리스트가 비어있습니다. 다음 커맨드로 곡을 재생해주세요. \n"
                            + "> " + MessageUtil.stripFormatting(Configuration.getInstance().getPrefix()) + "music **");
                        } else {
                            StringBuilder sb = new StringBuilder();
                            Set<AudioInfo> queue = getTrackManager(guild).getQueuedTracks();
                            queue.forEach(audioInfo -> sb.append(buildQueueMessage(audioInfo)));
                            String embedTitle = String.format(QUEUE_INFO, queue.size());

                            sender.sendEmbed(embedTitle, "**>**" + sb.toString());
                        }
                        break;

                    case "skip":
                        if(isIdle(sender, guild)) return; // 재생기의 유휴상태 확인

                        if(isCurrentRequester(e.getMember())) {
                            forceSkipTrack(guild, sender);
                        } else {
                            AudioInfo info = getTrackManager(guild).getTrackInfo(getPlayer(guild).getPlayingTrack());
                            if(info.hasVoted(e.getAuthor())) {
                                sender.sendMessage("이미 현재 곡을 스킵하는것에 투표했습니다.");
                            } else {
                                int votes = info.getSkips();
                                if(votes >= 2) { // 3번째 표가 들어오면 곡을 넘긴다
                                    getPlayer(guild).stopTrack();
                                    sender.sendMessage("현재 곡을 스킵합니다.");
                                } else {
                                    info.addSkip(e.getAuthor());
                                    tryToDelete(e.getMessage());
                                    sender.sendMessage("**" + MessageUtil.userDiscrimSet(e.getAuthor()) + "** 유저가 현재 곡을 스킵하는데 동의했씁니다. [" + (votes + 1) + "/3]");
                                }
                            }
                        }
                        break;

                    case "pause":
                        if(isIdle(sender, guild)) return;

                        pauseMusic(guild, sender);
                        break;

                    case "resume":
                        if(isIdle(sender, guild)) return;

                        resumeMusic(guild, sender);
                        break;

                    case "forceskip":
                        if(isIdle(sender, guild)) return;

                        if(isCurrentRequester(e.getMember()) || isAdmin(e.getMember())) {
                            forceSkipTrack(guild, sender);
                        } else {
                            sender.sendMessage("이 명령어를 실행할 권한이 없습니다. \n"
                            + "**" + MessageUtil.stripFormatting(Configuration.getInstance().getPrefix()) + "music skip** 명령어를 사용하세요.");
                        }
                        break;

                    case "reset":
                        if(isIdle(sender, guild)) return;

                        if(isAdmin(e.getMember())) {
                            sender.sendMessage("이 명령어를 실행할 권한이 없습니다.");
                        } else {
                            reset(guild);
                            sender.sendMessage("현재 재생기 리셋중...");
                        }
                        break;
                }
            default:
                String input = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                switch (args[0].toLowerCase()) {
                    case "search": // 유튜브 뮤직비디오 직접 검색
                        input = "Youtube Search : " + input;
                        // 추가 입력 대기하기
                    case "play": // 바로 재생
                        if(args.length <= 1) {
                            tryToDelete(e.getMessage());
                            sender.sendMessage("정상적인 URL을 입력해 주세요.");
                        } else {
                            loadTrack(input, e.getMember(), e.getMessage(), sender);
                        }
                        break;
                }
                break;
        }
    }

    @Override
    public List<String> getAlias() {
        return Collections.singletonList("music");
    }

    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        if(!players.containsKey(event.getGuild().getId())) // 서버에 음악 재생을 하고있지 않은 상태
            return;

        TrackManager manager = getTrackManager(event.getGuild());
        manager.getQueuedTracks().stream()
                .filter(info -> !info.getTrack().equals(getPlayer(event.getGuild()).getPlayingTrack())
                && info.getMember().getUser().equals(event.getMember().getUser()))
                .forEach(manager::remove);
    }

    // 서버 떠나기
    public void onGuildLeave(GuildLeaveEvent event) {
        reset(event.getGuild());
    }

    // 메세지 삭제
    private void tryToDelete(Message msg) {
        if(msg.getGuild().getSelfMember().hasPermission(msg.getTextChannel(), Permission.MESSAGE_MANAGE)) {
            msg.delete().queue();
        }
    }

    // 현재 서버에 플레이어가 존재하는가?
    private boolean hasPlayer(Guild guild) {
        return players.containsKey(guild.getId());
    }

    // 플레이어 객체 가져오기
    private AudioPlayer getPlayer(Guild guild) {
        AudioPlayer player;
        if(hasPlayer(guild)) {
            player = players.get(guild.getId()).getKey();
        } else {
            player = createPlayer(guild);
        }
        return player;
    }

    // 플레이어에 대한 트랙 가져오기
    private TrackManager getTrackManager(Guild guild) {
        return players.get(guild.getId()).getValue();
    }

    // 플레이어 생성
    private AudioPlayer createPlayer(Guild guild) {
        AudioPlayer player = audioPlayerManager.createPlayer();
        TrackManager manager = new TrackManager(player);
        player.addListener(manager);
        guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));
        players.put(guild.getId(), new AbstractMap.SimpleEntry<>(player, manager));
        return player;
    }

    // 서버 내 플레이어 초기화
    private void reset(Guild guild) {
        players.remove(guild.getId());
        getPlayer(guild).destroy();
        getTrackManager(guild).purgeQueue();
        guild.getAudioManager().closeAudioConnection();
    }

    //
    private void loadTrack(String identifier, Member member, Message msg, Command.MessageSender sender) {
        if(member.getVoiceState().getChannel() == null) {
            sender.sendMessage("보이스 챗에 입장한 후 이용해주세요!");
            return;
        }

        Guild guild = member.getGuild();
        getPlayer(guild); // 이 서버에 오디오 플레이어가 있는지 확인

        msg.getTextChannel().sendTyping().queue();
        audioPlayerManager.loadItemOrdered(guild, identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                sender.sendEmbed(String.format(QUEUE_TITLE, MessageUtil.userDiscrimSet(member.getUser()), 1, ""),
                        String.format(QUEUE_DESCRIPTION, CD, getOrNull(track.getInfo().title), "", MIC, getOrNull(track.getInfo().author), ""));
                getTrackManager(guild).queue(track, member);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if(playlist.getSelectedTrack() != null) {
                    trackLoaded(playlist.getSelectedTrack());
                } else if(playlist.isSearchResult()) {
                    trackLoaded(playlist.getTracks().get(0));
                } else {
                    sender.sendEmbed(String.format(QUEUE_TITLE, MessageUtil.userDiscrimSet(member.getUser()), Math.min(playlist.getTracks().size(), PLAYLIST_LIMIT), "s"),
                            String.format(QUEUE_DESCRIPTION, DVD, getOrNull(playlist.getName()), "", "", "", ""));
                    for(int i = 0; i < Math.min(playlist.getTracks().size(), PLAYLIST_LIMIT); i++) {
                        getTrackManager(guild).queue(playlist.getTracks().get(i), member);
                    }
                }
            }

            @Override
            public void noMatches() {
                sender.sendEmbed(String.format(ERROR, identifier), "재생 가능한 트랙이 존재하지 않습니다.");
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                sender.sendEmbed(String.format(ERROR, identifier), exception.getLocalizedMessage());
            }
        });
        tryToDelete(msg);
    }

    // 명령어 관리자 확인
    private boolean isAdmin(Member member) {
        return member.getId().equals(Configuration.getInstance().getAuthorId());
    }

    //
    private boolean isCurrentRequester(Member member) {
        return getTrackManager(member.getGuild()).getTrackInfo(getPlayer(member.getGuild()).getPlayingTrack()).getMember().equals(member);
    }

    private boolean isIdle(MessageSender sender, Guild guild) {
        if(!hasPlayer(guild) || getPlayer(guild).getPlayingTrack() == null) {
            sender.sendMessage("현재 재생중인 음악이 없습니다!");
            return true;
        }
        return false;
    }

    private void forceSkipTrack(Guild guild, MessageSender sender) {
        getPlayer(guild).stopTrack();
        sender.sendMessage("현재 트랙을 스킵합니다!!");
    }

    private void pauseMusic(Guild guild, MessageSender sender) {
        if(!getPlayer(guild).isPaused()) {
            getPlayer(guild).setPaused(true);
        } else {
            sender.sendMessage("이미 일시정지 중입니다.");
        }
    }

    private void resumeMusic(Guild guild, MessageSender sender) {
        if(getPlayer(guild).isPaused()) {
           getPlayer(guild).setPaused(false);
        } else {
            sender.sendMessage("이미 재생 중입니다.");
        }
    }

    private void sendHelpMessage(MessageSender sender) {
        sender.sendEmbed("Music bot 명령어", MessageUtil.stripFormatting(Configuration.getInstance().getPrefix()) + "music\n"
                + "         -> play [url]           - URL의 음악을 재생합니다.\n"
                + "         -> ytplay [query]  - 유튜브에서 비디오를 검색해 로드합니다.\n"
                + "         -> queue                 - 현재 대기중인 플레이리스트를 확인합니다.\n"
                + "         -> skip                     - 재생중인 트랙을 넘기는 것에 대한 표를 던집니다.\n"
                + "         -> current               - 현재 재생중인 트랙에 대한 정보를 확인합니다.\n"
                + "         -> pause                 - 현재 재생중인 트랙을 일시정지 합니다. \n"
                + "         -> resume                - 현재 재생중인 트랙의 일시정지를 해제합니다. \n"
                + "         -> forceskip**\\***          - 강제 스킵\n"
                + "         -> shuffle**\\***              - 현재 큐 섞기\n"
                + "         -> reset**\\***                 - 뮤직봇 리셋\n\n"
                + "\\* 표시가 있는 명령어는 __**봇 주인만 사용 가능합니다**__."
        );
    }

    private String buildQueueMessage(AudioInfo info) {
        AudioTrackInfo trackInfo = info.getTrack().getInfo();
        String title = trackInfo.title;
        long length = trackInfo.length;
        return "`[ " + getTimestamp(length) + " ]` " + title + "\n";
    }

    private String getTimestamp(long mills) {
        long seconds = mills / 1000;
        long hours = Math.floorDiv(seconds, 3600);
        seconds = seconds - (hours * 3600);
        long mins = Math.floorDiv(seconds, 60);
        seconds = seconds - (mins * 60);
        return (hours == 0 ? "" : hours + ":") + String.format("%02d", mins) + ":" + String.format("%02d", seconds);
    }

    private String getOrNull(String s) {
        return s.isEmpty() ? "N/A" : s;
    }
}