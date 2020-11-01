package de.mcmdev.picoclispigot.converters;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import picocli.CommandLine;

public class ConverterPlayer implements CommandLine.ITypeConverter<Player> {
    @Override
    public Player convert(String value) {
        return Bukkit.getPlayer(value);
    }
}
