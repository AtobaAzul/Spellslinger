package net.atobaazul.spellslinger.registry;

import com.sammy.malum.common.item.curiosities.curios.AbstractMalumCurioItem;
import io.redspace.irons_artifice.modifier.ModifierItem;
import net.atobaazul.spellslinger.Spellslinger;
import net.atobaazul.spellslinger.item.curio.BottomlessAmmoBelt;
import net.atobaazul.spellslinger.item.curio.BulletWarpingBelt;
import net.atobaazul.spellslinger.item.curio.MysticReverbNecklaceItem;
import net.atobaazul.spellslinger.item.modifier.VoidModifierItem;
import net.atobaazul.spellslinger.modifier.modifiers.SoulshotModifier;
import net.atobaazul.spellslinger.modifier.modifiers.SpiritShredderModifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SpellslingerItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Spellslinger.MODID);

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

    public static final DeferredItem<ModifierItem> SOULSHOT_MODIFIER = ITEMS.registerItem(
            "soulshot_modifier", properties -> new ModifierItem(properties.stacksTo(1), new SoulshotModifier()));
    public static final DeferredItem<BulletWarpingBelt> BULLET_WARP_BELT = ITEMS.registerItem("bullet_warp_belt", properties -> new BulletWarpingBelt(properties.stacksTo(1), AbstractMalumCurioItem.MalumTrinketType.CLOTH));

    //Void items
    public static final DeferredItem<ModifierItem> SPIRIT_SHREDDER_MODIFIER = ITEMS.registerItem(
            "spirit_shredder_modifier", properties -> new VoidModifierItem(properties.stacksTo(1), new SpiritShredderModifier()));
    public static final DeferredItem<BottomlessAmmoBelt> BOTTOMLESS_AMMO_BELT = ITEMS.registerItem("bottomless_ammo_belt", properties -> new BottomlessAmmoBelt(properties.stacksTo(1), AbstractMalumCurioItem.MalumTrinketType.VOID));

    public static final DeferredItem<MysticReverbNecklaceItem> MYSTIC_REVERB_NECKLACE = ITEMS.registerItem("mystic_reverb_necklace", properties -> new MysticReverbNecklaceItem(properties.stacksTo(1), AbstractMalumCurioItem.MalumTrinketType.VOID));
}
