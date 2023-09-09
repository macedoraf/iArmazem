package br.com.ion.iarmazem.events;

import br.com.ion.iarmazem.ActionBarAPI;
import br.com.ion.iarmazem.data.database.DatabaseMethod;
import br.com.ion.iarmazem.model.PlotModel;
import com.google.common.eventbus.Subscribe;
import com.plotsquared.bukkit.util.BukkitRegionManager;
import com.plotsquared.core.PlotAPI;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import com.plotsquared.core.plot.PlotId;
import com.plotsquared.core.plot.PlotManager;
import com.plotsquared.core.plot.world.PlotAreaManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

public class SpawnItemEvent implements Listener {

    private final PlotAPI api;
    private final DatabaseMethod databaseMethod;
    private final PlotAreaManager plotAreaManager;

    public SpawnItemEvent(PlotAPI api, DatabaseMethod databaseMethod) {
        this.api = api;
        this.databaseMethod = databaseMethod;
        this.plotAreaManager = api.getPlotSquared().getPlotAreaManager();
    }


    @EventHandler
    public void toSpawnItem(ItemSpawnEvent e) throws SQLException {
        Plot currentPlot = null;
        Location location = e.getLocation();
        PlotArea plotArea = requirePlotArea(location);
        if (plotArea == null) return;

        for (Plot itemPlot : api.getAllPlots()) {
            if (Objects.equals(itemPlot.getArea(), plotArea)) {
                currentPlot = itemPlot;
                break;
            }
        }

        if (currentPlot == null) return;

        if (!databaseMethod.hasPlot(currentPlot.getId().toString())) {
            databaseMethod.setDefaultPlot(currentPlot);
        }

        PlotModel plotModel = databaseMethod.getPlotModelByID(currentPlot.getId().toString());
        ItemStack itemStack = e.getEntity().getItemStack();

        if (plotModel.getArmazemModel().hasItem(itemStack.getType().toString())) {
            int limite = plotModel.getLimite();
            int itensSize = plotModel.getArmazemModel().getItensSize();
            int amount = itemStack.getAmount();
            if (limite >= (itensSize + amount)) {
                plotModel.getArmazemModel().addItem(itemStack.getType().toString(), amount);
                databaseMethod.saveArmazemModel(plotModel);
                e.setCancelled(true);
                return;
            }
            for (UUID uuid : currentPlot.getOwners()) {
                try {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null && player.isOnline()) ActionBarAPI.send(player, "§cArmazém lotado!");
                } catch (Exception ignored) {
                }
            }
        }
    }

    private PlotArea requirePlotArea(Location l) {
        String worldName = Objects.requireNonNull(l.getWorld()).getName();
        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();

        return plotAreaManager
                .getPlotArea(com.plotsquared.core.location.Location.at(worldName, x, y, z));

    }
}
