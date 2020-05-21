package me.ichun.mods.shatter.common.core;

import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.config.annotations.CategoryDivider;
import me.ichun.mods.ichunutil.common.config.annotations.Prop;
import me.ichun.mods.shatter.common.Shatter;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nonnull;


public class Config extends ConfigBase
{
    @CategoryDivider(name = "clientOnly")
    @Prop
    public boolean enableBossShatter = false;

    @Prop
    public boolean enablePlayerShatter = true;

    @Prop
    public boolean enableChildShatter = false;

    public Config()
    {
        super();
    }

    @Override
    public String getModId()
    {
        return Shatter.MOD_ID;
    }

    @Nonnull
    @Override
    public String getConfigName()
    {
        return Shatter.MOD_NAME;
    }

    @Nonnull
    @Override
    public ModConfig.Type getConfigType()
    {
        return ModConfig.Type.CLIENT;
    }
}
