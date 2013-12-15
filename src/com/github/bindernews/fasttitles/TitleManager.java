package com.github.bindernews.fasttitles;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;

public class TitleManager {

	private FastTitles fastTitles;
	private Map<String, String> titleMap = new HashMap<String, String>();
	
	public TitleManager(FastTitles fast) {
		fastTitles = fast;
	}
	
	public void loadFromConfig(Configuration conf) {
		clearTitles();
		ConfigurationSection cfgs = conf.getConfigurationSection("titles");
		if (cfgs != null) {
			for(String key : cfgs.getKeys(true)) {
				setTitle(key, cfgs.getString(key));
			}
		}
	}
	
	public void saveToConfig(Configuration conf) {
		clearTitles();
		ConfigurationSection cfgs = conf.getConfigurationSection("titles");
		cfgs = (cfgs != null) ? cfgs : new MemoryConfiguration();
		for(Entry<String,String> title : titleMap.entrySet()) {
			cfgs.set(title.getKey(), title.getValue());
		}
	}

	public String getTitle(String title) {
		return titleMap.get(title.toLowerCase());
	}
	
	public void setTitle(String title, String format) {
		if (format != null) {
			titleMap.put(title.toLowerCase(), format);
		} else {
			titleMap.remove(title.toLowerCase());
		}
	}

	public Iterable<String> getTitleIter() {
		return titleMap.keySet();
	}

	public void clearTitles() {
		titleMap.clear();
	}
	
	public void setPlayerTitle(Player p, String title, boolean save)
			throws UnknownTitleException {
		String confPath = "players." + p.getName();
		if (title.equalsIgnoreCase("none")) {
			p.setDisplayName(p.getName());
			if (save) {
				fastTitles.getConfig().set(confPath, null);
			}
		} else {
			String titleFormat = getTitle(title);
			if (titleFormat != null) {
				p.setDisplayName(formatString(colorize(title), p.getName()));
				if (save) {
					fastTitles.getConfig().set(confPath, title);
				}
			} else {
				throw new UnknownTitleException("That title doesn't exist");
			}
		}
	}
	
	public static String formatString(String in, String username) {
		in = in.replace("&N", username);
		return in;
	}
	
	public static String colorize(String in) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < in.length(); i++) {
			if (in.charAt(i) == '&' && i < in.length() - 1) {
				char ctrl = in.charAt(i + 1);
				ChatColor cc = ChatColor.getByChar(ctrl);
				if (cc != null) {
					sb.append(cc);
					i++;
				} else if (ctrl == '&') {
					sb.append('&');
					i++;
				} else {
					sb.append('&');
				}
			} else {
				sb.append(in.charAt(i));
			}
		}
		return sb.toString();
	}
}
