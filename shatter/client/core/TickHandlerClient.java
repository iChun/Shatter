package shatter.client.core;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

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
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TickHandlerClient implements ITickHandler {

	public TickHandlerClient()
	{
		renderShatteredInstance = new RenderShattered(new ModelShattered(), 0.0F);
		renderShatteredInstance.setRenderManager(RenderManager.instance);
	}
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) 
	{
        if (type.equals(EnumSet.of(TickType.CLIENT)))
        {
        	if(Minecraft.getMinecraft().theWorld != null)
        	{      		
        		worldTick(Minecraft.getMinecraft(), Minecraft.getMinecraft().theWorld);
        	}
        }
		else if (type.equals(EnumSet.of(TickType.PLAYER)))
		{
			playerTick((World)((EntityPlayer)tickData[0]).worldObj, (EntityPlayer)tickData[0]);
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) 
	{
	}

	@Override
	public EnumSet<TickType> ticks() 
	{
		return EnumSet.of(TickType.CLIENT, TickType.PLAYER);
	}

	@Override
	public String getLabel() 
	{
		return "TickHandlerClientShatter";
	}

	public void worldTick(Minecraft mc, WorldClient world)
	{
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
	
	public void playerTick(World world, EntityPlayer player)
	{
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
				if(deadPlayer.worldObj != world || deadPlayer.username.equals(player.username) && deadPlayer != player)
				{
					deadPlayers.remove(k);
				}
			}
		}
	}
	
    public boolean serverHasMod;
    
    public long clock;
    
    public RenderShattered renderShatteredInstance;
    
    public HashMap<EntityLivingBase, Integer> shatterTimeout = new HashMap<EntityLivingBase, Integer>();
    public ArrayList<EntityPlayer> deadPlayers = new ArrayList<EntityPlayer>();

	public final int maxShatterProgress = 100;
}
