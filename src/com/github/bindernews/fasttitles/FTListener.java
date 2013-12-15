package com.github.bindernews.fasttitles;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class FTListener implements Listener {

	private FastTitles ft;

	public FTListener(FastTitles fast) {
		ft = fast;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		if (e.getMessage().startsWith(ft.chatColorPrefix)
				&& e.getPlayer().hasPermission("fasttitles.colorchat")) {
			String baseMsg = e.getMessage().substring(
					ft.chatColorPrefix.length()); // get rid of chat prefix
			e.setMessage(TitleManager.colorize(baseMsg));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLogin(PlayerLoginEvent event) {
		String confPath = "players." + event.getPlayer().getName();
		String title = ft.getConfig().getString(confPath, "none");
		try {
			ft.titleman.setPlayerTitle(event.getPlayer(), title, false);
		} catch (UnknownTitleException ex) {
			ft.getConfig().set(confPath, null);
		}
	}
}
