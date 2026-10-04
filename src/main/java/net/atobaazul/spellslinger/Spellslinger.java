package net.atobaazul.spellslinger;

import com.mojang.logging.LogUtils;
import com.sammy.malum.registry.common.MalumAttachmentTypes;
import com.sammy.malum.registry.common.magic.MalumSpiritTypes;
import com.sammy.malum.visual_effects.SpiritLightSpecs;
import io.redspace.irons_artifice.data.ShotComponents;
import io.redspace.irons_artifice.entity.Bullet;
import io.redspace.irons_artifice.gun.ShotProfile;
import io.redspace.irons_artifice.modifier.PostHitEffect;
import net.atobaazul.spellslinger.modifier.on_hit_handlers.SoulshotOnHit;
import net.atobaazul.spellslinger.registry.SpellslingerDataAttachments;
import net.atobaazul.spellslinger.registry.SpellslingerItems;
import net.atobaazul.spellslinger.registry.SpellslingerMobEffects;
import net.atobaazul.spellslinger.registry.SpellslingerSoundEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.slf4j.Logger;
import team.lodestar.lodestone.handlers.LodestoneRenderHandler;
import team.lodestar.lodestone.helpers.DamageTypeHelper;
import team.lodestar.lodestone.helpers.RandomHelper;
import team.lodestar.lodestone.registry.common.tag.LodestoneDamageTypeTags;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.List;
import java.util.Optional;

import static net.atobaazul.spellslinger.registry.SpellslingerDataAttachments.MYSTIC_REVERB_DURATION;
import static net.atobaazul.spellslinger.registry.SpellslingerDataAttachments.MYSTIC_REVERB_INCOMING_DAMAGE;
import static net.atobaazul.spellslinger.registry.SpellslingerItems.MYSTIC_REVERB_NECKLACE;

@Mod(Spellslinger.MODID)
public class Spellslinger {
    public static final String MODID = "spellslinger";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Spellslinger(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        SpellslingerItems.register(modEventBus);
        SpellslingerMobEffects.register(modEventBus);
        SpellslingerDataAttachments.register(modEventBus);
        SpellslingerSoundEvents.register(modEventBus);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }

    @SubscribeEvent
    public void onEntityHurt(LivingDamageEvent.Post event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();
        MobEffectInstance effect = entity.getEffect(SpellslingerMobEffects.SPIRIT_SHRED);

        if (effect != null && source.is(LodestoneDamageTypeTags.IS_MAGIC)) {
            int level = effect.getAmplifier();
            entity.removeEffect(SpellslingerMobEffects.SPIRIT_SHRED);
            //We re-add the effect with 1 tick left instead of removing outright so multi-hits can work properly.
            entity.addEffect(new MobEffectInstance(SpellslingerMobEffects.SPIRIT_SHRED, 1, level));
        }
    }

    //We need this to apply soul exposed to explosion hit entities.
    @SubscribeEvent
    public void onEntityHurtPre(LivingDamageEvent.Pre event) {
        LivingEntity entity = event.getEntity();
        Entity directEntity = event.getSource().getDirectEntity();
        LivingEntity shooter = (LivingEntity) event.getSource().getEntity();

        if (directEntity instanceof Bullet bullet) {
            CuriosApi.getCuriosInventory(shooter).ifPresent(curiosInventory -> {
                Optional<SlotResult> mysticReverb = curiosInventory.findFirstCurio(MYSTIC_REVERB_NECKLACE.get());
                if (mysticReverb.isPresent()) {
                    entity.setData(MYSTIC_REVERB_DURATION, 200);
                    entity.setData(MYSTIC_REVERB_INCOMING_DAMAGE, event.getOriginalDamage()*0.1f);
                }
            });

            ShotProfile profile = bullet.getProfile();
            List<PostHitEffect> postHitEffects = profile.components().getOrCreate(ShotComponents.POST_HIT_EFFECTS).all();
            postHitEffects.forEach(postHitEffect -> {
                if (postHitEffect instanceof SoulshotOnHit) {
                    if (entity instanceof LivingEntity living) {
                        var data = living.getData(MalumAttachmentTypes.LIVING_SOUL_INFO);
                        data.setExposed();
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public void onEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        Level level = entity.level();
        if (entity instanceof LivingEntity living) {

            if (level instanceof ServerLevel serverLevel) {
                float incomingDamage = living.getData(SpellslingerDataAttachments.INCOMING_MAGIC_DAMAGE);
                if (incomingDamage > 0) {
                    living.hurtTime = 0;
                    living.hurt(DamageTypeHelper.create(serverLevel, DamageTypes.MAGIC, living.getLastAttacker(), living.getLastAttacker()), incomingDamage);
                    living.setData(SpellslingerDataAttachments.INCOMING_MAGIC_DAMAGE, 0f);
                }

                int reverbDuration = entity.getData(MYSTIC_REVERB_DURATION);

                if (reverbDuration > 0) {
                    reverbDuration--;
                    entity.setData(MYSTIC_REVERB_DURATION, reverbDuration);
                } else {
                    entity.setData(MYSTIC_REVERB_INCOMING_DAMAGE, 0f);
                }
            } else if (level instanceof ClientLevel clientLevel) {
                if (living.getData(MYSTIC_REVERB_INCOMING_DAMAGE) > 0) {
                    RandomSource random = clientLevel.random;

                    var color = ColorParticleData.create(MalumSpiritTypes.AERIAL_COLORS().primaryColor(), MalumSpiritTypes.WICKED_COLORS().primaryColor()).setCoefficient(2.5f).setEasing(Easing.SINE_IN_OUT).build();
                    int lifeTime = RandomHelper.randomBetween(random, 50, 60);
                    float scale = RandomHelper.randomBetween(random, 0.7F, 0.9F);
                    float vx = RandomHelper.randomBetween(random, -0.025F, 0.025F)*1.2f;
                    float vy = RandomHelper.randomBetween(random, 0.025F, 0.025F)*1.2f;
                    float vz = RandomHelper.randomBetween(random, -0.025F, 0.025F)*1.2f;

                    var lightSpecs = SpiritLightSpecs.spiritLightSpecs(clientLevel, entity.getEyePosition().add(0,0.5,0), color);
                    lightSpecs.getBuilder().setRenderTarget(LodestoneRenderHandler.LATE_DEFERRED_RENDER).setLifetime(lifeTime).setScaleData(GenericParticleData.create(scale, 0.0F).setEasing(Easing.SINE_IN_OUT).build()).setTransparencyData(GenericParticleData.create(0.05F, 0.2F, 0.0F).setEasing(Easing.EXPO_OUT, Easing.SINE_IN_OUT).build()).addMotion(vx, vy, vz);
                    lightSpecs.spawnParticlesRaw();
                }
            }
        }
    }


}
