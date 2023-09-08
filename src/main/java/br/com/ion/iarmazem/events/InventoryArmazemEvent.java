package br.com.ion.iarmazem.events;

import com.google.common.eventbus.Subscribe;
import com.plotsquared.core.PlotAPI;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryArmazemEvent {

    private final PlotAPI api;

    public InventoryArmazemEvent(PlotAPI api) {
        this.api = api;
        api.registerListener(this);
    }

    @Subscribe
    public void onClickInventory(InventoryClickEvent e) {
        //api
    }
}
