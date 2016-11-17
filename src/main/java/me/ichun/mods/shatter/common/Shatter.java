package me.ichun.mods.shatter.common;

import me.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.update.UpdateChecker;
import me.ichun.mods.shatter.client.core.TickHandlerClient;
import me.ichun.mods.shatter.client.entity.EntityShattered;
import me.ichun.mods.shatter.client.model.ModelShattered;
import me.ichun.mods.shatter.client.render.RenderShattered;
import me.ichun.mods.shatter.common.core.Config;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = "shatter", name = "Shatter",
        version = Shatter.VERSION,
        dependencies = "required-after:ichunutil@[" + iChunUtil.VERSION_OF_MC +".0.0,)",
        clientSideOnly = true
)
public class Shatter
{
    public static final String VERSION = iChunUtil.VERSION_OF_MC + ".0.0";

    private static final Logger LOGGER = LogManager.getLogger("Shatter");

    @Instance("Shatter")
    public static Shatter instance;

    public static Config config;

    public static TickHandlerClient tickHandlerClient;

    @EventHandler
    public void preLoad(FMLPreInitializationEvent event)
    {
        if(FMLCommonHandler.instance().getEffectiveSide().isServer())
        {
            console("You're loading Shatter on a server! This is a client-only mod!", true);
            return;
        }

        config = (Config)ConfigHandler.registerConfig(new Config(event.getSuggestedConfigurationFile()));

        UpdateChecker.registerMod(new UpdateChecker.ModVersionInfo("Shatter", iChunUtil.VERSION_OF_MC, VERSION, false));
    }

    @SideOnly(Side.CLIENT)
    @EventHandler
    public void load(FMLInitializationEvent event)
    {
        tickHandlerClient = new TickHandlerClient();
        FMLCommonHandler.instance().bus().register(tickHandlerClient);

        MinecraftForge.EVENT_BUS.register(instance);

        RenderingRegistry.registerEntityRenderingHandler(EntityShattered.class, new RenderShattered(new ModelShattered(), 0.0F));
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event)
    {
        if(FMLCommonHandler.instance().getEffectiveSide().isClient())
        {
            if(config.enableBossShatter == 0 && event.getEntityLiving() instanceof IBossDisplayData || config.enableChildShatter == 0 && event.getEntityLiving().isChild())
            {
                return;
            }
            tickHandlerClient.shatterTimeout.put(event.getEntityLiving(), 2);
        }
    }

    public static void console(String s, boolean warning)
    {
        StringBuilder sb = new StringBuilder();
        LOGGER.log(warning ? Level.WARN : Level.INFO, sb.append("[").append(VERSION).append("] ").append(s).toString());
    }
}
