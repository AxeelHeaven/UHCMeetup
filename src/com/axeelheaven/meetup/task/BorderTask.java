package com.axeelheaven.meetup.task;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.axeelheaven.meetup.Main;
import com.axeelheaven.meetup.listeners.WorldBorderListener;
import com.axeelheaven.meetup.util.TimeUtil;

public class BorderTask extends BukkitRunnable {

	private Main plugin;
	private int seconds;
	private boolean runnable;
	private int next_time;
	private int next_border;
	private int shrink = 0;
	
	public BorderTask(final Main plugin) {
		this.plugin = plugin;
		this.update();
	}
	
	@Override
	public void run() {
		this.seconds--;

		if (this.seconds == 900 || this.seconds == 600 || this.seconds == 300 || this.seconds == 240 || this.seconds == 180 || this.seconds == 120 || this.seconds == 60 || this.seconds == 30 || this.seconds == 15 || this.seconds == 10 || (this.seconds <= 5 && this.seconds > 0)) {
			for(final Player player : Bukkit.getOnlinePlayers()) {
				player.sendMessage(plugin.text(player, this.plugin.getLang().getString("border_countdown").replace("<borde>", String.valueOf(next_border)).replace("<countdown>", TimeUtil.getInstance().getSeconds(seconds, false)).replace("<formated>", TimeUtil.getInstance().formated(seconds))));
			}
		} else if(this.seconds < 0) {
            if (this.shrink == this.plugin.getConfig().getStringList("border_settings.border_shrinks").size()) {
				Bukkit.getScheduler().cancelTask(this.getTaskId());
				this.runnable = false;
			}
			this.plugin.getGameManager().setBorder(this.next_border);
			this.plugin.getWorldManager().addBedrockBorder(this.plugin.getGameManager().getBorder(), this.plugin.getConfig().getInt("border_settings.border_height"));
            for(final Player player : Bukkit.getOnlinePlayers()) {
            	player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 1F, 1F);
            	player.sendMessage(this.plugin.text(player, this.plugin.getLang().getString("border_shrunk").replace("<borde>", String.valueOf(this.plugin.getGameManager().getBorder()))));
            	WorldBorderListener.update(player);
            }
            this.update();
		}
		
		
	}
	
	private void update() {
		if(this.shrink == this.plugin.getConfig().getStringList("border_settings.border_shrinks").size()) return;
		final String[] strings = ((String)plugin.getConfig().getStringList("border_settings.border_shrinks").get(shrink)).split(";");
		this.next_border = Integer.valueOf(strings[0]).intValue();
		this.next_time = Integer.valueOf(strings[1]).intValue();
        this.seconds = this.next_time;
        this.shrink++;
	}
	
	public void start() {
		this.runnable = true;
		this.runTaskTimer(this.plugin, 20L, 20L);
	}
	
	public boolean isRunnable() {
		return this.runnable;
	}
	
	public int getShrinl() {
		return this.shrink;
	}

	public int getSeconds() {
		return this.seconds;
	}

}
