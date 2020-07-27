package me.ichun.mods.shatter.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.ichun.mods.ichunutil.client.model.util.ModelHelper;
import me.ichun.mods.shatter.client.entity.EntityShattered;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import java.util.ArrayList;
import java.util.Random;

public class ModelShattered extends Model
{
    public EntityShattered shatteredEnt;

    public LivingRenderer<?, ?> entRenderer;

    public Random rand;

    public ArrayList<ModelRenderer> modelList;

    public float renderYaw;

    public ModelShattered()
    {
        super(RenderType::getEntityTranslucentCull);
    }

    public ModelShattered(EntityShattered ent)
    {
        this();

        this.shatteredEnt = ent;
        this.rand = new Random();

        if(ent != null)
        {

            if(ent.acquired != null)
            {
                EntityRendererManager manager = Minecraft.getInstance().getRenderManager();
                EntityRenderer rend = manager.getRenderer(ent.acquired);
                if(rend instanceof LivingRenderer)
                {
                    this.entRenderer = (LivingRenderer<?, ?>)rend;
                }

                MatrixStack stack = new MatrixStack();
                stack.translate(0F, -500F, 0F);
                rend.render(ent.acquired, ent.acquired.rotationYawHead, 1F, stack, Minecraft.getInstance().getRenderTypeBuffers().getBufferSource(), 15728880);

                this.modelList = ModelHelper.explode(ModelHelper.createModelPartsFromProject(ModelHelper.convertModelToProject(entRenderer.getEntityModel())));

                for(ModelRenderer cube : modelList)
                {
                    cube.rotationPointY -= 12.0D;
                }

                this.renderYaw = ent.acquired.renderYawOffset;
            }
        }
    }

    @Override
    public void render(MatrixStack stack, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float progress)
    {
        if(shatteredEnt.acquired == Minecraft.getInstance().player && Minecraft.getInstance().gameSettings.thirdPersonView == 0)
        {
            return;
        }

        stack.push();

        stack.rotate(Vector3f.YP.rotationDegrees(renderYaw));

        Vector3d motion = shatteredEnt.getMotion();
        float alpha = 1.0F - progress;
        for(int i = 0; i < modelList.size(); i++)
        {
            ModelRenderer cube = modelList.get(i);

            stack.push();
            rand.setSeed(rand.nextInt() * shatteredEnt.getEntityId() * i * 1000);
            stack.translate(rand.nextFloat() * (rand.nextFloat() > 0.5F ? -1 : 1) * progress * motion.z * 5D, rand.nextDouble() * progress * (motion.y + (rand.nextDouble() - 1.0D)), rand.nextFloat() * (rand.nextFloat() > 0.5F ? -1 : 1) * progress * motion.x * 5D);
            float rotBase = 180F * rand.nextFloat() * progress;
            float rotX = rand.nextFloat() * (rand.nextFloat() > 0.5F ? -1 : 1) * progress;
            float rotY = rand.nextFloat() * (rand.nextFloat() > 0.5F ? -1 : 1) * progress;
            float rotZ = rand.nextFloat() * (rand.nextFloat() > 0.5F ? -1 : 1) * progress;
            stack.rotate(Vector3f.XP.rotationDegrees(rotBase * rotX));
            stack.rotate(Vector3f.YP.rotationDegrees(rotBase * rotY));
            stack.rotate(Vector3f.ZP.rotationDegrees(rotBase * rotZ));
            cube.render(stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            stack.pop();
        }

        stack.pop();
    }
}
