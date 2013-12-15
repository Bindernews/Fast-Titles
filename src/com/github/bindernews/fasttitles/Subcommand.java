package com.github.bindernews.fasttitles;

import java.util.List;

import org.bukkit.command.CommandSender;

public interface Subcommand {

	String getName();
	
	String getUsage();
	
	String getLabel();
	
	String getDescription();
	
	String getPermission();
	
	String getPermissionMessage();
	
	List<String> getAliases();
	
	boolean execute(CommandSender sender, String alias, String[] args);
	
	List<String> tabComplete(CommandSender sender, String alias, String[] args);
}
