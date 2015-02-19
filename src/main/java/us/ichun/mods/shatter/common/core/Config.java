package us.ichun.mods.shatter.common.core;

import us.ichun.mods.ichunutil.common.core.config.ConfigBase;
import us.ichun.mods.ichunutil.common.core.config.annotations.ConfigProp;
import us.ichun.mods.ichunutil.common.core.config.annotations.IntBool;

import java.io.File;

public class Config extends ConfigBase
{
    @ConfigProp(category = "clientOnly")
    @IntBool
    public int enableBossShatter = 0;

    @ConfigProp(category = "clientOnly")
    @IntBool
    public int enablePlayerShatter = 1;

    @ConfigProp(category = "clientOnly")
    @IntBool
    public int enableChildShatter = 0;

    public Config(File file, String... unhide)
    {
        super(file, unhide);
    }

    @Override
    public String getModId()
    {
        return "shatter";
    }

    @Override
    public String getModName()
    {
        return "Shatter";
    }
}
