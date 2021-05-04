package com.axeelheaven.meetup.scoreboard;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardUtil {
	
    private Scoreboard scoreboard;
    private Objective objective;
    private boolean reset;
    
    public String color(String s) {
        return s.replaceAll("&", "§");
    }
    
    public ScoreboardUtil(String displayName, String object) {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        (this.objective = this.scoreboard.registerNewObjective(object, "dummy")).setDisplayName(displayName);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        
        Objective hName = this.scoreboard.registerNewObjective("hname", "health");
        hName.setDisplaySlot(DisplaySlot.BELOW_NAME);
        hName.setDisplayName(ChatColor.RED + "❤");
        
        Objective hTab = this.scoreboard.registerNewObjective("htab", "health");
        hTab.setDisplaySlot(DisplaySlot.PLAYER_LIST);
    }
    
    public void setName(String substring) {
        if (substring.length() > 32) {
            substring = substring.substring(0, 32);
        }
        this.objective.setDisplayName(color(substring));
    }
    
    public void lines(Integer n, String substring) {
        Team team = this.scoreboard.getTeam("TEAM_" + n);
        if (substring.length() > 32) {
            substring = substring.substring(0, 32);
        }
        String[] splitStringLine = this.splitStringLine(substring);
        if (team == null) {
            Team registerNewTeam = this.scoreboard.registerNewTeam("TEAM_" + n);
            registerNewTeam.addEntry(this.getEntry(n));
            this.setPrefix(registerNewTeam, splitStringLine[0]);
            this.setSuffix(registerNewTeam, splitStringLine[1]);
            this.objective.getScore(this.getEntry(n)).setScore((int)n);
        }
        else {
            this.setPrefix(team, splitStringLine[0]);
            this.setSuffix(team, splitStringLine[1]);
        }
    }
    
    public void setPrefix(Team team, String prefix) {
        if (prefix.length() > 16) {
            team.setPrefix(prefix.substring(0, 16));
            return;
        }
        team.setPrefix(prefix);
    }
    
    public void setSuffix(Team team, String s) {
        if (s.length() > 16) {
            team.setSuffix(this.maxChars(16, s));
        }
        else {
            team.setSuffix(s.substring(0, s.length()));
        }
    }
    
    public String maxChars(int n, String s) {
        if (ChatColor.translateAlternateColorCodes('&', s).length() > n) {
            return s.substring(0, n);
        }
        return ChatColor.translateAlternateColorCodes('&', s);
    }
    
    public String getEntry(Integer n) {
        if (n == 0) {
            return "§0";
        }
        if (n == 1) {
            return "§1";
        }
        if (n == 2) {
            return "§2";
        }
        if (n == 3) {
            return "§3";
        }
        if (n == 4) {
            return "§4";
        }
        if (n == 5) {
            return "§5";
        }
        if (n == 6) {
            return "§6";
        }
        if (n == 7) {
            return "§7";
        }
        if (n == 8) {
            return "§8";
        }
        if (n == 9) {
            return "§9";
        }
        if (n == 10) {
            return "§a";
        }
        if (n == 11) {
            return "§b";
        }
        if (n == 12) {
            return "§c";
        }
        if (n == 13) {
            return "§d";
        }
        if (n == 14) {
            return "§e";
        }
        if (n == 15) {
            return "§f";
        }
        return "";
    }
    
    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }
    
    public void setReset(boolean reset) {
        this.reset = reset;
    }
    
    public boolean isReset() {
        return this.reset;
    }
    
    public void build(Player player) {
        player.setScoreboard(this.scoreboard);
    }
    
    public void reset(Integer value){
    	Team team = this.getScoreboard().getTeam("TEAM_" + value);
    	if(team != null){
    		this.getScoreboard().getTeam("TEAM_" + value).unregister();
    		this.getScoreboard().resetScores(this.getEntry(value));
    	}
    }
    
    private String[] splitStringLine(String s) {
        StringBuilder sb = new StringBuilder(s.substring(0, (s.length() >= 16) ? 16 : s.length()));
        StringBuilder sb2 = new StringBuilder((s.length() > 16) ? s.substring(16) : "");
        if (sb.toString().length() > 1 && sb.charAt(sb.length() - 1) == '§') {
            sb.deleteCharAt(sb.length() - 1);
            sb2.insert(0, '§');
        }
        String string = "";
        for (int i = 0; i < sb.toString().length(); ++i) {
            if (sb.toString().charAt(i) == '§' && i < sb.toString().length() - 1) {
                string = String.valueOf(string) + "§" + sb.toString().charAt(i + 1);
            }
        }
        String string2 = new StringBuilder().append((Object)sb2).toString();
        if (sb.length() > 14) {
            string2 = (string.isEmpty() ? ("§" + string2) : (String.valueOf(String.valueOf(string)) + string2));
        }
        return new String[] { (sb.toString().length() > 16) ? sb.toString().substring(0, 16) : sb.toString(), (string2.toString().length() > 16) ? string2.toString().substring(0, 16) : string2.toString() };
    }
	
    public void lines(List<String> list) {
    	while (list.size() > 15) {
			list.remove(list.size() - 1);
		}
		int slot = list.size();
		if (slot < 15) {
			for (int i = slot + 1; i <= 15; i++) {
				this.reset(i);
			}
		}
		for (String line : list){
			lines(slot, line);
			slot--;
		}
	}
}