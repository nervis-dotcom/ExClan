package ex.nervisking.commands;

import ex.api.base.Ex;
import ex.api.base.command.*;
import ex.api.base.dialog.ButtonSpec;
import ex.api.base.dialog.Dialog;
import ex.api.base.dialog.body.BodyItem;
import ex.api.base.dialog.body.BodyText;
import ex.api.base.dialog.botton.InputBoolean;
import ex.api.base.dialog.botton.InputNumberRange;
import ex.api.base.dialog.botton.InputOption;
import ex.api.base.dialog.botton.InputText;
import ex.api.base.item.ItemBuilder;
import ex.api.base.utils.BookBuilder;
import ex.nervisking.ExClan;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandInfo(name = "exclan", description = "Main command for ExClan plugin", permission = true)
public class CommandMain extends CustomCommand {

    private final ExClan plugin;

    public CommandMain(ExClan plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onCommand(Sender sender, Arguments args) {
        if (args.isEmpty()) {
            sender.help("Usage: /exclan reload");
            return;
        }

        String action = args.toLowerCase(0);
        switch (action) {
            case "reload" -> {
                sendMessage(sender, "%prefix% &aPlugin recargado...");
                plugin.getMainConfig().reloadConfig();
            }
            case "border" -> {
                if (args.lacksMinArgs(2)) {
                    sender.sendMessage("Usa /exclan border [set | reset]");
                    return;
                }

                switch (args.get(1)) {
                    case "set" -> createWorldBorder(sender.asPlayer(), sender.asPlayer().getLocation(), 100);
                    case "reset" -> removeWorldBorder(sender.asPlayer());
                    default -> sender.sendMessage("Usa /exclan border [set | reset]");
                }

            }
            case "book" -> {
                if (sender.isConsole()) {
                    sender.noConsole();
                    return;
                }

                BookBuilder.of(sender.asPlayer())
                        .setTitle("&6&lGuía del Servidor")
                        .setAuthor("&#00ffccNervis")
                        .registerPlaceholder("%player%", Player::getName)
                        .addPage(
                                "&aBienvenido %player%",
                                "&#ff0000Usa <bold>/spawn</bold> para volver",
                                "Texto con <blue>MiniMessage</blue>"
                        )
                        .addPageMixed(
                                "Página con ",
                                BookBuilder.clickableText("&eClick aquí", "&bHaz click!", "/help"),
                                " y más texto."
                        )
                        .open();
            }

            case "diálogo" -> {
                Dialog.DialogBuild dialog = Dialog.of(sender.asPlayer())
                        .setTitle("&ePlayer Setup")
                        .setExternalTitle("&ePlayer tus")
                        .addBodies(BodyItem.of(ItemBuilder.of(ItemBuilder.CLOSE), "hola").setShowDecorations(false))
                        .addBodies(BodyText.of("%prefix%"))
                        .addInput(InputText.of("&bName","name").setLabelVisible(false))
                        .addInput(InputText.of("&bDescription","description"))
                        .addInput(InputBoolean.of("&3status", "Status").setInitial(true))
                        .addInputs(InputNumberRange.of("level", "&6Level", 5, 40).setStep(1).setInitial(10))
                        .addInput(InputOption.of("&aOpciones","tus")
                                .addOption(InputOption.Option.of("Primera", "&dPrimera"))
                                .addOption(InputOption.Option.of("Segunda", "&eSegunda")))
                        .addButton(ButtonSpec.of("&aConfirm", "confirm").setDescription("Click para confirmar"))
                        .addButton(ButtonSpec.of("&cCancel", "cancel").setDescription("Click para cancelar"))
                        .addButton(ButtonSpec.close("&4Close").setDescription("Click para cerrar"))
                        .action((result, view) -> {
                            switch (result.actionKey()) {
                                case "confirm" -> {
                                    String name = view.getText("name");
                                    String description = view.getText("description");
                                    float level = view.getFloat("level", 0);
                                    String option = view.getText("tus");

                                    sender.sendMessage("✔ Confirmaste: Name=" + name +
                                            " Description=" + description +
                                            ", Level=" + level +
                                            ", Option=" + option);
                                }
                                case "cancel" -> sender.sendMessage("❌ Cancelaste");
                            }
                        }).build();

                Ex.openDialog(dialog);
            }
            default -> sender.help("Usage: /exclan reload");
        }
    }

    public void createWorldBorder(@NotNull Player player, Location center, int islandSize) {
        WorldBorder border = Bukkit.createWorldBorder();
        border.setCenter(center);
        border.setSize(islandSize);
        player.setWorldBorder(border);
    }

    public void removeWorldBorder(@NotNull Player player) {
        player.setWorldBorder(null);
    }

    @Override
    public Completions onTab(Sender sender, Arguments args, Completions completions) {
        if (args.has(1)) {
            completions.add("reload", "gui", "sota", "border", "book", "diálogo");
        }

        return completions;
    }
}