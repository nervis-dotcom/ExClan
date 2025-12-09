package ex.nervisking.gui;

import ex.api.base.dialog.ButtonSpec;
import ex.api.base.dialog.Dialog;
import ex.api.base.dialog.body.BodyText;
import ex.api.base.model.CustomColor;
import ex.nervisking.models.Symbols;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

public class GuiSymbols {

    public static void open(Player player, BiConsumer<Boolean, Symbols> consumer) {
        Dialog dialog = Dialog.of(player)
                .addBodies(BodyText.of("Elige un Símbolo"))
                .addButton(ButtonSpec.of("&cCancel", "cancel").setDescription("Click para cancelar"))
                .action((result, view) -> {
                    String re = result.actionKey();
                    Symbols symbols = Symbols.fromString(re);
                    if (re.equalsIgnoreCase("cancel")) {
                        consumer.accept(false, null);
                    } if (symbols != null) {
                        consumer.accept(true, symbols);
                    }
                });

        for (var symbol : Symbols.getSymbols()) {
            dialog.addButton(ButtonSpec.of(CustomColor.PURE_RANDOM.getHex() + symbol.getSymbol(), symbol.getName()).setDescription("&f" + symbol.getName(), " ", "&7Click para elegir"));
        }
        dialog.open();
    }
}