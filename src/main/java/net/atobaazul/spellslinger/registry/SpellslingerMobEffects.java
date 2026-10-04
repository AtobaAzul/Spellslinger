package net.atobaazul.spellslinger.registry;

import net.atobaazul.spellslinger.Spellslinger;
import net.atobaazul.spellslinger.effects.SpiritShredEffect;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SpellslingerMobEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, Spellslinger.MODID);

    public static void register(IEventBus modEventBus) {
        MOB_EFFECTS.register(modEventBus);
    }

    public static final DeferredHolder<MobEffect, MobEffect> SPIRIT_SHRED = MOB_EFFECTS.register("spirit_shred", SpiritShredEffect::new);
}
