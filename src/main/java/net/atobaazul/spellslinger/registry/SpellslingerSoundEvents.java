package net.atobaazul.spellslinger.registry;

import net.atobaazul.spellslinger.Spellslinger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SpellslingerSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Spellslinger.MODID);
    public static final DeferredHolder<SoundEvent, SoundEvent> MYSTIC_REVERB = registerVariable("mystic_reverb");

    public static void register(IEventBus modEventBus) {
        SOUND_EVENTS.register(modEventBus);
    }

    public static DeferredHolder<SoundEvent, SoundEvent> registerVariable(String name) {
        return register(SoundEvent.createVariableRangeEvent(Spellslinger.id(name)));
    }

    public static DeferredHolder<SoundEvent, SoundEvent> register(SoundEvent soundEvent) {
        return SOUND_EVENTS.register(soundEvent.getLocation().getPath(), () -> soundEvent);
    }

}
