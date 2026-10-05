package net.atobaazul.spellslinger;

import com.mojang.logging.LogUtils;
import com.sammy.malum.core.handlers.GeasEffectHandler;
import com.sammy.malum.core.systems.geas.GeasEffectType;
import com.sammy.malum.registry.common.MalumAttachmentTypes;
import com.sammy.malum.registry.common.MalumCreativeTabs;
import com.sammy.malum.registry.common.MalumParticles;
import com.sammy.malum.registry.common.magic.MalumSpiritTypes;
import io.redspace.irons_artifice.IronsArtifice;
import io.redspace.irons_artifice.api.ComposeShotEvent;
import io.redspace.irons_artifice.api.GunAnimations;
import io.redspace.irons_artifice.data.ParticleStack;
import io.redspace.irons_artifice.data.ReloadResult;
import io.redspace.irons_artifice.data.ShotComponents;
import io.redspace.irons_artifice.data.ValueModifier;
import io.redspace.irons_artifice.entity.Bullet;
import io.redspace.irons_artifice.gun.ShotProfile;
import io.redspace.irons_artifice.item.GunItem;
import io.redspace.irons_artifice.item.GunplayManager;
import io.redspace.irons_artifice.item.ReloadState;
import io.redspace.irons_artifice.menu.GunContainer;
import io.redspace.irons_artifice.modifier.ModifierItem;
import io.redspace.irons_artifice.modifier.PostHitEffect;
import io.redspace.irons_artifice.network.packets.ClientboundGunAnimationPacket;
import io.redspace.irons_artifice.registry.ItemRegistry;
import net.atobaazul.spellslinger.geas.MarksmanGeas;
import net.atobaazul.spellslinger.geas.SpellslingerGeas;
import net.atobaazul.spellslinger.modifier.on_hit_handlers.SoulshotOnHit;
import net.atobaazul.spellslinger.registry.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.slf4j.Logger;
import software.bernie.geckolib.animatable.GeoItem;
import team.lodestar.lodestone.handlers.LodestoneRenderHandler;
import team.lodestar.lodestone.helpers.DamageTypeHelper;
import team.lodestar.lodestone.helpers.RandomHelper;
import team.lodestar.lodestone.registry.common.LodestoneAttributes;
import team.lodestar.lodestone.registry.common.tag.LodestoneDamageTypeTags;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.List;
import java.util.Optional;

