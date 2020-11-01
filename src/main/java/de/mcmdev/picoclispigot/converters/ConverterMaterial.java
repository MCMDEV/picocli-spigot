package de.mcmdev.picoclispigot.converters;

import org.bukkit.Material;
import picocli.CommandLine;

public class ConverterMaterial implements CommandLine.ITypeConverter<Material> {
    @Override
    public Material convert(String value) {
        return Material.valueOf(value);
    }
}
