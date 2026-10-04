package net.atobaazul.spellslinger.modifier.modifiers;

import io.redspace.irons_artifice.client.particle.ColorTransitionParticleOption;
import io.redspace.irons_artifice.data.ShotComponentMap;
import io.redspace.irons_artifice.data.ShotComponents;
import io.redspace.irons_artifice.data.ValueModifier;
import io.redspace.irons_artifice.modifier.GunModifier;
import net.atobaazul.spellslinger.modifier.on_hit_handlers.SpiritShredderOnHit;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class SpiritShredderModifier implements GunModifier {
    @Override
    public void apply(ShotComponentMap components) {
        components.getOrCreate(ShotComponents.RELOAD_SPEED_MULTIPLIER).addModifier(new ValueModifier(-0.15, ValueModifier.Operation.MULTIPLY_TOTAL, ValueModifier.Type.HARMFUL));
        components.getOrCreate(ShotComponents.POST_HIT_EFFECTS).add(new SpiritShredderOnHit());
        components.getOrCreate(ShotComponents.MUZZLE_FLASH).addTint(0x6f5bb0);
        components.getOrCreate(ShotComponents.PARTICLE_TRAIL).add(ColorTransitionParticleOption.bulletTrail(
                0x6f5bb0, 0x733bcc
        ));
    }

    @Override
    public void getDescriptionText(Consumer<Component> builder) {
        builder.accept(Component.translatable("spellslinger.modifier.spirit_shredder.debuff").withStyle(ChatFormatting.RED));
        builder.accept(Component.translatable("spellslinger.modifier.spirit_shredder").withStyle(ChatFormatting.AQUA));
    }
}
