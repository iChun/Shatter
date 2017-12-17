package me.ichun.mods.shatter.client.render;

import me.ichun.mods.ichunutil.common.core.util.ObfHelper;
import me.ichun.mods.shatter.client.entity.EntityShattered;
import me.ichun.mods.shatter.client.model.ModelShattered;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;

public class RenderShattered extends Render<EntityShattered>
{
    public RenderShattered(RenderManager manager)
    {
        super(manager);
        shadowSize = 0F;
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityShattered entity)
    {
        return ObfHelper.getEntityTexture(renderManager.getEntityRenderObject(entity.acquired), renderManager.getEntityRenderObject(entity.acquired).getClass(), entity.acquired);
    }

    @Override
    public void doRender(EntityShattered shattered, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if(shattered.model == null)
        {
            shattered.model = new ModelShattered(shattered);
        }
        ResourceLocation rl = getEntityTexture(shattered);
        if(shattered.model.entRenderer instanceof RenderLivingBase && rl != null)
        {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            GlStateManager.rotate(180F, 0F, 1F, 0F);
            GlStateManager.scale(-1.0F, -1.0F, 1.0F);

            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            bindTexture(rl);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);
            ObfHelper.invokePreRenderCallback((RenderLivingBase)shattered.model.entRenderer, shattered.model.entRenderer.getClass(), shattered.acquired, partialTicks);
            GlStateManager.translate(0F, -1F, 0F);
            shattered.model.render(shattered, 0F, 0F, 0F, 0F, 0F, 0.0625F);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);

            GlStateManager.disableBlend();

            GlStateManager.popMatrix();
        }
    }

    public static class RenderFactory implements IRenderFactory<EntityShattered>
    {
        @Override
        public Render<EntityShattered> createRenderFor(RenderManager manager)
        {
            return new RenderShattered(manager);
        }
    }
}
