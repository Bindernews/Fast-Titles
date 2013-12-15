package com.github.bindernews.fasttitles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

public class CommandContext {

	public final ACommand annot;
	public final CommandSender sender;
	public final Command command;
	public final String alias;
	public final String[] originalArgs;
	public final String[] args;
	public final Set<String> flags;
	
	public CommandContext(CommandSender sender, Command cmd, String alias, String[] theargs, ACommand annot_) {
		this.sender = sender;
		this.command = cmd;
		this.alias = alias;
		this.originalArgs = theargs;
		this.annot = annot_;
		this.flags = new HashSet<String>();
		
		ArrayList<String> argv = new ArrayList<String>();
		for(int i=0; i<theargs.length; i++) {
			String tt = theargs[i];
			if (tt.charAt(0) == '-' && tt.length() == 2) {
				char flagChar = tt.charAt(1);
				if (annot.flags().indexOf(flagChar) == -1) {
					throw new CommandException("Unknown flag -" + flagChar);
				} else {
					flags.add(tt);
				}
			} else {
				argv.add(tt);
			}
		}
		args = new String[argv.size()];
		argv.toArray(args);
		if (args.length < annot.min()) {
			throw new CommandException("Too few arguments");
		}
		if (annot.max() != 0 && args.length > annot.max()) {
			throw new CommandException("Too many arguments");
		}
	}
	
	public boolean hasFlag(String flag) {
		return flags.contains(flag);
	}
	

}
