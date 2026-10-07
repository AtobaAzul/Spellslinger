package net.atobaazul.spellslinger.item.curio;

import com.sammy.malum.common.item.IMalumEventResponder;
import com.sammy.malum.common.item.curiosities.curios.MalumCurioItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

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
