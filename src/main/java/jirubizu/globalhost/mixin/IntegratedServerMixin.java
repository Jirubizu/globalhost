package jirubizu.globalhost.mixin;

import jirubizu.globalhost.config.GHConfig;
import jirubizu.globalhost.utilities.NgrokUtilites;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(IntegratedServer.class)
public class IntegratedServerMixin
{
    @Shadow
    @Final
    private static Logger LOGGER;
    private Process authenticationProcess;
    private Process updateProcess;
    private Process runningProcess;

    @Inject(method = "openToLan", at = @At("HEAD"))
    private void onOpenToLan(GameMode gameMode, boolean cheatsAllowed, int port, CallbackInfoReturnable<Boolean> cir)
    {
        try
        {
            GHConfig config = AutoConfig.getConfigHolder(GHConfig.class).getConfig();
            LOGGER.info("Starting NGROK");
            authenticationProcess = Runtime.getRuntime().exec(NgrokUtilites.getNgrokFile() + " authtoken " + config.authToken);
            LOGGER.info("Updating tunneling service (ngrok)");
            updateProcess = Runtime.getRuntime().exec(NgrokUtilites.getNgrokFile() + " update");
            LOGGER.info("Starting tunnel...");
            runningProcess = Runtime.getRuntime().exec(NgrokUtilites.getNgrokFile() + " tcp " + port + " --log ngrok.log");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Inject(method = "openToLan", at = @At("RETURN"))
    private void onOpenToLanReturn(GameMode gameMode, boolean cheatsAllowed, int port, CallbackInfoReturnable<Boolean> cir)
    {
        final boolean[] found = {false};
        Thread thread = new Thread(() ->
        {
            File logFile = new File("ngrok.log");
            while (!found[0])
            {
                if (logFile.exists() && logFile.length() > 0)
                {
                    try
                    {
                        String log = "";
                        Scanner scanner = new Scanner(logFile);
                        while (scanner.hasNextLine())
                        {
                            log += "\n" + scanner.nextLine();
                        }
                        scanner.close();
                        if (!log.contains("failed to auth") && !log.contains("url=tcp://"))
                        {
                            continue;
                        }
                        MinecraftClient minecraftClient = MinecraftClient.getInstance();
                        if (log.contains("failed to auth"))
                        {
                            minecraftClient.inGameHud.getChatHud().addMessage(Text.of("Make sure you have set the correct auth token for ngrok from https://dashboard.ngrok.com/get-started/your-authtoken"));
                            found[0] = true;
                            return;
                        }
                        Pattern pattern = Pattern.compile("url=tcp://(.*)\\s?");
                        Matcher matcher = pattern.matcher(log);
                        while (matcher.find())
                        {
                            minecraftClient.inGameHud.getChatHud().addMessage(Text.of("IP for the server is: " + matcher.group(1)));
                            minecraftClient.keyboard.setClipboard(matcher.group(1));
                            found[0] = true;
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

}
