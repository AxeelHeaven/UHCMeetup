package com.axeelheaven.meetup.worldborder;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.axeelheaven.meetup.Main;
import com.axeelheaven.meetup.enums.GameState;

public class LoadChunks {


	public LoadChunks(Main plugin){
		if(!plugin.getConfig().getBoolean("world_settings.load_chunks")){
			Bukkit.broadcastMessage("§cThe loading of chunks is disabled");
			return;
		}
		new BorderConfig(plugin);
		Bukkit.broadcastMessage(" ");
		Bukkit.broadcastMessage("§aThe loading of chunks has started, if it comes out it will not be able to return until it finishes");
		plugin.getGameManager().setGameState(GameState.LOADING);
		Integer ChunksSecond = plugin.getGameManager().getBorder() * 2;
		if(BorderConfig.taskStart == false){
			BorderConfig.taskStart = true;
			int fillFrequency = ChunksSecond.intValue();
	        int fillPadding = CoordXZ.chunkToBlock(13);
	        int ticks = 1;
	        int repeats = 1;
	        if(fillFrequency > 20){
	        	repeats = fillFrequency / 20;
	        }else{
	        	ticks = 20 / fillFrequency;
	        }
	        BorderConfig.setBorder("Meetup", plugin.getGameManager().getBorder(), 0.0D, 0.0D);
	        BorderConfig.fillTask = new WorldFillTask(Bukkit.getServer(), "Meetup", fillPadding, repeats, ticks, plugin);

	        Bukkit.broadcastMessage("§eStarting to load chunk from world: Meetup padding: " + fillPadding + " repeats: " + repeats + " ticks: " + ticks);
	        if (BorderConfig.fillTask.valid()){
	        	int task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, BorderConfig.fillTask, ticks, ticks);
	        	BorderConfig.fillTask.setTaskID(task);
	        }else{
	        	Bukkit.broadcastMessage("The world map generation task failed to start.");
	        }
			Bukkit.broadcastMessage(" ");
			for(Player p : Bukkit.getOnlinePlayers()) {
				p.kickPlayer("");
			}
		}
	}
	
}