import static net.atobaazul.spellslinger.registry.SpellslingerDataAttachments.*;
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
        SpellslingerGeasEffectTypes.GEAS_TYPES.register(modEventBus);

        modEventBus.addListener(this::addCreative);

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
                    entity.setData(MYSTIC_REVERB_INCOMING_DAMAGE, event.getOriginalDamage() * 0.5f);
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
                    living.hurtTime = 0;
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

                    var color = ColorParticleData.create(MalumSpiritTypes.ELDRITCH_COLORS().primaryColor(), MalumSpiritTypes.WICKED_COLORS().primaryColor()).setCoefficient(2.5f).setEasing(Easing.SINE_IN_OUT).build();
                    int lifeTime = RandomHelper.randomBetween(random, 10, 20);
                    float scale = RandomHelper.randomBetween(random, 0.7F, 0.9F);

                    WorldParticleBuilder.create(MalumParticles.HEX_TARGET).setTransparencyData(GenericParticleData.create(0f, 0.2f, 0f).setEasing(Easing.SINE_IN_OUT, Easing.SINE_IN_OUT).build()).setScaleData(GenericParticleData.create(scale, 0).setEasing(Easing.SINE_IN).build()).setRenderTarget(LodestoneRenderHandler.LATE_DEFERRED_RENDER).setLifetime(lifeTime).setColorData(color).enableNoClip().spawn(level, entity.getX(), entity.getEyeY() + 1, entity.getZ());
                }
            }
        }
    }


    private boolean isCurioEquipped(Player player, Item targetItem) {
        Optional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(player);
        return curiosInventory.map(iCuriosItemHandler -> iCuriosItemHandler.findCurios(targetItem).stream().anyMatch(slotResult -> {
            ItemStack stack = slotResult.stack();
            return !stack.isEmpty();
        })).orElse(false);
    }

    @SubscribeEvent
    public void composeShot(ComposeShotEvent event) {
        ShotProfile profile = event.getShotProfile();
        ItemStack itemStack = profile.itemStack();
        LivingEntity shooter = event.getEntity();
        Level level = shooter.level();

        var spellslingerGeas = GeasEffectHandler.getGeasEffect(shooter, SpellslingerGeasEffectTypes.PACT_OF_THE_SPELLSLINGER);
        var marksmanGeas = GeasEffectHandler.getGeasEffect(shooter, SpellslingerGeasEffectTypes.PACT_OF_THE_MARKSMAN);

        double magicProficiency = shooter.getAttribute(LodestoneAttributes.MAGIC_PROFICIENCY.getDelegate()).getValue() - 1;

        if (spellslingerGeas instanceof SpellslingerGeas) {
            GunContainer container = new GunContainer(itemStack);
            for (var item : container.getItems()) {
                if (!item.isEmpty() && item.getItem() instanceof ModifierItem modItem && level.random.nextDouble() < (0.25 + magicProficiency)) {
                    modItem.getModifier().apply(profile.components());
                }
            }
        }

        if (marksmanGeas instanceof MarksmanGeas) {
            profile.components().modifyValue(ShotComponents.DAMAGE, new ValueModifier(-0.5, ValueModifier.Operation.MULTIPLY_TOTAL, ValueModifier.Type.HARMFUL));
        }
        if (shooter instanceof Player player) {
            if (isCurioEquipped(player, MYSTIC_REVERB_NECKLACE.get())) {
                profile.components().getOrCreate(ShotComponents.PARTICLE_TRAIL).addAccent(new ParticleStack.ParticleAccent(ParticleTypes.WITCH, 0.5));
            }

        }
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == MalumCreativeTabs.GEAS.getKey()) {
            for (DeferredHolder<GeasEffectType, ? extends GeasEffectType> geasType : SpellslingerGeasEffectTypes.GEAS_TYPES.getEntries()) {
                event.accept(geasType.get().getDummyCreativeStack());
            }
        }

        if (event.getTabKey() == IronsArtifice.CREATIVE_TAB.getKey()) {
            event.insertAfter(ItemRegistry.BLOODLETTING_TIP_MODIFIER.toStack(), SpellslingerItems.SOULSHOT_MODIFIER.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ItemRegistry.BLOODLETTING_TIP_MODIFIER.toStack(), SpellslingerItems.SPIRIT_SHREDDER_MODIFIER.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

            event.insertAfter(ItemRegistry.TRICORNE_HAT.toStack(), SpellslingerItems.BOTTOMLESS_AMMO_BELT.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ItemRegistry.TRICORNE_HAT.toStack(), SpellslingerItems.BULLET_WARP_BELT.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            event.insertAfter(ItemRegistry.TRICORNE_HAT.toStack(), MYSTIC_REVERB_NECKLACE.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

    @SubscribeEvent
    private void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        Level level = player.level();

        if (player.level() instanceof ServerLevel serverLevel) {
            boolean equipped = isCurioEquipped(player, SpellslingerItems.BULLET_WARP_BELT.get());

            if (equipped) {
                int timer = player.getData(AMMO_WARP_TIMER);

                timer -= 1;
                player.setData(AMMO_WARP_TIMER, timer);

                if (timer <= 0) {
                    player.setData(AMMO_WARP_TIMER, 40);
                    for (ItemStack item : player.getInventory().items) {
                        if (!item.isEmpty() && item.getItem() instanceof GunItem gunItem && player.getMainHandItem() != item) {
                            ReloadResult result = GunplayManager.attemptFinishReload(player, item, 1);
                            if (result == ReloadResult.FINISHED_RELOAD) {
                                GunItem.playReloadFeedback(level, player, result);
                                ReloadState.remove(item);

                                ClientboundGunAnimationPacket packet = new ClientboundGunAnimationPacket(player.getId(), GeoItem.getOrAssignId(item, serverLevel), item == player.getMainHandItem() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, GunAnimations.IDLE, 2, 1, 0, 0);
                                PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, packet);
                                break;
                            }
                        }
                    }
                }
            } else {
                if (player.getData(AMMO_WARP_TIMER) < 40) {
                    player.setData(AMMO_WARP_TIMER, 40);
                }
            }
        }
    }
}
