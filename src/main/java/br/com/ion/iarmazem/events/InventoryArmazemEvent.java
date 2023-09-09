package br.com.ion.iarmazem.events;

import br.com.ion.iarmazem.PlotHelper;
import br.com.ion.iarmazem.data.database.DatabaseMethod;
import br.com.ion.iarmazem.model.ArmazemModel;
import br.com.ion.iarmazem.model.PlotModel;
import com.google.common.eventbus.Subscribe;
import com.plotsquared.core.PlotAPI;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.world.PlotAreaManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.UUID;

public class InventoryArmazemEvent {

    private final PlotAPI api;
    private final DatabaseMethod databaseMethod;
    private final PlotAreaManager plotAreaManager;

    private final Economy economy;

    public InventoryArmazemEvent(PlotAPI api, DatabaseMethod databaseMethod, Economy economy) {
        this.api = api;
        this.databaseMethod = databaseMethod;
        this.plotAreaManager = api.getPlotSquared().getPlotAreaManager();
        this.economy = economy;
        api.registerListener(this);
    }

    @Subscribe
    public void onClickInventory(InventoryClickEvent e) throws SQLException {
        ItemStack itemStack = e.getCurrentItem();
        if (itemStack == null || itemStack.getType() == Material.AIR) return;
        if (!e.getView().getTitle().equalsIgnoreCase("§8Armazem")) return;
        HumanEntity whoClicked = e.getWhoClicked();
        e.setCancelled(true);
        if (!(whoClicked instanceof Player)) return;
        Player player = (Player) whoClicked;
        PlotAPI plotAPI = new PlotAPI();
        Location location = player.getLocation();
        Plot plot = PlotHelper.findPlotByLocation(plotAreaManager, plotAPI, location);
        if (plot == null) return;
        PlotModel plotModel = databaseMethod.getPlotModelByID(plot.getId().toString());
        if (plotModel.getArmazemModel().hasItem(itemStack.getType().toString())) {
            ArmazemModel armazemModel = plotModel.getArmazemModel();
            int amountItem = armazemModel.getAmountItemByID(itemStack.getType().toString());
            if (amountItem <= 0) {
                player.sendMessage("§cVocê não tem itens para vender.");
            } else {
                DecimalFormat df = new DecimalFormat("#,###,###,##0.##");
                int money = amountItem * armazemModel.getPriceItemByID(itemStack.getType().toString());
                economy.depositPlayer(player, money);

                player.sendMessage("");
                if (amountItem == 1)
                    player.sendMessage("§a§lVENDIDO: §aVocê vendeu: §7" + amountItem + " item §apor: §f" + df.format(money) + "§a coins.");
                else
                    player.sendMessage("§a§l VENDIDO: §aVocê vendeu: §f" + amountItem + " itens §apor: §f" + df.format(money) + "§a coins.");
                player.sendMessage("");

                armazemModel.setAmountItemByID(itemStack.getType().toString(), 0);
                plotModel.setArmazemModel(armazemModel);
                databaseMethod.saveArmazemModel(plotModel);
            }
        } else {
            if (plotModel.getUpgradePrice() > economy.getBalance(player)) {
                player.sendMessage("§cVocê não tem money suficiente para upar o limite.");
                player.closeInventory();
                return;
            }
            if (!plotModel.hasNextLimite()) {
                player.sendMessage("§cSeu armazém já alcançou o nível máximo.");
                player.closeInventory();
                return;
            }
            economy.withdrawPlayer(player, plotModel.getUpgradePrice());
            for (UUID uuid : plot.getOwners()) {
                Player owner = Bukkit.getPlayer(uuid);
                owner.sendMessage("");
                owner.sendMessage("§6§l UPGRADE: §aSeu armazém tem um novo limite de: §a§l" + plotModel.getNextLimite() + " §aitens.");
                owner.sendMessage("");
                owner.playSound(owner.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            }
            plotModel.setUpgradeLimite();
            databaseMethod.saveLimite(plotModel);
        }
        player.closeInventory();
    }
}
