package me.ichun.mods.shatter.common;

import me.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.update.UpdateChecker;
import me.ichun.mods.shatter.client.core.EventHandlerClient;
import me.ichun.mods.shatter.common.core.Config;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Shatter.MOD_ID, name = Shatter.MOD_NAME,
        version = Shatter.VERSION,
        guiFactory = iChunUtil.GUI_CONFIG_FACTORY,
        dependencies = "required-after:ichunutil@[" + iChunUtil.VERSION_MAJOR + ".0.1," + (iChunUtil.VERSION_MAJOR + 1) + ".0.0)",
        acceptableRemoteVersions = "[" + iChunUtil.VERSION_MAJOR + ".0.0," + iChunUtil.VERSION_MAJOR + ".1.0)",
        acceptedMinecraftVersions = iChunUtil.MC_VERSION_RANGE,
        clientSideOnly = true
)
public class Shatter
{
    public static final String MOD_ID = "shatter";
    public static final String MOD_NAME = "Shatter";
    public static final String VERSION = iChunUtil.VERSION_MAJOR + ".0.1";

    @Instance(MOD_ID)
    public static Shatter instance;

    public static Config config;

    public static EventHandlerClient eventHandlerClient;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        config = ConfigHandler.registerConfig(new Config(event.getSuggestedConfigurationFile()));

        eventHandlerClient = new EventHandlerClient();
        MinecraftForge.EVENT_BUS.register(eventHandlerClient);

        eventHandlerClient.initMod();

        UpdateChecker.registerMod(new UpdateChecker.ModVersionInfo(MOD_NAME, iChunUtil.VERSION_OF_MC, VERSION, true));
    }
}
