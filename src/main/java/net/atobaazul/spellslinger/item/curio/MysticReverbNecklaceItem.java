package net.atobaazul.spellslinger.item.curio;

import com.sammy.malum.common.block.ether.EtherBlockEntity;
import com.sammy.malum.common.item.IMalumEventResponder;
import com.sammy.malum.common.item.IVoidItem;
import com.sammy.malum.common.item.curiosities.curios.MalumCurioItem;
import com.sammy.malum.common.item.curiosities.weapons.staff.HexStaffItem;
import com.sammy.malum.core.systems.events.CollectSpiritEvent;
import com.sammy.malum.core.systems.spirit.type.SpiritColorProperties;
import com.sammy.malum.registry.common.MalumParticles;
import com.sammy.malum.registry.common.magic.MalumSpiritTypes;
import com.sammy.malum.visual_effects.SparkParticleEffects;
import com.sammy.malum.visual_effects.SpiritLightSpecs;
import net.atobaazul.spellslinger.registry.SpellslingerSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import team.lodestar.lodestone.handlers.LodestoneRenderHandler;
import team.lodestar.lodestone.helpers.ColorHelper;
import team.lodestar.lodestone.helpers.DamageTypeHelper;
import team.lodestar.lodestone.helpers.RandomHelper;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.particle.data.spin.SpinParticleData;
import team.lodestar.lodestone.systems.particle.render_types.LodestoneWorldParticleRenderType;
import team.lodestar.lodestone.systems.particle.world.behaviors.DirectionalParticleBehavior;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import static net.atobaazul.spellslinger.registry.SpellslingerDataAttachments.MYSTIC_REVERB_DURATION;
import static net.atobaazul.spellslinger.registry.SpellslingerDataAttachments.MYSTIC_REVERB_INCOMING_DAMAGE;

public class MysticReverbNecklaceItem extends MalumCurioItem implements IMalumEventResponder, IVoidItem {
    public MysticReverbNecklaceItem(Properties properties, MalumTrinketType type) {
        super(properties, type);
    }

    @Override
    public void addExtraTooltipLines(Consumer<Component> consumer) {
        super.addExtraTooltipLines(consumer);

        consumer.accept(Component.translatable("spellslinger.curio.mystic_reverb_necklace").withStyle(ChatFormatting.BLUE));
    }

    @Override
    public void spiritCollectionEvent(CollectSpiritEvent event, LivingEntity collector, double arcaneResonance) {
        boolean hurt = false;

        if (collector.level() instanceof ServerLevel serverLevel && collector instanceof Player player) {
            List<LivingEntity> entities = serverLevel.getEntities(EntityTypeTest.forClass(LivingEntity.class), new AABB(player.blockPosition()).inflate(24), entity -> entity.getData(MYSTIC_REVERB_DURATION) > 0);
            for (LivingEntity entity : entities) {
                float incomingDamage = entity.getData(MYSTIC_REVERB_INCOMING_DAMAGE);

                //double-checking, realistically this should never be 0 though
                if (incomingDamage > 0) {
                    entity.invulnerableTime = 0;
                    hurt = entity.hurt(DamageTypeHelper.create(serverLevel, DamageTypes.MAGIC, player, player), (float) (incomingDamage*arcaneResonance));
                    entity.invulnerableTime = 0;
                }
            }

            if (hurt) {
                float pitch = Mth.lerp(serverLevel.getRandom().nextFloat(), 0.8f, 1.2f);
                float volume = Mth.lerp(serverLevel.getRandom().nextFloat(), 0.6f, 0.8f);
                serverLevel.playSound(null, collector.blockPosition(), SpellslingerSoundEvents.MYSTIC_REVERB.get(), SoundSource.PLAYERS, volume, pitch);
            }
        }
    }
}