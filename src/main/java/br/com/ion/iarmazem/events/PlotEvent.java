package br.com.ion.iarmazem.events;

import br.com.ion.iarmazem.IArmazem;
import br.com.ion.iarmazem.LogHelper;
import br.com.ion.iarmazem.data.database.DatabaseMethod;
import com.google.common.eventbus.Subscribe;
import com.plotsquared.core.PlotAPI;
import com.plotsquared.core.events.PlayerClaimPlotEvent;
import com.plotsquared.core.events.PlotDeleteEvent;
import com.plotsquared.core.plot.PlotId;
import org.bukkit.Bukkit;

import java.sql.SQLException;

public class PlotEvent {

    private final DatabaseMethod databaseMethod;
    private final IArmazem main;

    public PlotEvent(PlotAPI api, DatabaseMethod databaseMethod, IArmazem main) {
        this.databaseMethod = databaseMethod;
        this.main = main;
        api.registerListener(this);
    }

    @Subscribe
    public void onPlayerClaimPlot(PlayerClaimPlotEvent e) throws SQLException {
        if (!databaseMethod.hasPlot(e.getPlot().getId().toString())) {
            databaseMethod.setDefaultPlot(e.getPlot());
        }
        LogHelper.logI(e.getPlot().getId().toString() + " deletado do banco de dados");
    }

    @Subscribe
    public void onPlayerDeletePlot(PlotDeleteEvent e) throws SQLException {
        if (databaseMethod.hasPlot(e.getPlot().getId().toString())) {
            databaseMethod.deletePlot(e.getPlot());
        }
    }
}
