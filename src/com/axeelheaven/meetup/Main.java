package com.axeelheaven.meetup;

import java.io.*;

import org.bukkit.*;
import org.bukkit.configuration.file.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.*;

import com.axeelheaven.meetup.api.*;
import com.axeelheaven.meetup.commands.*;
import com.axeelheaven.meetup.enums.*;
import com.axeelheaven.meetup.kits.*;
import com.axeelheaven.meetup.listeners.*;
import com.axeelheaven.meetup.manager.*;
import com.axeelheaven.meetup.menus.*;
import com.axeelheaven.meetup.nms.*;
import com.axeelheaven.meetup.scoreboard.*;
import com.axeelheaven.meetup.task.*;
import com.axeelheaven.meetup.util.*;

public class Main extends JavaPlugin {
	
	private GameManager gameManager;
	private WorldManager worldManager;
	private NMS nms;
	private boolean placeholderapi = false;
	private PlayerManager playermanager;
	private TaskManager taskManager;
	private ScenariosManager scenariosManager;
	private KitsManager kitsManager;
	private EloManager  eloManager;
	private SocketManager socketManager;
	
	@Override
	public void onEnable() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		this.loadConfigs();
		this.loadVersions();
		this.loadListeners();
		this.loadCommands();

		this.gameManager = new GameManager(this);
		this.getGameManager().setGameState(GameState.WAITTING);
		this.worldManager = new WorldManager(this);
		this.playermanager = new PlayerManager(this);
		this.taskManager = new TaskManager(this);
		this.scenariosManager = new ScenariosManager(this);
		this.kitsManager = new KitsManager(this);
		this.eloManager = new EloManager(this);
		if(this.getConfig().getBoolean("serverSocket.enabled")) {
			this.socketManager = new SocketManager(this);
			this.socketManager.start();
		}
	}

	@Override 
	public void onDisable() {
		for(final Player player : Bukkit.getOnlinePlayers()) {
			player.kickPlayer("Restarting server.");
		}
		if(!this.getCache().getBoolean("world.is_loader")) {
			Bukkit.unloadWorld("Meetup", false);
			this.worldManager.deleteWorld(new File("Meetup"));
		}
		Bukkit.getServer().getScheduler().cancelAllTasks();
	}
	
	private void loadCommands() {
		final UptimeCommand uptimeCommand = new UptimeCommand(this);
		this.getCommand("uptime").setExecutor(uptimeCommand);
		this.getCommand("ping").setExecutor(uptimeCommand);
		this.getCommand("kits").setExecutor(new KitsCommand(this));
		
		final HMeetupCommand hmeetuoCommand = new HMeetupCommand(this);
		this.getCommand("forcestart").setExecutor(hmeetuoCommand);
		this.getCommand("elo").setExecutor(hmeetuoCommand);
	}
	
	public EloManager getEloManager() {
		return this.eloManager;
	}
	
	public KitsManager getKitsManager() {
		return this.kitsManager;
	}
	
	public ScenariosManager getScenarioManager() {
		return this.scenariosManager;
	}
	
	public TaskManager getTaskManager() {
		return this.taskManager;
	}
	
	public WorldManager getWorldManager() {
		return this.worldManager;
	}
	
	public GameManager getGameManager() {
		return this.gameManager;
	}

	private void loadListeners() {
		final PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new GameListener(this), this);
		pm.registerEvents(new LobbyListener(this), this);
		pm.registerEvents(new WorldBorderListener(this), this);
		pm.registerEvents(new ScoreboardData(this), this);
		pm.registerEvents(new ScenariosListener(this), this);

		pm.registerEvents(new ScenariosMenu(this), this);
	}
	
	
	private FileConfig fileConfig;
	
	private void loadConfigs() {
		this.getConfig().options().copyDefaults(true);
		this.saveDefaultConfig();
		this.fileConfig = FileConfig.getInstance();
		this.fileConfig.save("messages.yml");
		this.fileConfig.load("messages.yml");
		this.fileConfig.save("cache.yml");
		this.fileConfig.load("cache.yml");
		new TimeUtil(this);
		new ItemConfig(this);
		new HMeetupAPI(this);
	}
	
	public FileConfiguration getCache() {
		return this.fileConfig.get("cache.yml");
	}
	
	public FileConfiguration getLang() {
		return this.fileConfig.get("messages.yml");
	}
	
	private void loadVersions() {
		final String packageName = this.getServer().getClass().getPackage().getName();
		final String nmsString = packageName.substring(packageName.lastIndexOf('.') + 1);
	    try{
	    	Class<?> biomes = Class.forName("com.axeelheaven.meetup.nms.versions." + nmsString.toUpperCase());
	    	if (NMS.class.isAssignableFrom(biomes)) {
	    		this.nms = ((NMS)biomes.getConstructor(new Class[0]).newInstance(new Object[0]));
				Bukkit.getConsoleSender().sendMessage("§6HMeetup §fThe server version '" + nmsString + "' has been detected.");
	    	}
	    }catch (Exception e){
			Bukkit.getConsoleSender().sendMessage("§fSorry its current version: §c" + nmsString + "§f not support, download next:");
			Bukkit.getConsoleSender().sendMessage("§7  • §f1.7.10 §7(R4)§f,  1.8.8 §7(R3)");
			Bukkit.getConsoleSender().sendMessage("");
			Bukkit.getServer().getPluginManager().disablePlugins();
	    }
	    this.nms.BiomeSwap(this);
	}
	
	public NMS getNMS() {
		return this.nms;
	}
	
	public PlayerManager getPlayerManager() {
		return this.playermanager;
	}
	
	public String text(final String text) {
		return ChatColor.translateAlternateColorCodes('&', text
				.replace("<max_players>", String.valueOf(this.getGameManager().getMaxPlayers()))
				.replace("<total_players>", String.valueOf(this.getPlayerManager().getPlayers().size())));
	}
	
	public String text(final Player player, String text) {
		return this.text(text);
	}
	
}
