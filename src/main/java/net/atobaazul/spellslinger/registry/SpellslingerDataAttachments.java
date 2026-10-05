package net.atobaazul.spellslinger.registry;

import com.mojang.serialization.Codec;
import net.atobaazul.spellslinger.Spellslinger;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SpellslingerDataAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Spellslinger.MODID);

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }

    public static final Supplier<AttachmentType<Float>> INCOMING_MAGIC_DAMAGE = ATTACHMENT_TYPES.register(
            "incoming_magic_damage",
            () -> AttachmentType.builder(() -> 0.0f).serialize(com.mojang.serialization.Codec.FLOAT).build()
    );

    public static final Supplier<AttachmentType<Integer>> MYSTIC_REVERB_DURATION =  ATTACHMENT_TYPES.register(
            "mystic_reverb_duration",
            () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build()
    );

    public static final Supplier<AttachmentType<Integer>> AMMO_WARP_TIMER =  ATTACHMENT_TYPES.register(
            "ammo_warp_timer",
            () -> AttachmentType.builder(() -> 40).serialize(Codec.INT).build()
    );

    public static final Supplier<AttachmentType<Float>> MYSTIC_REVERB_INCOMING_DAMAGE =  ATTACHMENT_TYPES.register(
            "mystic_reverb_incoming_damage",
            () -> AttachmentType.builder(() -> 0.0f).serialize(Codec.FLOAT).sync(ByteBufCodecs.FLOAT).build()
    );


}
