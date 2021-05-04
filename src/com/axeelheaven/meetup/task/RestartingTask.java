package com.axeelheaven.meetup.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.axeelheaven.meetup.Main;
import com.axeelheaven.meetup.api.MeetupRestartEvent;
import com.axeelheaven.meetup.util.TimeUtil;

public class RestartingTask extends BukkitRunnable {
	
	private Main plugin;
	private int seconds;

	public RestartingTask(final Main main) {
		this.plugin = main;
		this.seconds = this.plugin.getConfig().getInt("settings.restarting_seconds");
	}

	@Override
	public void run() {
		seconds --;
		if(this.seconds == 60 || this.seconds == 50 || this.seconds == 40 || this.seconds == 30 || this.seconds == 20 ||
				this.seconds == 10 || this.seconds == 5 || this.seconds == 4 || this.seconds == 3 || this.seconds == 2 || this.seconds == 1) {
			for(final Player player : Bukkit.getOnlinePlayers()) {
				player.sendMessage(this.plugin.text(player, plugin.getLang().getString("restarting").replace("<formatted>", TimeUtil.getInstance().formated(seconds)).replace("<seconds>", TimeUtil.getInstance().getSeconds(seconds, false))));
			}
		} else if(seconds < 1) {
			Bukkit.getServer().getPluginManager().callEvent(new MeetupRestartEvent());
		}
	}
	
	public void start() {
		this.runTaskTimer(this.plugin, 20L, 20L);
	}
	
	public int getSeconds() {
		return this.seconds;
	}

}
