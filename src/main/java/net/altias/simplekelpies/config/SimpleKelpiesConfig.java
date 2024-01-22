package net.altias.simplekelpies.config;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.RangeConstraint;
import net.altias.simplekelpies.SimpleKelpies;

@Modmenu(modId = SimpleKelpies.MOD_ID)
@Config(name = "simplekelpies-config", wrapperName = "SimpleKelpiesCommon")
public class SimpleKelpiesConfig {

    @RangeConstraint(min = 0f, max = 1f)
    public float bridleDungeonDrop = 0.1f;
    @RangeConstraint(min = 0f, max = 1f)
    public float bridleBigWaterRuinDrop = 0.9f;

    @RangeConstraint(min = 0f, max = 1f)
    public float bridleSmallWaterRuinDrop = 0.5f;

    @RangeConstraint(min = 0f, max = 1f)
    public float bridleShipewreckDrop = 0.1f;

    @RangeConstraint(min = 0f, max = 1f)
    public float goldfinCodFishChance = 0.01f;
}
