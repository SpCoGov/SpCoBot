package top.spco.modules;

import top.spco.api.Bot;
import top.spco.api.Interactive;
import top.spco.api.message.Message;
import top.spco.config.Configs;
import top.spco.core.module.AbstractModule;
import top.spco.events.MessageEvents;

import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 渲染Wiki页面。
 *
 * @author SpCo
 * @version 4.1.0
 * @since 4.1.0
 */
public class WikiRender extends AbstractModule {
    private static final Map<String, InterwikiInfo> wikiMap = new HashMap<>();

    static {
        addInterwiki(new InterwikiInfo("moe", "https://zh.moegirl.org.cn/%s", "https://zh.moegirl.org.cn/api.php"));
        addInterwiki(new InterwikiInfo("mcwzh", "https://zh.minecraft.wiki/w/%s", "https://zh.minecraft.wiki/api.php"));
        addInterwiki(new InterwikiInfo("prts", "https://prts.wiki/w/%s", "https://prts.wiki/api.php"));
    }

    public WikiRender() {
        super("WikiRender");
    }

    @Override
    public void init() {
        MessageEvents.GROUP_MESSAGE.register(((bot, source, sender, message, time) -> {
            onMessage(bot, sender, source, message, time);
        }));
        MessageEvents.PRIVATE_MESSAGE.register((bot, source, message, time) -> {
            onMessage(bot, source, source, message, time);
        });

    }

    private void onMessage(Bot<?> bot, Interactive<?> sender, Interactive<?> source, Message message, int time) {
        if (!isActive()) {
            return;
        }
        try {
            if (!isAvailable(source)) {
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        List<String> query = processMessage(message.toMessageContext());
        LinkedHashMap<String, String> urlAndTitle = getQueryUrlAndTitle(query);
    }



    private static List<String> processMessage(String message) {
        String regex = "\\[\\[(.*?)]]";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

        Matcher matcher = pattern.matcher(message);

        List<String> queryList = new ArrayList<>();

        while (matcher.find()) {
            String match = matcher.group(1);  // Get the content inside the brackets
            if (!match.isEmpty() && !queryList.contains(match) && !match.startsWith("#")) {
                queryList.add(match.split("\\|")[0]);  // Split at '|' and take the first part
            }
        }

        // If there are any valid queries, process them
        if (!queryList.isEmpty()) {
            // Limit to the first 5 queries
            queryList = queryList.subList(0, Math.min(5, queryList.size()));

            return queryList;
        }
        return queryList;
    }

    private static LinkedHashMap<String, String> getQueryUrlAndTitle(List<String> query) {
        LinkedHashMap<String, String> urlTitle = new LinkedHashMap<>();
        for (String s : query) {
            String[] split = s.split(":", 2);
            if (split.length == 1) {
                String title = split[0];
                String link = String.format(Configs.WIKI_RENDER.getDefaultWikiUrl(), title);
                urlTitle.put(link, title);
            } else {
                String interwikiPrefix = split[0];
                String link;
                String title;
                if (wikiMap.containsKey(interwikiPrefix)) {
                    title = split[1];
                    link = String.format(wikiMap.get(interwikiPrefix).url, title);
                } else {
                    title = s;
                    link = String.format(Configs.WIKI_RENDER.getDefaultWikiUrl(), title);
                }
                urlTitle.put(link, title);
            }
        }
        return urlTitle;
    }

    public static void addInterwiki(InterwikiInfo interwiki) {
        wikiMap.put(interwiki.prefix, interwiki);
    }

    public record InterwikiInfo(String prefix, String url, String apiUrl) {
    }
}
