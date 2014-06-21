package shatter.client.model;

import ichun.client.model.ModelHelper;
import ichun.common.core.util.ObfHelper;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;

import shatter.client.entity.EntityShattered;
import shatter.common.Shatter;

public class ModelShattered extends ModelBase
{
	public EntityShattered shatteredEnt;
	
	public Render entRenderer;
	
	public Random rand;
	
	public ArrayList<ModelRenderer> modelList;
	
	public float renderYaw;
	
	public ModelShattered() {}
	
	public ModelShattered(EntityShattered ent)
	{
		this.shatteredEnt = ent;
		this.rand = new Random();
		
		if(ent != null)
		{
			this.entRenderer = RenderManager.instance.getEntityRenderObject(ent.acquired);
			
			if(RenderManager.instance.renderEngine != null && RenderManager.instance.livingPlayer != null && ent.acquired != null)
			{
				RenderManager.instance.getEntityRenderObject(ent.acquired).doRender(ent.acquired, 0.0D, -500D, 0.0D, 0.0F, 1.0F);
			}
			
			this.modelList = ModelHelper.getModelCubesCopy(ModelHelper.getModelCubes(ent.acquired), this, ent.acquired);
			
			for(ModelRenderer cube : modelList)
			{
				cube.rotationPointY -= 8.0D;
			}
		}
		
		this.renderYaw = ent.acquired.renderYawOffset;
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		if(shatteredEnt.acquired == Minecraft.getMinecraft().thePlayer && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0)
		{
			return;
		}
		GL11.glPushMatrix();

		GL11.glRotatef(renderYaw, 0.0F, 1.0F, 0.0F);
		
		FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);
		FloatBuffer buffer1 = GLAllocation.createDirectFloatBuffer(16);
		
//		GL11.glPushMatrix();
//		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, buffer);
		ObfHelper.invokePreRenderCallback(entRenderer, entRenderer.getClass(), shatteredEnt.acquired, f5);
//		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, buffer1);
//		GL11.glPopMatrix();
		
//		float prevScaleX = buffer1.get(0) / buffer.get(0);
//		float prevScaleY = buffer1.get(1) / buffer.get(1);
//		float prevScaleZ = buffer1.get(2) / buffer.get(2);
//
//		GL11.glScalef(prevScaleX, prevScaleY, prevScaleZ);
		
		float progress = MathHelper.clamp_float((float)Math.pow(((double)shatteredEnt.progress + f5) / (double)Shatter.tickHandlerClient.maxShatterProgress, 0.99D), 0.0F, 1.0F);

        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F - progress);
		
		for(int i = 0; i < modelList.size(); i++)
		{
			ModelRenderer cube = modelList.get(i);
		
			GL11.glPushMatrix();
			rand.setSeed(rand.nextInt() * shatteredEnt.getEntityId() * i * 1000);
			GL11.glTranslated(rand.nextFloat() * (rand.nextFloat() > 0.5F ? -1 : 1) * progress * shatteredEnt.motionZ * 5D, rand.nextDouble() * progress * (shatteredEnt.motionY + (rand.nextDouble() - 1.0D)) , rand.nextFloat() * (rand.nextFloat() > 0.5F ? -1 : 1) * progress * shatteredEnt.motionX * 5D);
			GL11.glRotatef(180F * rand.nextFloat() * progress, rand.nextFloat() * (rand.nextFloat() > 0.5F ? -1 : 1) * progress, rand.nextFloat() * (rand.nextFloat() > 0.5F ? -1 : 1) * progress, rand.nextFloat() * (rand.nextFloat() > 0.5F ? -1 : 1) * progress);
			cube.render(f5);
			GL11.glPopMatrix();
		}
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}
}
