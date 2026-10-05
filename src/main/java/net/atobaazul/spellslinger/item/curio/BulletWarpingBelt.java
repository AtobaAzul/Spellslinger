package net.atobaazul.spellslinger.item.curio;

import com.sammy.malum.common.item.IMalumEventResponder;
import com.sammy.malum.common.item.IVoidItem;
import com.sammy.malum.common.item.curiosities.curios.MalumCurioItem;
import io.redspace.irons_artifice.api.GunAnimations;
import io.redspace.irons_artifice.data.ReloadResult;
import io.redspace.irons_artifice.item.GunItem;
import io.redspace.irons_artifice.item.GunplayManager;
import io.redspace.irons_artifice.item.ReloadState;
import io.redspace.irons_artifice.network.packets.ClientboundGunAnimationPacket;
import net.atobaazul.spellslinger.Spellslinger;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import software.bernie.geckolib.animatable.GeoItem;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Optional;
import java.util.function.Consumer;

public class BulletWarpingBelt extends MalumCurioItem implements IMalumEventResponder {
    public BulletWarpingBelt(Properties properties, MalumTrinketType type) {
        super(properties, type);
    }

    @Override
    public void addExtraTooltipLines(Consumer<Component> consumer) {
        super.addExtraTooltipLines(consumer);

        consumer.accept(Component.translatable("spellslinger.curio.bullet_warp_belt").withStyle(ChatFormatting.BLUE));
    }
}
