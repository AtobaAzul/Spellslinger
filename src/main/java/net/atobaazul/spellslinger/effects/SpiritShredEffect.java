package net.atobaazul.spellslinger.effects;

import net.atobaazul.spellslinger.Spellslinger;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import team.lodestar.lodestone.helpers.ColorHelper;
import team.lodestar.lodestone.registry.common.LodestoneAttributes;

public class SpiritShredEffect extends MobEffect {
    public SpiritShredEffect() {
        super(MobEffectCategory.HARMFUL, ColorHelper.getColor(115, 59, 204));
        addAttributeModifier(LodestoneAttributes.MAGIC_RESISTANCE, Spellslinger.id("spirit_shred"), -0.1f, AttributeModifier.Operation.ADD_VALUE);
    }
}