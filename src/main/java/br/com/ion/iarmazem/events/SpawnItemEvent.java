package br.com.ion.iarmazem.events;

import br.com.ion.iarmazem.data.database.DatabaseMethod;
import br.com.ion.iarmazem.model.PlotModel;
import com.google.common.eventbus.Subscribe;
import com.plotsquared.core.PlotAPI;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import com.plotsquared.core.plot.world.PlotAreaManager;
import org.bukkit.Location;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class SpawnItemEvent {

    private final PlotAPI api;
    private final DatabaseMethod databaseMethod;
    private final PlotAreaManager plotAreaManager;

    public SpawnItemEvent(PlotAPI api, DatabaseMethod databaseMethod) {
        this.api = api;
        this.databaseMethod = databaseMethod;
        this.plotAreaManager = api.getPlotSquared().getPlotAreaManager();
        api.registerListener(this);
    }


    @Subscribe
    public void toSpawnItem(ItemSpawnEvent e) throws SQLException {
        Location location = e.getLocation();
        if (verifyIsPlot(location)) return;


        if (!databaseMethod.hasPlot(plot.getId().toString())) {
            databaseMethod.setDefaultPlot(plot);
        }
        PlotModel plotModel = databaseMethod.getPlotModelByID(plot.getId().toString());
        ItemStack itemStack = e.getEntity().getItemStack();
        if (plotModel.getArmazemModel().hasItem(itemStack.getTypeId() + "")) {
            int limite = plotModel.getLimite();
            int itensSize = plotModel.getArmazemModel().getItensSize();
            int amount = itemStack.getAmount();
            if (limite >= (itensSize + amount)) {
                plotModel.getArmazemModel().addItem(itemStack.getTypeId() + "", amount);
                databaseMethod.saveArmazemModel(plotModel);
                e.setCancelled(true);
                return;
            }
            for (UUID uuid : plot.getOwners()) {
                try {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player.isOnline()) ActionBarAPI.send(player, "§cArmazém lotado!");
                } catch (Exception ignored) {
                }
            }
        }
    }

    private boolean verifyIsPlot(Location l) {
        String worldName = Objects.requireNonNull(l.getWorld()).getName();
        if (plotAreaManager.hasPlotArea(worldName)) {
            return false
        }
        for (PlotArea plotArea : api.getPlotAreas(worldName)) {
            plotArea.getPlotAbs(l)
        }


        return
    }
}
