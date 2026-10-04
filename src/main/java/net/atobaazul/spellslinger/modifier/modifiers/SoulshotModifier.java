package net.atobaazul.spellslinger.modifier.modifiers;

import io.redspace.irons_artifice.client.particle.ColorTransitionParticleOption;
import io.redspace.irons_artifice.data.ShotComponentMap;
import io.redspace.irons_artifice.data.ShotComponents;
import io.redspace.irons_artifice.data.ValueModifier;
import io.redspace.irons_artifice.modifier.GunModifier;
import net.atobaazul.spellslinger.modifier.on_hit_handlers.SoulshotOnHit;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class SoulshotModifier implements GunModifier {
    @Override
    public void apply(ShotComponentMap components) {
        components.getOrCreate(ShotComponents.DAMAGE).addModifier(new ValueModifier(-0.15, ValueModifier.Operation.MULTIPLY_TOTAL, ValueModifier.Type.HARMFUL));
        components.getOrCreate(ShotComponents.POST_HIT_EFFECTS).add(new SoulshotOnHit());
        components.getOrCreate(ShotComponents.MUZZLE_FLASH).addTint(0xdc8af2);
        components.getOrCreate(ShotComponents.PARTICLE_TRAIL).add(ColorTransitionParticleOption.bulletTrail(
                0xdc8af2, 0xa789b0
        ));

    }

    @Override
    public void getDescriptionText(Consumer<Component> builder) {
        builder.accept(Component.translatable("spellslinger.modifier.soulshot.damage_debuff").withStyle(ChatFormatting.RED));
        builder.accept(Component.translatable("spellslinger.modifier.soulshot.damage").withStyle(ChatFormatting.GREEN));
        builder.accept(Component.translatable("spellslinger.modifier.soulshot.spirit_collection").withStyle(ChatFormatting.AQUA));

    }
}
