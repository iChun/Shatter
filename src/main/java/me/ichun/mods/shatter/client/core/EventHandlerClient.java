package me.ichun.mods.shatter.client.core;

import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.shatter.client.entity.EntityShattered;
import me.ichun.mods.shatter.client.render.RenderShattered;
import me.ichun.mods.shatter.common.Shatter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EventHandlerClient
{
    public HashMap<EntityLivingBase, Integer> shatterTimeout = new HashMap<>();
    public ArrayList<EntityPlayer> deadPlayers = new ArrayList<>();

    public void initMod()
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityShattered.class, new RenderShattered.RenderFactory());
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event)
    {
        if(event.getEntityLiving().getEntityWorld().isRemote)
        {
            if(Shatter.config.enableBossShatter == 0 && !event.getEntityLiving().isNonBoss() || Shatter.config.enableChildShatter == 0 && event.getEntityLiving().isChild())
            {
                return;
            }
            shatterTimeout.put(event.getEntityLiving(), 2);
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if(!(event.phase == TickEvent.Phase.END && Minecraft.getMinecraft().world != null))
        {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        WorldClient world = mc.world;

        if(!mc.isGamePaused())
        {
            Iterator<Map.Entry<EntityLivingBase, Integer>> ite = shatterTimeout.entrySet().iterator();
            if(ite.hasNext())
            {
                Map.Entry<EntityLivingBase, Integer> e = ite.next();

                e.setValue(e.getValue() - 1);

                EntityLivingBase ent = e.getKey();

                if(iChunUtil.hasMorphMod() && ent instanceof EntityPlayer && me.ichun.mods.morph.api.MorphApi.getApiImpl().hasMorph(ent.getName(), Side.CLIENT))
                {
                    ent = me.ichun.mods.morph.api.MorphApi.getApiImpl().getMorphEntity(ent.getEntityWorld(), ent.getName(), Side.CLIENT);
                }

                ent.hurtTime = 0;
                ent.deathTime = 0;

                if(e.getValue() <= 0)
                {
                    if(ent.world == world)
                    {
                        ent.world.spawnEntity(new EntityShattered(ent.world, ent));
                        ent.setDead();
                    }
                    ite.remove();
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if(event.side == Side.SERVER || event.phase != TickEvent.Phase.END)
        {
            return;
        }
        World world = event.player.world;
        EntityPlayer player = event.player;

        if(Shatter.config.enablePlayerShatter == 1)
        {
            if(!player.isEntityAlive() && !shatterTimeout.containsKey(player) && !deadPlayers.contains(player))
            {
                deadPlayers.add(player);
                shatterTimeout.put(player, 2);
            }
            for(int k = deadPlayers.size() - 1; k >= 0; k--)
            {
                EntityPlayer deadPlayer = deadPlayers.get(k);
                if(deadPlayer.world != world || deadPlayer.getName().equals(player.getName()) && deadPlayer != player)
                {
                    deadPlayers.remove(k);
                }
            }
        }
    }
}
