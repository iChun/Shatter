package shatter.common;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ichun.common.core.config.Config;
import ichun.common.core.config.ConfigHandler;
import ichun.common.core.config.IConfigUser;

import ichun.common.core.updateChecker.ModVersionChecker;
import ichun.common.core.updateChecker.ModVersionInfo;
import ichun.common.iChunUtil;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shatter.client.core.TickHandlerClient;
import shatter.client.entity.EntityShattered;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = "Shatter", name = "Shatter",
			version = Shatter.version,
			dependencies = "required-after:iChunUtil@[" + iChunUtil.versionMC +".0.0,)"
				)
public class Shatter
	implements IConfigUser
{
	public static final String version = iChunUtil.versionMC + ".0.0";
	
	private static final Logger logger = LogManager.getLogger("Shatter");
	
	@Instance("Shatter")
	public static Shatter instance;

	public static Config config;
	
	public static TickHandlerClient tickHandlerClient;
	
	@Override
	public boolean onConfigChange(Config cfg, Property prop) { return true; }

	@EventHandler
	public void preLoad(FMLPreInitializationEvent event)
	{
		if(FMLCommonHandler.instance().getEffectiveSide().isServer())
		{
			console("You're loading Shatter on a server! This is a client-only mod!", true);
			return;
		}
		
		config = ConfigHandler.createConfig(event.getSuggestedConfigurationFile(), "shatter", "Shatter", logger, instance);

        config.setCurrentCategory("clientOnly", "shatter.config.cat.clientOnly.name", "shatter.config.cat.clientOnly.comment");
        config.createIntBoolProperty("enableBossShatter", "shatter.config.prop.enableBossShatter.name", "shatter.config.prop.enableBossShatter.comment", true, false, false);
		config.createIntBoolProperty("enablePlayerShatter", "shatter.config.prop.enablePlayerShatter.name", "shatter.config.prop.enablePlayerShatter.comment", true, false, true);
		config.createIntBoolProperty("enableChildShatter", "shatter.config.prop.enableChildShatter.name", "shatter.config.prop.enableChildShatter.comment", true, false, false);

        ModVersionChecker.register_iChunMod(new ModVersionInfo("Shatter", "1.7", version, false));

		init();
	}

	@SideOnly(Side.CLIENT)
	public static void init()
	{
		tickHandlerClient = new TickHandlerClient();
        FMLCommonHandler.instance().bus().register(tickHandlerClient);

		MinecraftForge.EVENT_BUS.register(instance);
		
		RenderingRegistry.registerEntityRenderingHandler(EntityShattered.class, tickHandlerClient.renderShatteredInstance);
	}
	
	@SubscribeEvent
	public void onLivingDeath(LivingDeathEvent event)
	{
        if(FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			if(config.getInt("enableBossShatter") == 0 && event.entityLiving instanceof IBossDisplayData || config.getInt("enableChildShatter") == 0 && event.entityLiving.isChild())
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
