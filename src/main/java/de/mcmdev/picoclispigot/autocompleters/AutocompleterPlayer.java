package de.mcmdev.picoclispigot.autocompleters;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;

import java.util.Iterator;

public class AutocompleterPlayer implements Iterable<String> {

    @Override
    public Iterator<String> iterator() {
        return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).iterator();
    }

}