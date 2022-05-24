package me.ichun.mods.shatter.common;

import me.ichun.mods.shatter.client.core.EventHandler;
import me.ichun.mods.shatter.client.entity.EntityShattered;
import me.ichun.mods.shatter.client.render.RenderShattered;
import me.ichun.mods.shatter.common.core.Config;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Shatter.MOD_ID)
public class Shatter
{
    public static final String MOD_ID = "shatter";
    public static final String MOD_NAME = "Shatter";

    public static final Logger LOGGER = LogManager.getLogger();

    public static Config config;

    public static EventHandler eventHandler;

    public Shatter()
    {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            config = new Config().init();

            MinecraftForge.EVENT_BUS.register(eventHandler = new EventHandler());
            IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
            bus.addGenericListener(EntityType.class, EntityTypes::onEntityTypeRegistry);
            bus.addListener(this::onClientSetup);
        });
        DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, () -> () -> LOGGER.log(Level.ERROR, "You are loading " + MOD_NAME + " on a server. " + MOD_NAME + " is a client only mod!"));

        //Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityTypes.SHATTERED_TYPE, new RenderShattered.RenderFactory());
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> me.ichun.mods.ichunutil.client.core.EventHandlerClient::getConfigGui);
    }

    public static boolean hasMorphMod()
    {
        return ModList.get().isLoaded("morph");
    }

    public static class EntityTypes
    {
        public static EntityType<EntityShattered> SHATTERED_TYPE;
        public static void onEntityTypeRegistry(final RegistryEvent.Register<EntityType<?>> entityTypeRegistryEvent) //we're doing it this way because it's a client-side entity and we don't want to sync registry values
        {
            SHATTERED_TYPE = EntityType.Builder.create(EntityShattered::new, EntityClassification.MISC)
                    .size(0.1F, 0.1F)
                    .disableSerialization()
                    .disableSummoning()
                    .immuneToFire()
                    .build("from " + MOD_NAME + ". Ignore this.");
        }
    }
}
