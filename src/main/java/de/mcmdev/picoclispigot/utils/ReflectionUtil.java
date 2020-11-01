package de.mcmdev.picoclispigot.utils;

import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public class ReflectionUtil {

    public static CommandMap getCommandMap(JavaPlugin accessor) {
        if ( Bukkit.getPluginManager() instanceof SimplePluginManager ) {
            SimplePluginManager spm = (SimplePluginManager) Bukkit.getPluginManager();
            try {
                Field field = FieldUtils.getDeclaredField( spm.getClass(), "commandMap", true );
                return (CommandMap) field.get( spm );
            } catch ( IllegalAccessException e ) {
                Bukkit.getConsoleSender().sendMessage("§cPicoli-Spigot could not find CommandMap, disabling!");
                Bukkit.getPluginManager().disablePlugin(accessor);
                return null;
            }
        }
        return null;
    }

}
