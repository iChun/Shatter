package shatter.client.entity;

import shatter.client.model.ModelShattered;
import shatter.common.Shatter;
import net.minecraft.entity.Entity;
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
	
	public EntityShattered(World par1World) 
	{
		super(par1World);
		model = new ModelShattered(this);
		yOffset = -0.5F;
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
		yOffset = -0.5F;
		setSize(0.1F, 0.1F);
		noClip = true;
		renderDistanceWeight = 10D;
		ignoreFrustumCheck = true;
		setLocationAndAngles(acquired.posX, acquired.posY - acquired.yOffset, acquired.posZ, acquired.rotationYaw, acquired.rotationPitch);
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
		if(progress > Shatter.tickHandlerClient.maxShatterProgress + 5)
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
	public void setCurrentItemOrArmor(int i, ItemStack itemstack) {
	}

	@Override
	public ItemStack[] getLastActiveItems() {
		return new ItemStack[0];
	}

}
