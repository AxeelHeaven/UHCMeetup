package com.axeelheaven.meetup.manager;

import java.util.*;

import org.bukkit.entity.Player;

import com.axeelheaven.meetup.Main;
import com.axeelheaven.meetup.storage.SPlayer;

public class PlayerManager {
	
	private HashMap<UUID, SPlayer> data;
	private List<Player> players;
	private List<Player> spec;
	private Main plugin;
	
	public PlayerManager(final Main plugin) {
		this.data = new HashMap<UUID, SPlayer>();
		this.players = new ArrayList<>();
		this.spec = new ArrayList<>();
		this.plugin = plugin;
	}

	public List<Player> getPlayers(){
		return this.players;
	}
	
	public List<Player> getSpectators(){
		return this.spec;
	}
	
	public HashMap<UUID, SPlayer> getData(){
		return this.data;
	}
	
	public SPlayer getData(final UUID uuid){
		if(!this.data.containsKey(uuid)) {
			this.data.put(uuid, new SPlayer(plugin, uuid));
		}
		return this.data.get(uuid);
	}
	
}
