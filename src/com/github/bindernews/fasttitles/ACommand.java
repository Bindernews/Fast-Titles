package com.github.bindernews.fasttitles;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;

public @interface ACommand {
	/**
	 * The command's aliases.
	 * The first alias is used as the name.
	 */
	String[] alias();
	
	/**
	 * The command's usage
	 */
	String usage();
	
	/**
	 * The command's description
	 */
	String desc();
	
	/**
	 * The permission for the command.
	 * Optional.
	 */
	String permission() default "";
	
	/**
	 * The name of the tab-completing method that goes with
	 * this command. Defaults to nothing.
	 */
	String completer() default "";
	
	String flags() default "";
	
	int min() default 0;
	
	int max() default 0;
	
	
	public static class Executor implements TabExecutor {
		public final ACommand annot;
		private Object cmdinstance;
		private Method mcommand;
		private Method mcomplete;

		public Executor(ACommand acmd, Object obj, Method cmd, Method compl) {
			annot = acmd;
			mcommand = cmd;
			mcomplete = compl;
			cmdinstance = obj;
		}

		@Override
		public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
			CommandContext cc = new CommandContext(sender, cmd, alias, args, annot);
			try {
				return (Boolean)mcommand.invoke(cmdinstance, cc);
			} catch (ReflectiveOperationException e) {
				throw new CommandException("", e);
			}
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
			CommandContext cc = new CommandContext(sender, cmd, alias, args, annot);
			try {
				if (mcomplete != null) {
					return (List<String>)mcomplete.invoke(cmdinstance, cc);
				} else {
					return null;
				}
			} catch (ReflectiveOperationException e) {
				throw new CommandException("", e);
			}
		}
		
		public void setAsExecutor(Command cmd) {
			cmd.setDescription(annot.desc());
			cmd.setUsage(annot.usage());
			cmd.setPermission(annot.permission());
			cmd.setAliases(new ArrayList<String>(Arrays.asList(annot.alias())));
			if (cmd instanceof PluginCommand) {
				PluginCommand pcmd = ((PluginCommand)cmd); 
				pcmd.setExecutor(this);
				pcmd.setTabCompleter(this);
			}
		}
	}
	
	public static class Wrapper extends Command {
		private Executor executor;
		
		public Wrapper(Executor ex) {
			super(ex.annot.alias()[0]);
			executor = ex;
			executor.setAsExecutor(this);
		}

		@Override
		public boolean execute(CommandSender sender, String alias, String[] args) {
			return executor.onCommand(sender, this, alias, args);
		}
		
		@Override
		public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
			return executor.onTabComplete(sender, this, alias, args);
		}
	}
}
