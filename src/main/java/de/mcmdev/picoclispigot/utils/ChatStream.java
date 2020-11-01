package de.mcmdev.picoclispigot.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public class ChatStream extends PrintWriter {

    private final CommandSender sender;
    private final boolean error;

    public ChatStream(CommandSender sender, boolean error) {
        super(new OutputStream() {
            @Override
            public void write(int b) {}
        });
        this.sender = sender;
        this.error = error;
    }

    @Override
    public void print(Object object) {
        this.print(object.toString());
    }

    @Override
    public void print(String string) {
        string = string.replace("\r", "");
        sender.sendMessage((error ? ChatColor.RED : ChatColor.AQUA) + string);
    }

    @Override
    public void println(String string) {
        this.print(string);
    }
}
