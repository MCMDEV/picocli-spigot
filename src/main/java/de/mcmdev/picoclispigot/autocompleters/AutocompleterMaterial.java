package de.mcmdev.picoclispigot.autocompleters;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.Iterator;

public class AutocompleterMaterial implements Iterable<String> {

    @Override
    public Iterator<String> iterator() {
        return Arrays.stream(Material.values()).map(Enum::name).iterator();
    }

}