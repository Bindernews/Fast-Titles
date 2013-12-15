package com.github.bindernews.fasttitles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

public class SubcommandMap{

	private Map<String, Subcommand> cmdMap = new HashMap<String, Subcommand>(10);

	public static final int PAGE_SIZE = 6;

	public SubcommandMap() {
	}

	public void showGeneralHelp(CommandSender sender, String cmdName, int page, String format) {
		for (Subcommand c : cmdMap.values()) {
			if (c.getName().startsWith(cmdName)) {
				sender.sendMessage(String.format(format, c.getLabel(), c.getDescription()));
			}
		}
	}

	public void showUsage(CommandSender sender, String cmdName, String format)
			throws CommandException {
		Subcommand cmd = checkCommand(cmdName);
		sender.sendMessage(String.format(format, cmd.getLabel(), cmd.getUsage()));
	}

	public void clearCommands() {
		cmdMap.clear();
	}

	public boolean dispatch(CommandSender sender, String commandLine)
			throws CommandException {
		String[] splitCmd = splitCommandLine(commandLine);
		return dispatch(sender, splitCmd);
	}

	public boolean dispatch(CommandSender sender, String[] splitCmd)
			throws CommandException {
		if (splitCmd.length == 0) {
			throw new CommandException("No command specified");
		}
		String cmdName = splitCmd[0];
		if (cmdName.startsWith("/")) {
			cmdName = cmdName.substring(1);
		}
		Subcommand cmd = checkCommand(cmdName);
		if (!testEmptyString(cmd.getPermission())) {
			if (!sender.hasPermission(cmd.getPermission())) {
				sender.sendMessage(cmd.getPermissionMessage());
				return true;
			}
		}
		String[] args = Arrays.copyOfRange(splitCmd, 1, splitCmd.length);
		return cmd.execute(sender, cmdName, args);
	}

	public Subcommand getCommand(String name) {
		return cmdMap.get(name);
	}

	public Subcommand checkCommand(String name) throws CommandException {
		Subcommand cmd = cmdMap.get(name);
		if (cmd == null) {
			throw new CommandException("Command \"" + name + "\" not found");
		}
		return cmd;
	}
	
	public boolean register(String fallbackPrefix, Subcommand command) {
		return register(command.getLabel(), fallbackPrefix, command);
	}

	public boolean register(String label, String fallbackPrefix, Subcommand command) {
		String cmdName = label;
		boolean usedFallback = false;
		if (cmdMap.containsKey(cmdName)) {
			do {
				cmdName = ":" + cmdName;
			} while (cmdMap.containsKey(fallbackPrefix + cmdName));
			cmdName = fallbackPrefix + cmdName;
			usedFallback = true;
		}
		cmdMap.put(cmdName, command);
		return usedFallback;
	}

	public void registerAll(String fallbackPrefix, List<Subcommand> commands) {
		for (Subcommand cmd : commands) {
			register(fallbackPrefix, cmd);
		}
	}

	public List<String> tabComplete(CommandSender sender, String commandLine)
			throws IllegalArgumentException {
		String[] splitCmd = splitCommandLine(commandLine);
		return tabComplete(sender, splitCmd);
	}

	// advanced tab-completion
	public List<String> tabComplete(CommandSender sender, String[] splitCmd)
			throws IllegalArgumentException {
		// this will return a list of the sub commands
		if (splitCmd.length == 0
				|| (splitCmd.length == 1 && splitCmd[0].length() == 0)) {
			return new ArrayList<String>(cmdMap.keySet());
		}
		// extract command name
		String cmdName = splitCmd[0];
		if (cmdName.startsWith("/")) {
			cmdName = cmdName.substring(1);
		}
		// if a command is partially typed then do completion with that name
		if (!cmdMap.containsKey(cmdName) && splitCmd.length == 1) {
			ArrayList<String> opts = new ArrayList<String>();
			for (String name : cmdMap.keySet()) {
				if (name.startsWith(splitCmd[0]))
					opts.add(name);
			}
			return opts;
		} else {
			Subcommand cmd = checkCommand(cmdName);
			String[] args = Arrays.copyOfRange(splitCmd, 1, splitCmd.length);
			return cmd.tabComplete(sender, cmdName, args);
		}
	}

	public void printMapping() {
		Logger log = Bukkit.getServer().getLogger();
		for (Entry<String, Subcommand> ent : cmdMap.entrySet()) {
			log.info(ent.getKey() + " : " + ent.getValue());
		}
	}

	public static boolean testEmptyString(String s) {
		return s == null || s.equals("");
	}

	public String[] splitCommandLine(String cmdLine) {
		return cmdLine.split(" ");
	}

}
