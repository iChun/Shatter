package shatter.client.core;

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
import shatter.client.entity.EntityShattered;
import shatter.client.model.ModelShattered;
import shatter.client.render.RenderShattered;
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
			
//			for(int i = 0; i < world.loadedEntityList.size(); i++)
//			{
//				Entity ent = (Entity)world.loadedEntityList.get(i);
//				if((ent instanceof EntityLivingBase && !(ent instanceof IBossDisplayData)) && !((EntityLivingBase)ent).isChild() && (!ent.isEntityAlive() || ((EntityLivingBase)ent).deathTime > 0) && !shatterTimeout.containsKey(ent))
//				{
//					shatterTimeout.put((EntityLivingBase)ent, 2);
//				}
//			}
			
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
					e.getKey().worldObj.spawnEntityInWorld(new EntityShattered(e.getKey().worldObj, e.getKey()));
					e.getKey().setDead();
					ite.remove();
				}
			}
			
		}
	}
	
    public boolean serverHasMod;
    
    public long clock;
    
    public RenderShattered renderShatteredInstance;
    
    public HashMap<EntityLivingBase, Integer> shatterTimeout = new HashMap<EntityLivingBase, Integer>();

	public final int maxShatterProgress = 100;
}
