package ex.nervisking.commands;

import ex.api.base.command.*;
import ex.nervisking.ExClan;
import ex.nervisking.manager.WarManager;

@CommandInfo(name = "exclan", description = "Main command for ExClan plugin", permission = true)
public class CommandMain extends CustomCommand {

    private final ExClan plugin;
    private final WarManager warManager;

    public CommandMain(ExClan plugin) {
        this.plugin = plugin;
        this.warManager = plugin.getPointsWarManager();
    }

    @Override
    public void onCommand(Sender sender, Arguments args) {
        if (args.isEmpty()) {
            sender.help("Usage: /exclan <reload | war-point | war-bank>");
            return;
        }

        String action = args.toLowerCase(0);
        switch (action) {
            case "reload" -> {
                sender.sendMessage("%prefix% &aPlugin recargado...");
                plugin.reload();
            }
            case "war-point" -> {
                if (args.lacksMinArgs(2)){
                    sender.sendMessage("%prefix% &cUsage: /exclan war-point <start | stop>");
                    return;
                }
                String subAction = args.toLowerCase(1);
                switch (subAction) {
                    case "start" -> {
                        if (args.lacksMinArgs(3)) {
                            sender.sendMessage("%prefix% &cUsage: /exclan war-point start <time>");
                            return;
                        }

                        long time;
                        try {
                            time = parseTime(args.get(2));
                        } catch (NumberFormatException e) {
                            sender.invalidityAmount();
                            return;
                        }

                        if (warManager.startPoint(time)) {
                            sender.sendMessage("%prefix% &aGuerra de puntos iniciada.");
                        } else {
                            sender.sendMessage("%prefix% &cYa hay una guerra de puntos en curso.");
                        }
                    }
                    case "stop" -> {
                        if (warManager.stopPoint()) {
                            sender.sendMessage("%prefix% &aGuerra de puntos detenida.");
                        } else {
                            sender.sendMessage("%prefix% &cNo hay una guerra de puntos en curso.");
                        }
                    }
                    default -> sender.help("Usage: /exclan war-point <start | stop>");
                }
            }
            case "war-bank" -> {
                if (args.lacksMinArgs(2)){
                    sender.sendMessage("%prefix% &cUsage: /exclan war-bank <start | stop>");
                    return;
                }
                String subAction = args.toLowerCase(1);
                switch (subAction) {
                    case "start" -> {
                        if (args.lacksMinArgs(3)) {
                            sender.sendMessage("%prefix% &cUsage: /exclan war-bank start <time>");
                            return;
                        }

                        long time;
                        try {
                            time = parseTime(args.get(2));
                        } catch (NumberFormatException e) {
                            sender.invalidityAmount();
                            return;
                        }

                        if (warManager.startBank(time)) {
                            sender.sendMessage("%prefix% &aGuerra de dinero iniciada.");
                        } else {
                            sender.sendMessage("%prefix% &cYa hay una guerra de dinero en curso.");
                        }
                    }
                    case "stop" -> {
                        if (warManager.stopBank()) {
                            sender.sendMessage("%prefix% &aGuerra de dinero detenida.");
                        } else {
                            sender.sendMessage("%prefix% &cNo hay una guerra de dinero en curso.");
                        }
                    }
                    default -> sender.help("Usage: /exclan war-bank <start | stop>");
                }
            }
            default -> sender.help("Usage: /exclan <reload | war-point | war-bank>");
        }
    }

    @Override
    public Completions onTab(Sender sender, Arguments args, Completions completions) {
        if (args.has(1)) {
            completions.add("reload", "war-point", "war-bank");
        }
        if (args.has(2) && args.equalsIgnoreCase(0, "war-point", "war-bank")) {
            completions.add("start", "stop");
        }

        return completions;
    }
}