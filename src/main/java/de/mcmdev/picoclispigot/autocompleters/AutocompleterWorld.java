package de.mcmdev.picoclispigot.autocompleters;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Iterator;

public class AutocompleterWorld implements Iterable<String> {

    @Override
    public Iterator<String> iterator() {
        return Bukkit.getWorlds().stream().map(World::getName).iterator();
    }

}