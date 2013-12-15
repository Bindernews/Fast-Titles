package com.github.bindernews.fasttitles;

import org.bukkit.plugin.java.JavaPlugin;

public class FastTitles extends JavaPlugin {

	private static FastTitles instance;

	private TitlesCommand titlesCommand = new TitlesCommand(this);
	private FTListener ftListener = new FTListener(this);

	public TitleManager titleman = new TitleManager(this);
	public boolean chatColorEnabled = false;
	public String chatColorPrefix = "@@ ";

	private FastTitles self = this;

	private Runnable saveConfigRunnable = new Runnable() {
		public void run() {
			titleman.saveToConfig(getConfig());
			getServer().getScheduler().runTaskAsynchronously(self,
					new Runnable() {
						public void run() {
							saveConfig();
						}
					});
		}
	};

	/**
	 * Interval for saving the config file. This is in server ticks. Note: 30
	 * ticks ~= 1 second
	 */
	private long configSaveInterval;

	public FastTitles() {
		instance = this;
	}

	@Override
	public void onEnable() {
		getLogger().info(
				"Enabling FastTitles v" + getDescription().getVersion());
		loadConfig();
		saveConfig();
		titlesCommand.setTabExecutor(getCommand("title"));
		getServer().getPluginManager().registerEvents(ftListener, this);
		getServer().getScheduler().runTaskTimer(this, saveConfigRunnable,
				configSaveInterval * 30 /* initial delay */,
				configSaveInterval * 30 /* period */);
	}

	@Override
	public void onDisable() {
		getLogger().info(
				"Disabling FastTitles v" + getDescription().getVersion());
	}

	public void loadConfig() {
		reloadConfig();
		chatColorEnabled = getConfig().getBoolean("general.chatColorEnabled",
				false);
		chatColorPrefix = getConfig().getString("general.chatColorPrefix",
				"@@ ");
		configSaveInterval = getConfig().getLong("general.configSaveInterval",
				60 * 5); // 60 secs / min * 5 min
		titleman.loadFromConfig(getConfig());
	}

	public static FastTitles get() {
		return instance;
	}
}
