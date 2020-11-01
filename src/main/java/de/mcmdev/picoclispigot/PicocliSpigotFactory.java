package de.mcmdev.picoclispigot;

import de.mcmdev.picoclispigot.api.Sender;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import picocli.CommandLine;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class PicocliSpigotFactory implements CommandLine.IFactory {

    private final JavaPlugin accessor;
    private final CommandSender sender;

    public PicocliSpigotFactory(JavaPlugin accessor, CommandSender sender) {
        this.accessor = accessor;
        this.sender = sender;
    }

    @Override
    public <K> K create(Class<K> commandClass) {
        K instance = null;
        try {
            instance = getInstance(commandClass, sender);

            for (Field field : instance.getClass().getDeclaredFields()) {
                if(CommandSender.class.isAssignableFrom(field.getType()) && field.isAnnotationPresent(Sender.class))    {
                    field.setAccessible(true);
                    field.set(instance, sender);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            Bukkit.getConsoleSender().sendMessage("§cPicoli-Spigot could not create command instance, disabling");
            Bukkit.getPluginManager().disablePlugin(accessor);
        }

        return instance;
    }

    private static <K> K getInstance(Class<K> clazz, CommandSender sender) throws IllegalAccessException,
            InvocationTargetException, InstantiationException, IllegalArgumentException {

        try {
            Constructor<K> constructor = clazz.getDeclaredConstructor(CommandSender.class);
            constructor.setAccessible(true);
            return constructor.newInstance(sender);
        } catch (NoSuchMethodException ignored) {}

        try {
            Constructor<K> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException ignored) {}

        throw new IllegalArgumentException("The supplied class did not have a no-args or " +
                "ICommandSender.class constructor");
    }
}
