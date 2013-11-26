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
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) 
	{
	}

	@Override
	public EnumSet<TickType> ticks() 
	{
		return EnumSet.of(TickType.CLIENT);
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
			
			if(Shatter.config.getInt("enablePlayerShatter") == 1)
			{
				for(int i = 0; i < world.playerEntities.size(); i++)
				{
					EntityPlayer ent = (EntityPlayer)world.playerEntities.get(i);
					if(!ent.isEntityAlive() && !shatterTimeout.containsKey(ent) && !deadPlayers.contains(ent))
					{
						deadPlayers.add(ent);
						shatterTimeout.put((EntityLivingBase)ent, 2);
					}
					for(int k = deadPlayers.size() - 1; k >= 0; k--)
					{
						EntityPlayer deadPlayer = deadPlayers.get(k);
						if(deadPlayer.worldObj != world || deadPlayer.username.equals(ent.username) && deadPlayer != ent)
						{
							deadPlayers.remove(k);
						}
					}
				}
			}
			
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
	
    public boolean serverHasMod;
    
    public long clock;
    
    public RenderShattered renderShatteredInstance;
    
    public HashMap<EntityLivingBase, Integer> shatterTimeout = new HashMap<EntityLivingBase, Integer>();
    public ArrayList<EntityPlayer> deadPlayers = new ArrayList<EntityPlayer>();

	public final int maxShatterProgress = 100;
}
