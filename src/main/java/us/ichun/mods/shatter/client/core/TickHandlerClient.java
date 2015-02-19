package us.ichun.mods.shatter.client.core;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import us.ichun.mods.ichunutil.common.iChunUtil;
import us.ichun.mods.shatter.client.entity.EntityShattered;
import us.ichun.mods.shatter.client.model.ModelShattered;
import us.ichun.mods.shatter.client.render.RenderShattered;
import us.ichun.mods.shatter.common.Shatter;
import us.ichun.mods.shatter.common.Shatter;

public class TickHandlerClient
{
    @SubscribeEvent
	public void worldTick(TickEvent.ClientTickEvent event)
	{
        if(!(event.phase == TickEvent.Phase.END && Minecraft.getMinecraft().theWorld != null))
        {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        WorldClient world = mc.theWorld;

		if(clock != world.getWorldTime() || !world.getGameRules().getGameRuleBooleanValue("doDaylightCycle"))
		{
			clock = world.getWorldTime();
			
			Iterator<Entry<EntityLivingBase, Integer>> ite = shatterTimeout.entrySet().iterator();
			if(ite.hasNext())
			{
				Entry<EntityLivingBase, Integer> e = ite.next();
				
				e.setValue(e.getValue() - 1);

				EntityLivingBase ent = e.getKey();

				if(iChunUtil.hasMorphMod && ent instanceof EntityPlayer && morph.api.Api.hasMorph(ent.getName(), true))
				{
					 ent = morph.api.Api.getMorphEntity(ent.getName(), true);
				}

				ent.hurtTime = 0;
				ent.deathTime = 0;
				
				if(e.getValue() <= 0)
				{
					if(ent.worldObj == world)
					{
						ent.worldObj.spawnEntityInWorld(new EntityShattered(ent.worldObj, ent));
						ent.setDead();
					}
					ite.remove();
				}
			}
			
		}
	}

    @SubscribeEvent
	public void playerTick(TickEvent.PlayerTickEvent event)
	{
        if(event.side == Side.SERVER || event.phase != TickEvent.Phase.END)
        {
            return;
        }
        World world = event.player.worldObj;
        EntityPlayer player = event.player;

		if(Shatter.config.enablePlayerShatter == 1)
		{
			if(!player.isEntityAlive() && !shatterTimeout.containsKey(player) && !deadPlayers.contains(player))
			{
				deadPlayers.add(player);
				shatterTimeout.put((EntityLivingBase)player, 2);
			}
			for(int k = deadPlayers.size() - 1; k >= 0; k--)
			{
				EntityPlayer deadPlayer = deadPlayers.get(k);
				if(deadPlayer.worldObj != world || deadPlayer.getName().equals(player.getName()) && deadPlayer != player)
				{
					deadPlayers.remove(k);
				}
			}
		}
	}
	
    public long clock;
    
    public HashMap<EntityLivingBase, Integer> shatterTimeout = new HashMap<EntityLivingBase, Integer>();
    public ArrayList<EntityPlayer> deadPlayers = new ArrayList<EntityPlayer>();

	public final int maxShatterProgress = 100;
}
