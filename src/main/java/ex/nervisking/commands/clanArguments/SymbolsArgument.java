package ex.nervisking.commands.clanArguments;

import ex.api.base.command.*;
import ex.nervisking.ClanManager;
import ex.nervisking.ExClan;
import ex.nervisking.gui.GuiSymbols;
import ex.nervisking.models.Clan;
import ex.nervisking.models.Rank;
import ex.nervisking.models.Symbols;

import java.util.Arrays;

@CommandArg(name = "symbols", permission = true)
public class SymbolsArgument implements CommandArgument {

    private final ClanManager clanManager;

    public SymbolsArgument(ExClan plugin) {
        this.clanManager = plugin.getClanManager();
    }

    @Override
    public void execute(Sender sender, Arguments args) {
        if (args.lacksMinArgs(1)) {
            sender.help("Usa /clan symbols <rank> <symbol>");
        }

        Rank rank = Rank.fromString(args.get(0));
        if (rank == null) {
            sender.sendMessage("%prefix% &cEl rango no exite.");
            return;
        }

        Clan clan = clanManager.getClan(sender.getUniqueId());
        if (clan == null) {
            sender.sendMessage("%prefix% &cNo estás en un clan.");
            return;
        }

        if (!clan.isLader(sender.getUniqueId())) {
            sender.sendMessage("%prefix% &cNo eres líder del clan.");
            return;
        }

        if (args.hasMaxArgs(2)) {
            Symbols symbol = Symbols.fromString(args.get(1));
            if (symbol == null) {
                sender.sendMessage("%prefix% &cEl símbolo no es válido.");
                return;
            }
            clan.setSymbols(rank, symbol);
            sender.sendMessage("%prefix% &aHas cambiado el símbolo al rango " + rank.getDisplayName() + " por el símbolo " + symbol + ".");
            return;
        }

        GuiSymbols.open(sender.asPlayer(), (result, symbol) -> {
            if (result && symbol != null) {
                clan.setSymbols(rank, symbol);
                sender.sendMessage("%prefix% &aHas cambiado el símbolo al rango " + rank.getDisplayName() + " por el símbolo " + symbol.getSymbol() + ".");
            } else {
                sender.sendMessage("%prefix% &cHas cancelado la acción.");
            }
        });
    }

    @Override
    public Completions tab(Sender sender, Arguments args, Completions completions) {
        if (args.has(1)) {
            completions.add(Arrays.stream(Rank.values()).toList().stream().map(Rank::name).toList());
        }
        if (args.has(2)) {
            completions.add(Symbols.getSymbols().stream().map(Symbols::getSymbol).toList());
        }
        return completions;
    }
}