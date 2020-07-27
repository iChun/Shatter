package me.ichun.mods.shatter.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.client.render.RenderHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.util.ObfHelper;
import me.ichun.mods.shatter.client.entity.EntityShattered;
import me.ichun.mods.shatter.client.model.ModelShattered;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderShattered extends EntityRenderer<EntityShattered>
{
    public RenderShattered(EntityRendererManager manager)
    {
        super(manager);
        shadowSize = 0F;
    }

    @Override
    public ResourceLocation getEntityTexture(EntityShattered entity)
    {
        EntityRenderer<?> renderer = renderManager.getRenderer(entity.acquired);
        return RenderHelper.getEntityTexture(renderer, entity.acquired);
    }

    @Override
    public void render(EntityShattered shattered, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
    {
        if(shattered.model == null)
        {
            shattered.model = new ModelShattered(shattered);
        }
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180F));
        matrixStackIn.scale(-1.0F, -1.0F, 1.0F);

        if(shattered.model.entRenderer != null)
        {
            RenderHelper.invokePreRenderCallback(shattered.model.entRenderer, shattered.acquired, matrixStackIn, partialTicks);
        }
        matrixStackIn.translate(0F, -0.75F, 0F);

        shattered.model.render(matrixStackIn, bufferIn.getBuffer(RenderType.getEntityTranslucentCull(getEntityTexture(shattered))), packedLightIn, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, MathHelper.clamp((float)Math.pow(((double)shattered.progress + iChunUtil.eventHandlerClient.partialTick) / 100D, 0.99D), 0.0F, 1.0F));
    }

    public static class RenderFactory implements IRenderFactory<EntityShattered>
    {
        @Override
        public EntityRenderer<EntityShattered> createRenderFor(EntityRendererManager manager)
        {
            return new RenderShattered(manager);
        }
    }
}
