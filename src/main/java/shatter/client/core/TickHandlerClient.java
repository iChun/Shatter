package shatter.client.core;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import shatter.client.entity.EntityShattered;
import shatter.client.model.ModelShattered;
import shatter.client.render.RenderShattered;
import shatter.common.Shatter;

public class TickHandlerClient
{

	public TickHandlerClient()
	{
		renderShatteredInstance = new RenderShattered(new ModelShattered(), 0.0F);
		renderShatteredInstance.setRenderManager(RenderManager.instance);
	}

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
				
				e.getKey().hurtTime = 0;
				e.getKey().deathTime = 0;
				
				Entity explo = null;
				double dist = 1000D;
				
				if(e.getValue() <= 0)
				{
					if(e.getKey().worldObj == world)
					{
						e.getKey().worldObj.spawnEntityInWorld(new EntityShattered(e.getKey().worldObj, e.getKey()));
						e.getKey().setDead();
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

		if(Shatter.config.getInt("enablePlayerShatter") == 1)
		{
			if(!player.isEntityAlive() && !shatterTimeout.containsKey(player) && !deadPlayers.contains(player))
			{
				deadPlayers.add(player);
				shatterTimeout.put((EntityLivingBase)player, 2);
			}
			for(int k = deadPlayers.size() - 1; k >= 0; k--)
			{
				EntityPlayer deadPlayer = deadPlayers.get(k);
				if(deadPlayer.worldObj != world || deadPlayer.getCommandSenderName().equals(player.getCommandSenderName()) && deadPlayer != player)
				{
					deadPlayers.remove(k);
				}
			}
		}
	}
	
    public long clock;
    
    public RenderShattered renderShatteredInstance;
    
    public HashMap<EntityLivingBase, Integer> shatterTimeout = new HashMap<EntityLivingBase, Integer>();
    public ArrayList<EntityPlayer> deadPlayers = new ArrayList<EntityPlayer>();

	public final int maxShatterProgress = 100;
}
