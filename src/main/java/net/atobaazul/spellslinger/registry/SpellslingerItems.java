package net.atobaazul.spellslinger.registry;

import com.sammy.malum.common.item.curiosities.curios.AbstractMalumCurioItem;
import io.redspace.irons_artifice.modifier.ModifierItem;
import io.redspace.irons_artifice.modifier.modifiers.ChainShotModifier;
import net.atobaazul.spellslinger.Spellslinger;
import net.atobaazul.spellslinger.item.curio.AmmoBeltItem;
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


    public static final DeferredItem<ModifierItem> SPIRIT_SHREDDER_MODIFIER = ITEMS.registerItem(
            "spirit_shredder_modifier", properties -> new VoidModifierItem(properties.stacksTo(1), new SpiritShredderModifier()));

    public static final DeferredItem<AmmoBeltItem> AMMO_BELT = ITEMS.registerItem("ammo_belt", properties -> new AmmoBeltItem(properties.stacksTo(1), AbstractMalumCurioItem.MalumTrinketType.METALLIC));

    //Void items
    public static final DeferredItem<ModifierItem> SOULSHOT_MODIFIER = ITEMS.registerItem(
            "soulshot_modifier", properties -> new ModifierItem(properties.stacksTo(1), new SoulshotModifier()));

    public static final DeferredItem<MysticReverbNecklaceItem> MYSTIC_REVERB_NECKLACE = ITEMS.registerItem("mystic_reverb_necklace", properties -> new MysticReverbNecklaceItem(properties.stacksTo(1), AbstractMalumCurioItem.MalumTrinketType.METALLIC));
}
