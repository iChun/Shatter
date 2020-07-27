package me.ichun.mods.shatter.client.entity;

import me.ichun.mods.shatter.client.model.ModelShattered;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

@OnlyIn(Dist.CLIENT)
public class EntityShattered extends Entity
{
    public LivingEntity acquired;

    public int progress;

    public ModelShattered model;

    public EntityShattered(EntityType<?> entityTypeIn, World par1World)
    {
        super(entityTypeIn, par1World);
        noClip = true;
        ignoreFrustumCheck = true;
    }

    public EntityShattered setAcquired(LivingEntity ac)
    {
        acquired = ac;
        progress = 0;
        setLocationAndAngles(acquired.getPosX(), acquired.getPosY(), acquired.getPosZ(), acquired.rotationYaw, acquired.rotationPitch);
        Vector3d acMotion = ac.getMotion();
        setMotion(acMotion.x * 0.4D, acMotion.y * 0.15D, acMotion.z * 0.4D);
        return this;
    }

    @Override
    protected void registerData(){}

    @Override
    public void tick()
    {
        super.tick();

        prevPosX = getPosX();
        prevPosY = getPosY();
        prevPosZ = getPosZ();

        progress++;
        if(progress > 100 + 5)
        {
            remove();
            return;
        }

        Vector3d motion = getMotion();
        double pX = getPosX() + motion.x;
        double pY = getPosY() + motion.y * 0.2D;
        double pZ = getPosZ() + motion.z;

        setMotion(getMotion().mul(0.97D, 0.97D, 0.97D));
        setPosition(pX, pY, pZ);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isInRangeToRenderDist(double distance)
    {
        double d0 = this.getBoundingBox().getAverageEdgeLength() * 10.0D; // * 10D is the new renderDistanceWeight

        if(Double.isNaN(d0))
        {
            d0 = 1.0D;
        }

        d0 = d0 * 64.0D * getRenderDistanceWeight();
        return distance < d0 * d0;
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
    public boolean writeUnlessRemoved(CompoundNBT compound) { return false; } //disable saving of entity

    @Override
    protected void readAdditional(CompoundNBT compound){}

    @Override
    protected void writeAdditional(CompoundNBT compound){}

    @Override
    public IPacket<?> createSpawnPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
