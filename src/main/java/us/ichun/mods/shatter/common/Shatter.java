package us.ichun.mods.shatter.common;

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
import us.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import us.ichun.mods.ichunutil.common.core.updateChecker.ModVersionChecker;
import us.ichun.mods.ichunutil.common.core.updateChecker.ModVersionInfo;
import us.ichun.mods.ichunutil.common.iChunUtil;
import us.ichun.mods.shatter.client.core.TickHandlerClient;
import us.ichun.mods.shatter.client.entity.EntityShattered;
import us.ichun.mods.shatter.client.model.ModelShattered;
import us.ichun.mods.shatter.client.render.RenderShattered;
import us.ichun.mods.shatter.common.core.Config;

@Mod(modid = "Shatter", name = "Shatter",
        version = Shatter.version,
        dependencies = "required-after:iChunUtil@[" + iChunUtil.versionMC +".0.0,)"
)
public class Shatter
{
    public static final String version = iChunUtil.versionMC + ".0.0";

    private static final Logger logger = LogManager.getLogger("Shatter");

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

        ModVersionChecker.register_iChunMod(new ModVersionInfo("Shatter", iChunUtil.versionOfMC, version, false));
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
            if(config.enableBossShatter == 0 && event.entityLiving instanceof IBossDisplayData || config.enableChildShatter == 0 && event.entityLiving.isChild())
            {
                return;
            }
            tickHandlerClient.shatterTimeout.put(event.entityLiving, 2);
        }
    }

    public static void console(String s, boolean warning)
    {
        StringBuilder sb = new StringBuilder();
        logger.log(warning ? Level.WARN : Level.INFO, sb.append("[").append(version).append("] ").append(s).toString());
    }
}
