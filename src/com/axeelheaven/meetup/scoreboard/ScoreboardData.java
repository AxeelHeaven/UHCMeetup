package com.axeelheaven.meetup.scoreboard;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.axeelheaven.meetup.Main;
import com.axeelheaven.meetup.enums.GameState;
import com.axeelheaven.meetup.storage.SPlayer;
import com.axeelheaven.meetup.task.BorderTask;
import com.axeelheaven.meetup.util.TimeUtil;

public class ScoreboardData implements Listener {
	
	private Main plugin;
	private HashMap<Player, Integer> data;
	private HashMap<String, ArrayList<String>> scoreboards;
	private String title;
	
	public ScoreboardData(final Main plugin) {
		this.plugin = plugin;
		this.data = new HashMap<Player, Integer>();
		this.scoreboards = new HashMap<String, ArrayList<String>>();
		this.loadScoreboard();
		title = plugin.getLang().getString("scoreboard.title");
		if(plugin.getLang().getBoolean("scoreboard.animated_title")) {
			this.animatedTitleBoard();
		}
	}
	
	private void setScoreboard(final Player player) {
		final ScoreboardUtil sb = new ScoreboardUtil(text(title, player), player.getName());
		final ArrayList<String> lines = new ArrayList<String>();
		final SPlayer data = this.plugin.getPlayerManager().getData(player.getUniqueId());
		Integer task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				sb.setName(text(title, player));
				lines.clear();
				
				if(plugin.getGameManager().isGameState(GameState.WAITTING)) {
					for(final String var : scoreboards.get("waitting_lines")) {
						if(var.contains("<starting_mode>")) {
							if(plugin.getTaskManager().getTeleportingTask().isRunning()) {
								lines.add(text(plugin.getLang().getString("scoreboard.waitting_teleporting").replace("<seconds>", TimeUtil.getInstance().getSeconds(plugin.getTaskManager().getTeleportingTask().getSeconds(), false)), player));
							}else {
								lines.add(text(plugin.getLang().getString("scoreboard.waitting_players"), player));
							}
						} else if(var.contains("<scenarios>")) {
							int i = 0;
							for(final String scenario : plugin.getScenarioManager().getScenarios().keySet()) {
								if((scoreboards.get("waitting_lines").size() + i) >= 15) {
									lines.add(text(plugin.getLang().getString("scoreboard.scenarios_more").replace("<amount>", String.valueOf(plugin.getScenarioManager().getScenarios().size() - i)), player));
									break;
								} else {
									lines.add(text(plugin.getLang().getString("scoreboard.scenarios_status").replace("<scenario_name>", scenario).replace("<scenario_votes>", String.valueOf(plugin.getScenarioManager().getScenarios().get(scenario))), player));
								}

								i++;
							}
						} else {
							lines.add(text(var, player));
						}
					}
				} else if(plugin.getGameManager().isGameState(GameState.STARTING)) {
					for(final String var  : scoreboards.get("starting_lines")) {
						lines.add(text(var, player).replace("<seconds>", TimeUtil.getInstance().getSeconds(plugin.getTaskManager().getStartingTask().getSeconds(), false)));
					}
				} else if(plugin.getGameManager().isGameState(GameState.INGAME)) {
					for(final String var  : scoreboards.get("game_lines")) {
						if(var.contains("<noclean>")) {
							if(plugin.getScenarioManager().getSelectedScenario().equalsIgnoreCase("NoClean") && plugin.getScenarioManager().getNoClean().containsKey((player)) && System.currentTimeMillis() < plugin.getScenarioManager().getNoClean().get(player)) {
								final Long time = Math.max(plugin.getScenarioManager().getNoClean().get(player) - System.currentTimeMillis(), 0L);
								lines.add(text(plugin.getLang().getString("scoreboard.noclean_status").replace("<seconds>", formatMilisecondsToSeconds(time)), player));
							}
						} else {
							lines.add(text(var, player).replace("<elo_rank>", data.getEloRank()).replace("<elo>", String.valueOf(data.getElo())).replace("<kills>", String.valueOf(data.getKills())).replace("<border_time>", getBorderFormat()).replace("<spectators>", String.valueOf(plugin.getPlayerManager().getSpectators().size())).replace("<date>", getDate()).replace("<border>", String.valueOf(plugin.getGameManager().getBorder())).replace("<time>", TimeUtil.getInstance().getSeconds(plugin.getTaskManager().getGameTask().getSeconds(), false)));
						}
					}
				}else if(plugin.getGameManager().isGameState(GameState.END)) {
					final Player winner = plugin.getGameManager().getWinner();
					for(final String var  : scoreboards.get("end_lines")) {
						lines.add(text(var, player).replace("<kills>", String.valueOf(plugin.getPlayerManager().getData(winner.getUniqueId()).getKills())).replace("<winner>", winner.getName()).replace("<restarting>", String.valueOf(plugin.getTaskManager().getRestartingTask().getSeconds())));
					}
				}
				sb.lines(lines);
			}
		}, 3L, 3L);
		this.data.put(player, task);
		sb.build(player);
	}
	
	public String formatMilisecondsToSeconds(final Long time) {
        final float seconds = (time + 0.0f) / 1000.0f;
        final String string = String.format("%1$.1f", seconds);
        return string;
    }
	
	public String getBorderFormat() {
		final BorderTask borderTask = this.plugin.getTaskManager().getBorderTask();
		if(borderTask.isRunnable()) {
			return this.plugin.text(this.plugin.getLang().getString("scoreboard.border_status").replace("<border_time>", this.convert(borderTask.getSeconds())));
		}
		return "";
	}
	
	private String convert(final int paramInt){
		final int i = paramInt;
		final int j = i / 3600;
	    int k = i / 60;
	    final int m = i;
	    String str = null;
	    if (paramInt >= 3600) {
	    	str = j + "h";
	    }
	    if ((paramInt < 3600) && (paramInt >= 60)){
	    	k++;
	    	str = k + "m";
	    }
	    if (paramInt < 60) {
	    	str = m + "s";
	    }
	    return str;
	}
	
	private String getDate(){
		final DateFormat date = new SimpleDateFormat("dd/MM/yyyy");
		final Calendar cal = Calendar.getInstance();
		return date.format(cal.getTime());
	}
	
	@EventHandler
	public void onJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		if(this.data.containsKey(player)) {
			Bukkit.getServer().getScheduler().cancelTask(this.data.get(player).intValue());
		}
		this.setScoreboard(player);
	}
	
	@EventHandler
	public void onLeave(final PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		if(this.data.containsKey(player)) {
			Bukkit.getServer().getScheduler().cancelTask(this.data.get(player).intValue());
		}
	}
	
	@EventHandler
	public void onKick(final PlayerKickEvent event) {
		final Player player = event.getPlayer();
		if(this.data.containsKey(player)) {
			Bukkit.getServer().getScheduler().cancelTask(this.data.get(player).intValue());
		}
	}
	
	private void loadScoreboard() {
		this.scoreboards.put("title_frames", new ArrayList<>(this.plugin.getLang().getStringList("scoreboard.title_frames")));
		this.scoreboards.put("waitting_lines", new ArrayList<>(this.plugin.getLang().getStringList("scoreboard.waitting_lines")));
		this.scoreboards.put("starting_lines", new ArrayList<>(this.plugin.getLang().getStringList("scoreboard.starting_lines")));
		this.scoreboards.put("game_lines", new ArrayList<>(this.plugin.getLang().getStringList("scoreboard.game_lines")));
		this.scoreboards.put("end_lines", new ArrayList<>(this.plugin.getLang().getStringList("scoreboard.end_lines")));
	}
	
	private void animatedTitleBoard(){
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable(){
			int i = 0;
			public void run(){
				setTitle(((String)plugin.text(scoreboards.get("title_frames").get(i))));
				if (this.i == scoreboards.get("title_frames").size() - 1) {
					this.i = 0;
				}else{
					this.i ++;
				}
			}
		}, 3L, 3L);
	}
	
	public void setTitle(final String title) {
		this.title = title;
	}
	
	public String text(final String text, final Player player) {
		return this.plugin.text(player, ChatColor.translateAlternateColorCodes('&', text));
	}
	
	public void setTag(final Player p, final String team) {
		final String team1Prefix = ChatColor.translateAlternateColorCodes('&', "&a");
		final String team2Prefix = ChatColor.translateAlternateColorCodes('&', "&c");
	    for(final Player p2 : Bukkit.getOnlinePlayers()) {
	    	final Scoreboard board = p2.getScoreboard();
	    	Team color = board.getTeam(team);
	    	if (color == null) {
	    		color = board.registerNewTeam(team);
	    	}
	    	if (team.equals("team1")) {
	    		color.setPrefix(team1Prefix);
	    	} else if (team.equals("team2")) {
	    		color.setPrefix(team2Prefix);
	    	} 
	    	color.addEntry(p.getName());
	    }
	}
	
}
