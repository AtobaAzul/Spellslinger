package net.atobaazul.spellslinger.item.curio;

import com.sammy.malum.common.item.IMalumEventResponder;
import com.sammy.malum.common.item.curiosities.curios.MalumCurioItem;
import com.sammy.malum.core.systems.events.CollectSpiritEvent;
import io.redspace.irons_artifice.api.GunAnimations;
import io.redspace.irons_artifice.item.GunItem;
import io.redspace.irons_artifice.item.GunplayManager;
import io.redspace.irons_artifice.item.MagazineContents;
import io.redspace.irons_artifice.item.ReloadState;
import io.redspace.irons_artifice.network.packets.ClientboundGunAnimationPacket;
import io.redspace.irons_artifice.utils.IronsArtificeTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import software.bernie.geckolib.animatable.GeoItem;

import java.util.function.Consumer;

public class AmmoBeltItem extends MalumCurioItem implements IMalumEventResponder {
    public AmmoBeltItem(Properties properties, MalumTrinketType type) {
        super(properties, type);
    }

    @Override
    public void addExtraTooltipLines(Consumer<Component> consumer) {
        super.addExtraTooltipLines(consumer);

        consumer.accept(Component.translatable("spellslinger.curio.ammo_belt").withStyle(ChatFormatting.BLUE));
    }

    @Override
    public void spiritCollectionEvent(CollectSpiritEvent event, LivingEntity collector, double arcaneResonance) {
        if (collector.level() instanceof ServerLevel serverLevel && collector instanceof Player player) {
            int ammoCount = GunplayManager.countBullets(player);

            if (ammoCount > 0) {
                for (ItemStack stack : player.getAllSlots()) {
                    if (stack.getItem() instanceof GunItem gunItem) {
                        int missing = GunItem.getMagazine(stack).missing(gunItem.magazineCapacity());

                        if (missing > 0) {
                            GunplayManager.attemptFinishReload(player, stack, (int) Math.min(Math.floor(arcaneResonance), missing));

                            //we need to cancel reload animation if it is happening.
                            ReloadState state = ReloadState.get(stack);
                            if (state != null) {
                                ReloadState.remove(stack);

                                ClientboundGunAnimationPacket packet = new ClientboundGunAnimationPacket(player.getId(), GeoItem.getOrAssignId(stack, (ServerLevel) player.level()), stack == player.getMainHandItem() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, GunAnimations.IDLE, 2, 1, 0, 0);
                                PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, packet);
                            }
                        }
                    }
                }
            }
        }
    }
}
