package top.spco.config;

import top.spco.core.config.Config;
import top.spco.core.config.ConfigSpecHelper;

public class WikiRenderConfig extends Config {
    public WikiRenderConfig() {
        super("wiki_render");
        init();
    }

    private final ConfigSpecHelper.ConfigValue<String> WEB_RENDER_URL = builder
            .comment("e.g. http://localhost:15551")
            .define("web_render_url", "");
    private final ConfigSpecHelper.ConfigValue<String> DEFAULT_WIKI_URL = builder
            .comment("Default wiki entry URL, e.g. https://zh.wikipedia.org/wiki/%s")
            .define("default_wiki_api", "https://zh.wikipedia.org/wiki/%s");
    private final ConfigSpecHelper.ConfigValue<String> DEFAULT_WIKI_API = builder
            .define("default_wiki_api", "https://zh.wikipedia.org/w/api.php");

    public String getWebRenderUrl() {
        return WEB_RENDER_URL.get();
    }

    public String getDefaultWikiUrl() {
        return DEFAULT_WIKI_URL.get();
    }

    public String getDefaultWikiApi() {
        return DEFAULT_WIKI_API.get();
    }
}
