package com.github.bindernews.fasttitles;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class BaseComponent {
	
	private Plugin plugin;

	public BaseComponent(Plugin plugin) {
		this.plugin = plugin;
	}
	
	public Plugin getPlugin() {
		return plugin;
	}
	
	public Server getServer() {
		return plugin.getServer();
	}
	
	static boolean ensurePlayer(CommandSender sender) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You must be a player to use this command");
			return false;
		}
		return true;
	}

}
