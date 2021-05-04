package com.axeelheaven.meetup.manager;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.axeelheaven.meetup.Main;
import com.axeelheaven.meetup.enums.GameState;
import com.axeelheaven.meetup.util.LocationUtil;

public class GameManager {
	
	private GameState gamestate;
	private int border_size = 150;
	private Location spawnpoint;
	private Random random;
	private int max_players;
	private int min_players;
	private ItemStack goldenhead = null;
	private Player winner;
	
	private Main plugin;
	
	public GameManager(final Main plugin) {
		this.plugin = plugin;
		this.border_size = plugin.getConfig().getInt("border_settings.border_size");
		if(plugin.getCache().getString("spawnpoint") != null) {
			this.spawnpoint = LocationUtil.getLocation(plugin.getCache().getString("spawnpoint"));
		}
		this.random = new Random();
		this.max_players = plugin.getConfig().getInt("settings.max_players");
		this.min_players = plugin.getConfig().getInt("settings.min_players");
	}
	
	public void setWinner(final Player player) {
		this.winner = player;
	}
	
	public Player getWinner() {
		return this.winner;
	}

	public int getMaxPlayers() {
		return this.max_players;
	}

	public int getMinPlayers() {
		return this.min_players;
	}
	
	public GameState getGameState() {
		return this.gamestate;
	}
	
	public void setGameState(final GameState gamestate) {
		this.gamestate = gamestate;
	}
	
	public boolean isGameState(final GameState gamestate) {
		return this.gamestate == gamestate;
	}
	
	public int getBorder() {
		return this.border_size;
	}
	
	public void setBorder(final int border_size) {
		this.border_size = border_size;
	}
	
	public Location getSpawnpoint() {
		return this.spawnpoint;
	}

	public void setLocation(final Player p){
		final Location location = this.getLocation();
		if ((location != null) && (!location.getWorld().isChunkLoaded(location.getBlock().getChunk()))) {
			location.getWorld().loadChunk(location.getBlock().getChunk());
	    }
		if((location != null)){
		    Block block = location.getWorld().getBlockAt(location.getBlockX(), (location.getBlockY() - 2), location.getBlockZ());
		    block.setType(Material.BEDROCK);
			p.teleport(location.clone().add(0, 1, 0));
			p.setFallDistance(0.0F);
		}
	}

	private Location getLocation(){
		final World world = Bukkit.getWorld("Meetup");
		if(world != null){
	        int z = random.nextInt(this.getBorder() + 1 - (this.getBorder() * -1)) + (this.getBorder() * -1);
	        int x = random.nextInt(this.getBorder() + 1 - (this.getBorder() * -1)) + (this.getBorder() * -1);
	        Location location = new Location(world, x, world.getHighestBlockYAt(x, z), z);
	        if (location.getBlock().getRelative(BlockFace.DOWN).getType() != Material.WATER &&
	                location.getBlock().getRelative(BlockFace.DOWN).getType() != Material.STATIONARY_WATER &&
	                location.getBlock().getRelative(BlockFace.DOWN).getType() != Material.LAVA &&
	                location.getBlock().getRelative(BlockFace.DOWN).getType() != Material.STATIONARY_LAVA && 
	                location.getBlock().getRelative(BlockFace.DOWN).getType() != Material.CACTUS){
	            return location.add(0, 0.5, 0);
	        }
	        return getLocation();
		}
		return null;
    }
	
	public ItemStack getGoldenHead() {
		if(this.goldenhead == null) {
			ItemStack item = new ItemStack(Material.GOLDEN_APPLE);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(this.plugin.text(this.plugin.getConfig().getString("goldenhead.name")));
			final ArrayList<String> lore_ = new ArrayList<String>();
			for(final String lore : this.plugin.getConfig().getStringList("goldenhead.lore")) {
				lore_.add(this.plugin.text(lore));
			}
			meta.setLore(lore_);
			item.setItemMeta(meta);
			this.goldenhead = item;
		}
		return this.goldenhead;
	}
	
}
