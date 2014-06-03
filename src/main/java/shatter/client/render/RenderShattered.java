package shatter.client.render;

import ichun.common.core.util.ObfHelper;
import org.lwjgl.opengl.GL11;
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

    @Override
    public void doRender(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, float par8, float par9)
    {
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
        super.doRender(par1EntityLivingBase, par2, par4, par6, par8, par9);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
    }

    public void setMainModel(ModelBase base)
	{
		mainModel = base;
	}
}
