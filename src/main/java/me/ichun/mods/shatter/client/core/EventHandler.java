package me.ichun.mods.shatter.client.core;

import me.ichun.mods.ichunutil.client.tracker.ClientEntityTracker;
import me.ichun.mods.shatter.client.entity.EntityShattered;
import me.ichun.mods.shatter.common.Shatter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

public class EventHandler
{
    public IdentityHashMap<LivingEntity, Integer> shatterTimeout = new IdentityHashMap<>();
    public ArrayList<PlayerEntity> deadPlayers = new ArrayList<>();

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event)
    {
        if(event.getEntityLiving().getEntityWorld().isRemote)
        {
            if(!Shatter.config.enableBossShatter && !event.getEntityLiving().isNonBoss() || !Shatter.config.enableChildShatter && event.getEntityLiving().isChild())
            {
                return;
            }
            shatterTimeout.put(event.getEntityLiving(), 2);
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if(!(event.phase == TickEvent.Phase.END && Minecraft.getInstance().world != null))
        {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        ClientWorld world = mc.world;

        if(!mc.isGamePaused())
        {
            Iterator<Map.Entry<LivingEntity, Integer>> ite = shatterTimeout.entrySet().iterator();
            if(ite.hasNext())
            {
                Map.Entry<LivingEntity, Integer> e = ite.next();

                e.setValue(e.getValue() - 1);

                LivingEntity ent = e.getKey();

                //                if(iChunUtil.hasMorphMod() && ent instanceof PlayerEntity && me.ichun.mods.morph.api.MorphApi.getApiImpl().hasMorph(ent.getName(), Side.CLIENT))
                //                {
                //                    ent = me.ichun.mods.morph.api.MorphApi.getApiImpl().getMorphEntity(ent.getEntityWorld(), ent.getName(), Side.CLIENT);
                //                }

                ent.hurtTime = 0;
                ent.deathTime = 0;

                if(e.getValue() <= 0)
                {
                    if(ent.world == world)
                    {
                        EntityShattered shattered = Shatter.EntityTypes.SHATTERED.get().create(ent.world).setAcquired(ent);
                        shattered.setEntityId(ClientEntityTracker.getNextEntId());
                        ((ClientWorld)ent.world).addEntity(shattered.getEntityId(), shattered);
                        ent.remove();
                    }
                    ite.remove();
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if(event.side.isServer() || event.phase != TickEvent.Phase.END)
        {
            return;
        }
        if(Shatter.config.enablePlayerShatter)
        {
            World world = event.player.world;
            PlayerEntity player = event.player;

            if(!player.isAlive() && !shatterTimeout.containsKey(player) && !deadPlayers.contains(player))
            {
                deadPlayers.add(player);
                shatterTimeout.put(player, 2);
            }
            for(int k = deadPlayers.size() - 1; k >= 0; k--)
            {
                PlayerEntity deadPlayer = deadPlayers.get(k);
                if(deadPlayer.world != world || deadPlayer.getName().equals(player.getName()) && deadPlayer != player)
                {
                    deadPlayers.remove(k);
                }
            }
        }
    }
}
