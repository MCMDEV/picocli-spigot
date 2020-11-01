package de.mcmdev.picoclispigot;

import com.google.common.collect.ImmutableList;
import de.mcmdev.picoclispigot.converters.ConverterMaterial;
import de.mcmdev.picoclispigot.converters.ConverterPlayer;
import de.mcmdev.picoclispigot.converters.ConverterWorld;
import de.mcmdev.picoclispigot.utils.ChatStream;
import de.mcmdev.picoclispigot.utils.CommandTokenizer;
import de.mcmdev.picoclispigot.utils.ReflectionUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import picocli.AutoComplete;
import picocli.CommandLine;

import java.util.*;

public class PicocliSpigotCommand<K> extends Command {

    private final Class<K> commandClass;
    private final JavaPlugin javaPlugin;
    private final String permission;

    private static Map<Class, CommandLine.ITypeConverter> pureConverters = new HashMap<>();

    public static <T> void registerPureConverter(Class<T> forClass, CommandLine.ITypeConverter<T> converter) {
        pureConverters.put(forClass, converter);
    }

    public PicocliSpigotCommand(Class<K> commandClass, JavaPlugin javaPlugin, String permission) {
        super(getPicoliCommand(commandClass).name(),
                Arrays.toString(getPicoliCommand(commandClass).description()),
                "Undefined",
                Arrays.asList(getPicoliCommand(commandClass).aliases())
        );

        registerPureConverter(Player.class, new ConverterPlayer());
        registerPureConverter(World.class, new ConverterWorld());
        registerPureConverter(Material.class, new ConverterMaterial());

        this.commandClass = commandClass;
        this.javaPlugin = javaPlugin;
        this.permission = permission;
    }

    public void register()  {
        CommandMap commandMap = ReflectionUtil.getCommandMap(javaPlugin);
        commandMap.register(getLabel(), javaPlugin.getName(), this);
        for (String alias : getPicoliCommand(commandClass).aliases()) {
            commandMap.register(alias, javaPlugin.getName(), this);
        }
    }

    @Override
    public String getName() {
        return getPicoliCommand(commandClass).name();
    }

    @Override
    public String getDescription() {
        return Arrays.toString(getPicoliCommand(commandClass).description());
    }

    @Override
    public String getPermission() {
        return permission;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList(getPicoliCommand(commandClass).aliases());
    }

    @Override
    public String getUsage() {
        PicocliSpigotFactory factory = new PicocliSpigotFactory(javaPlugin, null);
        CommandLine commandLine = new CommandLine(factory.create(commandClass), factory);
        return commandLine.getUsageMessage();
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        PicocliSpigotFactory factory = new PicocliSpigotFactory(javaPlugin, sender);

        CommandLine commandLine = new CommandLine(factory.create(commandClass), factory);
        commandLine.setColorScheme(new CommandLine.Help.ColorScheme.Builder(CommandLine.Help.Ansi.OFF).build());
        pureConverters.forEach(commandLine::registerConverter);

        commandLine.setOut(new ChatStream(sender, false));
        commandLine.setErr(new ChatStream(sender, true));

        configureCommandSpec(commandLine);

        commandLine.execute(CommandTokenizer.toProperArgs(args));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return tabComplete(sender, alias, args, null);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location) throws IllegalArgumentException {
        PicocliSpigotFactory factory = new PicocliSpigotFactory(javaPlugin, sender);

        CommandLine.Model.CommandSpec spec = CommandLine.Model.CommandSpec.forAnnotatedObject(factory.create(commandClass), factory);

        configureCommandSpec(spec);

        String[] newArgs = CommandTokenizer.toTabCompleteArgs(args);
        List<CharSequence> candidates = new LinkedList<>();

        AutoComplete.complete(spec, newArgs, newArgs.length - 1,
                newArgs[newArgs.length - 1].length(), 500, candidates);

        return candidates.stream()
                .distinct()
                .map(CharSequence::toString)
                .map(s -> newArgs[newArgs.length - 1] + s)
                .collect(ImmutableList.toImmutableList());

    }

    private void configureCommandSpec(CommandLine line) {
        configureCommandSpec(line.getCommandSpec());
    }

    private void configureCommandSpec(CommandLine.Model.CommandSpec spec) {
        spec.usageMessage().width(55);
        spec.mixinStandardHelpOptions(true);
    }

    private static CommandLine.Command getPicoliCommand(Class<?> commandClass)   {
        return commandClass.getAnnotation(CommandLine.Command.class);
    }
}
