package me.ichun.mods.shatter.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import me.ichun.mods.ichunutil.common.core.util.ObfHelper;
import net.minecraft.entity.EntityLiving;
import org.lwjgl.opengl.GL11;
import me.ichun.mods.shatter.client.entity.EntityShattered;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class RenderShattered extends RenderLiving
{

	public RenderShattered(ModelBase par1ModelBase, float par2) 
	{
		super(Minecraft.getMinecraft().getRenderManager(), par1ModelBase, par2);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) 
	{
		if(entity instanceof EntityShattered)
		{
			setMainModel(((EntityShattered)entity).model);
		}
		RenderManager manager = Minecraft.getMinecraft().getRenderManager();
		return ObfHelper.getEntityTexture(manager.getEntityRenderObject(((EntityShattered)entity).acquired), manager.getEntityRenderObject(((EntityShattered)entity).acquired).getClass(), ((EntityShattered)entity).acquired);
	}
	
    @Override
    public void doRender(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, float par8, float par9)
    {
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);
        super.doRender((EntityLiving)par1EntityLivingBase, par2, par4 - 0.5F, par6, par8, par9);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
    }

	@Override
	protected void preRenderCallback(EntityLivingBase ent, float renderTick)
	{
		if(ent instanceof EntityShattered)
		{
			//		FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);
			//		FloatBuffer buffer1 = GLAllocation.createDirectFloatBuffer(16);
			//
			//		GL11.glPushMatrix();
			//		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, buffer);
			ObfHelper.invokePreRenderCallback((((EntityShattered)ent).model).entRenderer, (((EntityShattered)ent).model).entRenderer.getClass(), (((EntityShattered)ent).model).shatteredEnt.acquired, renderTick);
			//		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, buffer1);
			//		GL11.glPopMatrix();

			//		float prevScaleX = buffer1.get(0) / buffer.get(0);
			//		float prevScaleY = buffer1.get(1) / buffer.get(1);
			//		float prevScaleZ = buffer1.get(2) / buffer.get(2);
			//
			//		GL11.glScalef(prevScaleX, prevScaleY, prevScaleZ);
		}
	}

	public void setMainModel(ModelBase base)
	{
		mainModel = base;
	}
}
