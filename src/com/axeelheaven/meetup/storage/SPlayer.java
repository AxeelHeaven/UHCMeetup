package com.axeelheaven.meetup.storage;

import java.util.UUID;

import org.bukkit.Bukkit;

import com.axeelheaven.meetup.Main;
import com.axeelheaven.meetup.api.MeetupUpdateRankEvent;

public class SPlayer {
	
	private Main plugin;
	private UUID uuid;
	
	private double damage_caused = 0.0D;
	private int kills = 0;
	private int total_kills = 0;
	private int deaths = 0;
	private int wins = 0;
	private int played = 0;
	private int golden_eaten = 0;
	private int elo = 0;
	private boolean spectatorsShow = true;
	private boolean fly = true;
	private int speedLevel = 0;
	private String elo_rank;
	
	public SPlayer(final Main plugin, final UUID uuid) {
		this.plugin = plugin;
		this.uuid = uuid;
		this.load();
	}

	private void load() {
		this.elo_rank = this.plugin.getEloManager().getRank(this.elo);
	}

	public int getSpeedLevel(){
		return this.speedLevel;
	}
	
	public void setSpeedLevel(final int level){
		this.speedLevel = level;
	}
	
	public boolean getFlying(){
		return this.fly;
	}
	public void setFlying(final boolean value){
		this.fly = value;
	}
	
	public boolean getShowSpectators(){
		return this.spectatorsShow;
	}
	
	public int getElo() {
		return this.elo;
	}
	
	public void addElo(final int elo) {
		this.elo = (this.elo + elo);
		final String newRank = this.plugin.getEloManager().getRank(this.elo);
		if(!this.getEloRank().equals(newRank)) {
			Bukkit.getPluginManager().callEvent(new MeetupUpdateRankEvent(Bukkit.getPlayer(this.uuid), this.elo_rank, newRank));
			this.elo_rank = newRank;
		}
	}
	
	public void removeElo(final int elo) {
		this.elo = (this.elo - elo);
	}
	
	public void setShowSpectators(final boolean value){
		this.spectatorsShow = value;
	}
	
	public UUID getUUID() {
		return this.uuid;
	}
	
	public int getKills() {
		return this.kills;
	}
	
	public int getTotalKills() {
		return this.total_kills;
	}
	
	public void addKills() {
		this.kills = (this.kills + 1);
		this.total_kills = (this.total_kills + 1);
	}
	
	public int getDeaths() {
		return this.deaths;
	}
	
	public void addDeaths() {
		this.deaths = (this.deaths + 1);
	}
	
	public int getWins() {
		return this.wins;
	}
	
	public void addWins() {
		this.wins = (this.wins + 1);
	}
	
	public int getPlayed() {
		return this.played;
	}
	
	public void addPlayed() {
		this.played = (this.played + 1);
	}
	
	public int getApplesEaten() {
		return this.golden_eaten;
	}
	
	public void addApplesEaten() {
		this.golden_eaten = (this.golden_eaten + 1);
	}
	
	public double getDamageCaused() {
		return this.damage_caused;
	}
	
	public void addDamageCaused(final double amount) {
		this.damage_caused = (this.damage_caused + amount);
	}

	public String getEloRank() {
		return this.elo_rank;
	}

	public void save() {
		// TODO Auto-generated method stub
		
	}

}
