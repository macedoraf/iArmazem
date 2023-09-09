package br.com.ion.iarmazem;

import com.plotsquared.core.PlotAPI;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import com.plotsquared.core.plot.world.PlotAreaManager;
import org.bukkit.Location;

import java.util.Objects;

public abstract class PlotHelper {

    public static Plot findPlotByLocation(PlotAreaManager manager, PlotAPI plotAPI, Location l) {
        String worldName = Objects.requireNonNull(l.getWorld()).getName();
        Plot currentPlot = null;
        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();

        PlotArea plotArea = manager.getPlotArea(com.plotsquared.core.location.Location.at(worldName, x, y, z));
        for (Plot itemPlot : plotAPI.getAllPlots()) {
            if (Objects.equals(itemPlot.getArea(), plotArea)) {
                currentPlot = itemPlot;
                break;
            }
        }

        return currentPlot;
    }
}
