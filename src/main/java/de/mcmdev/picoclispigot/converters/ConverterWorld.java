package de.mcmdev.picoclispigot.converters;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import picocli.CommandLine;

public class ConverterWorld implements CommandLine.ITypeConverter<World> {
    @Override
    public World convert(String value) {
        return Bukkit.getWorld(value);
    }
}
