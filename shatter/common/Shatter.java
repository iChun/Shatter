package shatter.common;

import ichun.core.LoggerHelper;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraftforge.common.MinecraftForge;
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
{
	public static final String version = "2.0.0";
	
	private static final Logger logger = LoggerHelper.createLogger("Shatter");
	
	@Instance("Shatter")
	public static Shatter instance;

	public static TickHandlerClient tickHandlerClient;

	@EventHandler
	public void preLoad(FMLPreInitializationEvent event)
	{
		if(FMLCommonHandler.instance().getEffectiveSide().isServer())
		{
			console("You're loading Shatter on a server! This is a client-only mod!", true);
			return;
		}
		
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
		if(FMLCommonHandler.instance().getEffectiveSide().isClient() && !(event.entityLiving instanceof IBossDisplayData) && !event.entityLiving.isChild())
		{
			tickHandlerClient.shatterTimeout.put(event.entityLiving, 2);
		}
	}

    public static void console(String s, boolean warning)
    {
    	StringBuilder sb = new StringBuilder();
    	logger.log(warning ? Level.WARNING : Level.INFO, sb.append("[").append(version).append("] ").append(s).toString());
    }
}
