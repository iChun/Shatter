package me.ichun.mods.shatter.client.entity;

import me.ichun.mods.shatter.client.render.RenderShattered;
import me.ichun.mods.shatter.common.Shatter;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import me.ichun.mods.shatter.client.model.ModelShattered;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityShattered extends EntityLivingBase
{
	public EntityLivingBase acquired;
	
	public int progress;
	
	public ModelShattered model;

	//TODO no more yOffset. What do?
	public EntityShattered(World par1World) 
	{
		super(par1World);
		model = new ModelShattered(this);
		setSize(0.1F, 0.1F);
		noClip = true;
		renderDistanceWeight = 10D;
		ignoreFrustumCheck = true;
	}
	
	public EntityShattered(World par1World, EntityLivingBase ac) 
	{
		super(par1World);
		acquired = ac;
		model = new ModelShattered(this);
		progress = 0;
		setSize(0.1F, 0.1F);
		noClip = true;
		renderDistanceWeight = 10D;
		ignoreFrustumCheck = true;
		setLocationAndAngles(acquired.posX, acquired.posY, acquired.posZ, acquired.rotationYaw, acquired.rotationPitch);
		motionX = ac.motionX * 0.4D;
		motionY = ac.motionY * 0.15D;
		motionZ = ac.motionZ * 0.4D;
	}

	@Override
	public void onUpdate()
	{
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		
		progress++;
		if(progress > 100 + 5)
		{
			setDead();
			return;
		}
		
		posX += motionX;
		posY += motionY * 0.2D;
		posZ += motionZ;
		
		motionX *= 0.97D;
		motionY *= 0.97D;
		motionZ *= 0.97D;
	}
	
	@Override
    public boolean shouldRenderInPass(int pass)
    {
        return pass == 1;
    }
	
	@Override
    public boolean canBeCollidedWith()
    {
        return false;
    }
    
    @Override
    public boolean canBePushed()
    {
        return false;
    }

	@Override
    public boolean isEntityAlive()
    {
        return !this.isDead;
    }
	
	@Override
    public void setHealth(float par1)
    {
    }
	
	@Override
    public boolean writeToNBTOptional(NBTTagCompound par1NBTTagCompound)
    {
    	return false;
    }
	
	@Override
	protected void entityInit() 
	{
		super.entityInit();
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {}

	@Override
	public ItemStack getHeldItem() {
		return null;
	}

	@Override
	public ItemStack getEquipmentInSlot(int i) {
		return null;
	}

	@Override
	public ItemStack getCurrentArmor(int slotIn)
	{
		return null;
	}

	@Override
	public void setCurrentItemOrArmor(int i, ItemStack itemstack) {
	}

	@Override
	public ItemStack[] getInventory()
	{
		return new ItemStack[0];
	}

    public static class RenderFactory implements IRenderFactory<EntityShattered>
    {
        @Override
        public Render<EntityShattered> createRenderFor(RenderManager manager)
        {
            return new RenderShattered(manager, new ModelShattered(), 0.0F);
        }
    }
}
