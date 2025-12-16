package ex.nervisking.commands.clanArguments;

import ex.api.base.command.*;
import ex.api.base.hook.ViaHook;
import ex.api.base.model.ClientVersion;
import ex.api.base.model.ParseVariable;
import ex.nervisking.manager.ClanManager;
import ex.nervisking.gui.GuiText;
import ex.nervisking.models.Clan;
import ex.nervisking.models.Rank;
import ex.nervisking.models.Symbols;

import java.util.Arrays;

@CommandArg(name = "symbols", permission = true)
public record SymbolsArgument(ClanManager clanManager) implements CommandArgument {

    @Override
    public void execute(Sender sender, Arguments args) {
        if (args.lacksMinArgs(1)) {
            sender.helpLang("symbols.usage");
            return;
        }

        Rank rank = Rank.fromString(args.get(0));
        if (rank == null) {
            sender.sendLang("symbols.invalid-rank");
            return;
        }

        Clan clan = clanManager.getClan(sender.getUniqueId());
        if (clan == null) {
            sender.sendLang("no-clan");
            return;
        }

        if (!clan.isLader(sender.getUniqueId())) {
            sender.sendLang("not-leader");
            return;
        }

        if (args.hasMaxArgs(2)) {
            Symbols symbol = Symbols.froSimbol(args.get(1));
            if (symbol == null) {
                sender.sendLang("symbols.invalid-symbol");
                return;
            }
            clan.setSymbols(rank, symbol);
            sender.sendLang("symbols.changed", ParseVariable.adD("%rank%", rank.getDisplayName()).add("%symbol%", symbol.getSymbol()));
            return;
        }

        if (ViaHook.isAtLeast(sender.getPlayer(), ClientVersion.v1_21_6)) {
            GuiText.openSimbolo(sender.getPlayer(), (result, symbol) -> {
                if (result) {
                    clan.setSymbols(rank, symbol);
                    sender.sendLang("symbols.changed", ParseVariable.adD("%rank%", rank.getDisplayName()).add("%symbol%", symbol.getSymbol()));
                } else {
                    sender.sendLang("symbols.canceled");
                }
            });
        } else {
            sender.sendLang("symbols.gui-not-supported");
        }
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