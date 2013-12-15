package com.github.bindernews.fasttitles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class TitlesCommand implements TabExecutor {

	private SubcommandMap cmdMap;
	private FastTitles ft;

	public static final String HELP_FORMAT = ChatColor.GREEN + "/title %s - "
			+ ChatColor.AQUA + "%s";
	public static final String USAGE_FORMAT = ChatColor.GREEN + "/title %s - "
			+ ChatColor.RED + "%s";

	public TitlesCommand(FastTitles fast) {
		cmdMap = new SubcommandMap();
		cmdTitleSet.register(cmdMap);
		cmdTitleList.register(cmdMap);
		cmdTitleReload.register(cmdMap);
		cmdTitleRemove.register(cmdMap);
		cmdTitleCreate.register(cmdMap);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command,
			String label, String[] args) {
		return cmdMap.tabComplete(sender, args);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		try {
			cmdMap.dispatch(sender, args);
		} catch (CommandException e) {
			cmdMap.showGeneralHelp(sender, "", 0, HELP_FORMAT);
		}
		return true;
	}

	public final BasicSubcommand cmdTitleList = new BasicSubcommand("list",
			"List all available titles", "", null, "fasttitles.title.list") {
		@Override
		public boolean execute(CommandSender sender, String alias, String[] args) {
			for (String s : ft.titleman.getTitleIter()) {
				sender.sendMessage(ChatColor.AQUA + s + " - "
						+ ft.titleman.getTitle(s));
			}
			return true;
		}
	};

	public final BasicSubcommand cmdTitleReload = new BasicSubcommand("reload",
			"Reload Fast Titles configuration", "", null,
			"fasttitles.title.reload") {
		@Override
		public boolean execute(CommandSender sender, String alias, String[] args) {
			ft.loadConfig();
			sender.sendMessage(ChatColor.GREEN + "Configuration reloaded");
			return true;
		}
	};

	public final BasicSubcommand cmdTitleRemove = new BasicSubcommand("remove",
			"Remove a title", "<name>", null, "fasttitles.title.remove") {
		@Override
		public boolean execute(CommandSender sender, String alias, String[] args) {
			if (args.length == 0) {
				sender.sendMessage(ChatColor.RED
						+ "Please specify a title name");
			} else {
				ft.titleman.setTitle(args[0], null);
			}
			return true;
		}
	};

	public final BasicSubcommand cmdTitleCreate = new BasicSubcommand("create",
			"Create a new title",
			"<name> \"<format string (spaces allowed)>\"",
			Arrays.asList("modify"), "fasttitles.title.create") {
		@Override
		public boolean execute(CommandSender sender, String alias, String[] args) {
			String[] nargs = resplitArgsWithQuotes(args, 0, args.length);
			if (nargs.length < 2) {
				showUsage(sender, TitlesCommand.USAGE_FORMAT);
				return true;
			}
			if (!nargs[1].contains("%s")) {
				sender.sendMessage(ChatColor.RED
						+ "Insert %s in the format where you want the player's name to be");
				return true;
			}
			ft.titleman.setTitle(nargs[0], nargs[1]);
			return true;
		}
	};

	public final BasicSubcommand cmdTitleSet = new BasicSubcommand("set",
			"Set a player's title", "<title> [player]", null,
			"fasttitles.title.set") {
		@Override
		public boolean execute(CommandSender sender, String alias, String[] args) {
			String title = args[0];
			Player p;
			if (args.length > 1) {
				p = Bukkit.getPlayer(args[1]);
			} else if (sender instanceof Player) {
				p = (Player) sender;
			} else {
				sender.sendMessage(ChatColor.RED
						+ "Error. You must specify a player.");
				return true;
			}
			if (p != sender) {
				if (!sender.hasPermission("fasttitles.title.set.other")) {
					sender.sendMessage(ChatColor.RED
							+ "You don't have permission to set other's titles");
					return true;
				}
			}

			if (title.equalsIgnoreCase("none")
					|| sender.hasPermission("fasttitles.title.name." + title)
					|| sender.isOp()) {
				try {
					ft.titleman.setPlayerTitle(p, title, true);
				} catch (UnknownTitleException e) {
					sender.sendMessage(ChatColor.RED
							+ "That title doesn't exist");
				}
			} else {
				sender.sendMessage(ChatColor.RED
						+ "You don't have permission to use that title");
			}
			return true;
		}
	};

	public void setTabExecutor(PluginCommand cmd) {
		cmd.setExecutor(this);
		cmd.setTabCompleter(this);
	}

	/**
	 * Build a new String[] holding the arguments separated by spaces but
	 * allowing quoted arguments
	 * 
	 * @param args
	 *            Original argument array
	 * @param begin
	 *            Beginning index
	 * @param end
	 *            Ending index
	 * @return The newly split arguments
	 */
	public static String[] resplitArgsWithQuotes(String[] args, int begin,
			int end) {
		ArrayList<String> ret = new ArrayList<String>(args.length);
		StringBuilder line = new StringBuilder();
		boolean inStr = false;
		for (int i = begin; i < end; i++) {
			String s = args[i];
			if (inStr) {
				boolean isEndStr = s.endsWith("\"") ? !s.endsWith("\\\"")
						|| s.endsWith("\\\\\"") : false;
				if (isEndStr) {
					line.append(s.substring(0, s.length() - 1));
					ret.add(line.toString());
					line.delete(0, line.length());
					inStr = false;
				} else {
					line.append(s);
					line.append(' ');
				}
			} else if (s.startsWith("\"")) {
				inStr = true;
				line.append(s.substring(1));
			} else if (!s.equals("")) {
				ret.add(s);
			}
		}
		return ret.toArray(new String[ret.size()]);
	}
}
