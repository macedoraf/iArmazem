package br.com.ion.iarmazem.commands;

import br.com.ion.iarmazem.ArmazemInventory;
import br.com.ion.iarmazem.data.database.DatabaseMethod;
import br.com.ion.iarmazem.model.PlotModel;
import com.plotsquared.bukkit.player.BukkitPlayer;
import com.plotsquared.core.PlotAPI;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class CommandArmazem implements CommandExecutor {

    private final PlotAPI plotAPI;
    private final ArmazemInventory armazemInventory;
    private final DatabaseMethod databaseMethod;

    public CommandArmazem(PlotAPI plotAPI, ArmazemInventory armazemInventory, DatabaseMethod databaseMethod) {
        this.plotAPI = plotAPI;
        this.armazemInventory = armazemInventory;
        this.databaseMethod = databaseMethod;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cApenas jogadores podem executar esse comando.");
            return true;
        }
        Player player = (Player) sender;
        PlotAPI plotAPI = new PlotAPI();
        PlotPlayer plotPlayer = BukkitPlayer.from(player);
        Plot plot = plotPlayer.getCurrentPlot();

        if (plot == null) {
            player.sendMessage("§cVocê APENAS pode executar esse comando dentro de um plot.");
            return true;
        }

        if (!plot.getOwners().contains(player.getUniqueId())) {
            if (!plot.getTrusted().contains(player.getUniqueId())) {
                player.sendMessage("§cApenas donos ou pessoas com trust, podem usar esse comando.");
                return true;
            }
        }
        PlotModel plotModel = null;
        try {
            plotModel = databaseMethod.getPlotModelByID(plot.getId().toString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        armazemInventory.openInventory(player, plotModel);

        return false;

    }
}
