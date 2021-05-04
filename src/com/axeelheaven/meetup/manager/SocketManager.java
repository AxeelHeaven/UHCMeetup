package com.axeelheaven.meetup.manager;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.axeelheaven.meetup.Main;

public class SocketManager {
	
	private Socket socket = null;
	private Main plugin;
	private GameManager manager;
	private String message = "";
	private String name;
	private int port;
	private String host;
	
	public SocketManager(final Main main) {
		this.plugin = main;
		this.manager = main.getGameManager();
		this.port = main.getConfig().getInt("serverSocket.received_information_port");
		this.name = main.getConfig().getString("serverSocket.server_name");
		this.host = main.getConfig().getString("serverSocket.received_information_host");
	}
	
	public void start() {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable() {
			@Override
			public void run() {
				final String msg = manager.getGameState().toString() + ";" + plugin.getPlayerManager().getPlayers().size();
				if(!message.equals(msg)) {
					message = msg;
					update(message);
				}
			}
		}, 20L, 20L);
	}
	
	private void update(final String msg){
		try {
			socket = new Socket(this.host, this.port);
			final ObjectOutputStream oos = new ObjectOutputStream(this.socket.getOutputStream());
	        oos.writeObject(this.name + " " + msg);
	        oos.close();
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSocketManager: " + msg.toString()));
		} catch (IOException e) {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cSocketManager: " + e.getMessage().toString()));
		}
    }
	
	public Socket getServerSocket() {
		return this.socket;
	}
	
}
