package me.endergaming.plugins.backend.exceptions;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;

import java.util.List;

public class AddonException extends Exception {
    private final ImmutableList<String> messages;

    public AddonException(Iterable<String> messages) {
        this.messages = ImmutableList.copyOf(messages);
    }

    public AddonException(String... messages) {
        this.messages = ImmutableList.copyOf(messages);
    }

    public List<String> getMessages() {
        return this.messages;
    }

    public boolean hasMessages() {
        return !this.messages.isEmpty();
    }

    public String getMessage() {
        return Joiner.on(".\n" + ChatColor.RED).join(this.getMessages());
    }
}
