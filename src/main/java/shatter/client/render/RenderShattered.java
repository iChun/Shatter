package shatter.client.render;

import ichun.common.core.util.ObfHelper;
import shatter.client.entity.EntityShattered;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class RenderShattered extends RendererLivingEntity 
{

	public RenderShattered(ModelBase par1ModelBase, float par2) 
	{
		super(par1ModelBase, par2);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) 
	{
		if(entity instanceof EntityShattered)
		{
			setMainModel(((EntityShattered)entity).model);
		}
		return ObfHelper.invokeGetEntityTexture(RenderManager.instance.getEntityRenderObject(((EntityShattered)entity).acquired), RenderManager.instance.getEntityRenderObject(((EntityShattered)entity).acquired).getClass(), ((EntityShattered)entity).acquired);
	}
	
	@Override
    protected void passSpecialRender(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6)
    {
    }
	
	public void setMainModel(ModelBase base)
	{
		mainModel = base;
	}
}
