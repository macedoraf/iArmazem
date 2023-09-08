package br.com.ion.iarmazem;

import org.bukkit.Bukkit;

public abstract class LogHelper {

    public static void logI(String message) {
        Bukkit.getConsoleSender().sendMessage("§5" + Constants.TAG + ":" + "§a" + message);
    }
}
