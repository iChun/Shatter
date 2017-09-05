package me.ichun.mods.shatter.client.model;

import me.ichun.mods.ichunutil.client.model.util.ModelHelper;
import me.ichun.mods.shatter.client.entity.EntityShattered;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Random;

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
			RenderManager manager = Minecraft.getMinecraft().getRenderManager();
			this.entRenderer = manager.getEntityRenderObject(ent.acquired);

			if(manager.renderEngine != null && manager.renderViewEntity != null && ent.acquired != null)
			{
				manager.getEntityRenderObject(ent.acquired).doRender(ent.acquired, 0.0D, -500D, 0.0D, 0.0F, 1.0F);
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
		GlStateManager.pushMatrix();

		GlStateManager.rotate(renderYaw, 0.0F, 1.0F, 0.0F);

		float progress = MathHelper.clamp_float((float)Math.pow(((double)shatteredEnt.progress + f5) / 100D, 0.99D), 0.0F, 1.0F);

		GlStateManager.depthMask(true);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F - progress);

		for(int i = 0; i < modelList.size(); i++)
		{
			ModelRenderer cube = modelList.get(i);

			GlStateManager.pushMatrix();
			rand.setSeed(rand.nextInt() * shatteredEnt.getEntityId() * i * 1000);
			GlStateManager.translate(rand.nextFloat() * (rand.nextFloat() > 0.5F ? -1 : 1) * progress * shatteredEnt.motionZ * 5D, rand.nextDouble() * progress * (shatteredEnt.motionY + (rand.nextDouble() - 1.0D)), rand.nextFloat() * (rand.nextFloat() > 0.5F ? -1 : 1) * progress * shatteredEnt.motionX * 5D);
			GlStateManager.rotate(180F * rand.nextFloat() * progress, rand.nextFloat() * (rand.nextFloat() > 0.5F ? -1 : 1) * progress, rand.nextFloat() * (rand.nextFloat() > 0.5F ? -1 : 1) * progress, rand.nextFloat() * (rand.nextFloat() > 0.5F ? -1 : 1) * progress);
			cube.render(f5);
			GlStateManager.popMatrix();
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}
}
