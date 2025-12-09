package ex.nervisking.commands;

import ex.api.base.command.*;
import ex.nervisking.ExClan;

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
            default -> sender.help("Usage: /exclan reload");
        }
    }

    @Override
    public Completions onTab(Sender sender, Arguments args, Completions completions) {
        if (args.has(1)) {
            completions.add("reload", "gui", "sota", "border", "book", "diálogo");
        }

        return completions;
    }
}