package jirubizu.globalhost.config;

import blue.endless.jankson.Comment;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "globalhost")
public class GHConfig implements ConfigData
{
    @Comment("authentication token for ngrok")
    public String authToken = "changeme";
}
