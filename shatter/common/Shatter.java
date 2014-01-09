package shatter.common;

import ichun.core.LoggerHelper;
import ichun.core.config.Config;
import ichun.core.config.ConfigHandler;
import ichun.core.config.IConfigUser;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import shatter.client.core.TickHandlerClient;
import shatter.client.entity.EntityShattered;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = "Shatter", name = "Shatter",
			version = Shatter.version,
			dependencies = "required-after:iChunUtil@[2.4.0,)"
				)
@NetworkMod(clientSideRequired = false,
			serverSideRequired = false
				)
public class Shatter 
	implements IConfigUser
{
	public static final String version = "2.0.2";
	
	private static final Logger logger = LoggerHelper.createLogger("Shatter");
	
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
		
		config.createOrUpdateIntProperty("clientOnly", "Client Only", "enableBossShatter", "Enable Shatter - Boss mobs", "The reason this is disabled by default is due to rendering issues that may occur due to some boss' unique models.\nGood examples are the Minecraft's Ender Dragon, and TwilightForest's Naga and Hydra.\n\n0 = No\n1 = Yes", true, 0, 0, 1);
		config.createOrUpdateIntProperty("clientOnly", "Client Only", "enablePlayerShatter", "Enable Shatter - Player mobs", "Enable Shatter on Players?.\n\n0 = No\n1 = Yes", true, 1, 0, 1);
		config.createOrUpdateIntProperty("clientOnly", "Client Only", "enableChildShatter", "Enable Shatter - Child mobs", "The reason this is disabled by default is due to rendering issues that occur due to the way Minecraft rescales children.\nShattering child mobs will show an adult model.\n\n0 = No\n1 = Yes", true, 0, 0, 1);
		
		init();
	}

	@SideOnly(Side.CLIENT)
	public static void init()
	{
		tickHandlerClient = new TickHandlerClient();
		TickRegistry.registerTickHandler(tickHandlerClient, Side.CLIENT);
		
		MinecraftForge.EVENT_BUS.register(instance);
		
		RenderingRegistry.registerEntityRenderingHandler(EntityShattered.class, tickHandlerClient.renderShatteredInstance);
	}
	
	@ForgeSubscribe
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
    	logger.log(warning ? Level.WARNING : Level.INFO, sb.append("[").append(version).append("] ").append(s).toString());
    }
}
