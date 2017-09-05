package me.ichun.mods.shatter.client.entity;

import me.ichun.mods.shatter.client.model.ModelShattered;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityShattered extends Entity
{
    public EntityLivingBase acquired;

    public int progress;

    public ModelShattered model;

    public EntityShattered(World par1World)
    {
        super(par1World);
        setSize(0.1F, 0.1F);
        noClip = true;
        ignoreFrustumCheck = true;
    }

    public EntityShattered(World par1World, EntityLivingBase ac)
    {
        super(par1World);
        acquired = ac;
        progress = 0;
        setSize(0.1F, 0.1F);
        noClip = true;
        ignoreFrustumCheck = true;
        setLocationAndAngles(acquired.posX, acquired.posY, acquired.posZ, acquired.rotationYaw, acquired.rotationPitch);
        motionX = ac.motionX * 0.4D;
        motionY = ac.motionY * 0.15D;
        motionZ = ac.motionZ * 0.4D;
    }

    @Override
    protected void entityInit()
    {
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
        setPosition(posX, posY, posZ);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean isInRangeToRenderDist(double distance)
    {
        double d0 = this.getEntityBoundingBox().getAverageEdgeLength() * 10.0D; // * 10D is the new renderDistanceWeight

        if(Double.isNaN(d0))
        {
            d0 = 1.0D;
        }

        d0 = d0 * 64.0D * getRenderDistanceWeight();
        return distance < d0 * d0;
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
    public boolean writeToNBTOptional(NBTTagCompound par1NBTTagCompound)
    {
        return false;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound) {}

    @Override
    public void writeEntityToNBT(NBTTagCompound nbttagcompound) {}
}
