package jirubizu.globalhost.client;

import jirubizu.globalhost.config.GHConfig;
import jirubizu.globalhost.utilities.NgrokUtilites;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GlobalhostClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        NgrokUtilites.CheckNgrok();
        AutoConfig.register(GHConfig.class, GsonConfigSerializer::new);
    }

}
