package br.com.ion.iarmazem;

import br.com.ion.iarmazem.commands.CommandArmazem;
import br.com.ion.iarmazem.data.database.DatabaseMethod;
import br.com.ion.iarmazem.data.database.HikariConnect;
import br.com.ion.iarmazem.events.InventoryArmazemEvent;
import br.com.ion.iarmazem.events.PlotEvent;
import br.com.ion.iarmazem.events.SpawnItemEvent;
import br.com.ion.iarmazem.exceptions.InvalidDatabaseSchemaException;
import com.plotsquared.core.PlotAPI;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;
import java.util.Objects;

public final class IArmazem extends JavaPlugin {

    private Economy economy;
    private HikariConnect hikariConnect;

    private Permission perms;

    private DatabaseMethod databaseMethod;

    private ArmazemInventory armazemInventory;


    @Override
    public void onEnable() {
        LogHelper.logI("Iniciando...");
        saveDefaultConfig();
        setupEconomy();
        setupDatabase();
        setupDomain();
        setupEventsAndCommands();
        setupPermissions();

    }

    private void setupDomain() {
        armazemInventory = new ArmazemInventory(this);
    }

    private void setupEventsAndCommands() {
        if (Bukkit.getPluginManager().getPlugin("PlotSquared") != null) {
            PlotAPI api = new PlotAPI();
            new PlotEvent(api, databaseMethod, this);
            getServer().getPluginManager().registerEvents(new InventoryArmazemEvent(api, databaseMethod, economy), this);
            getServer().getPluginManager().registerEvents(new SpawnItemEvent(api, databaseMethod), this);
            getCommand("armazem").setExecutor(new CommandArmazem(api, armazemInventory, databaseMethod));
        } else {
            throw new RuntimeException("Missing PlotSquared plugin");
        }
    }

    private void setupDatabase() {
        try {
            onEnableBanco();
            databaseMethod = new DatabaseMethod(hikariConnect, this);
            databaseMethod.createTable();
            LogHelper.logI("Banco de dados iniciado com sucesso!");
        } catch (SQLException e) {
            LogHelper.logI("Erro ao criar banco de dados: " + e);
        }

    }

    private void setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    @Override
    public void onDisable() {
        LogHelper.logI("Conexão com o banco de dados encerrada.");
        LogHelper.logI("Plugin desligando.");
    }

    public void onEnableBanco() {
        hikariConnect = new HikariConnect();
        switch (Objects.requireNonNull(getConfig().getString("Banco"))) {
            case "SQLite" -> hikariConnect.SQLConnectLoad(new File(getDataFolder(), "database").toString());
            case "MySQL" ->
                    hikariConnect.MySQLConnectLoad(getConfig().getString("MySQL.host"), getConfig().getString("MySQL.database"),
                            getConfig().getString("MySQL.user"), getConfig().getString("MySQL.password"));

            default -> throw new InvalidDatabaseSchemaException();
        }
    }

    public void setEconomy(Economy economy) {
        this.economy = economy;
    }

    public Economy getEconomy() {
        return this.economy;
    }
}
