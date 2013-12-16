package com.github.bindernews.fasttitles;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class BasicSubcommand extends Command implements Subcommand {

	protected BasicSubcommand(String name) {
		super(name);
		init();
	}

	protected BasicSubcommand(String name, String description, String usageMessage,
			List<String> aliases) {
		super(name, description, usageMessage, aliases != null ? aliases : new ArrayList<String>());
		setPermissionMessage("You don't have permission");
		init();
	}

	protected BasicSubcommand(String name, String description, String usageMessage,
			List<String> aliases, String permission) {
		super(name, description, usageMessage, aliases != null ? aliases : new ArrayList<String>());
		setPermission(permission);
		setPermissionMessage("You don't have permission");
		init();
	}

	/**
	 * This can be used in place of a constructor for anonymous subclasses.
	 */
	protected void init() {

	}

	@Override
	public abstract boolean execute(CommandSender sender, String alias,
			String[] args);

	// @Override
	// public abstract List<String> tabComplete(CommandSender sender, String
	// alias, String[] args);

	public boolean register(SubcommandMap cmap) {
		return cmap.register("", this);
	}

	public void showUsage(CommandSender sender, String format) {
		sender.sendMessage(String.format(format, getLabel(), getUsage()));
	}

}
