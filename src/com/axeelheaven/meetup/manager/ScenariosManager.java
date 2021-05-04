package com.axeelheaven.meetup.manager;

import java.util.*;

import org.bukkit.entity.Player;

import com.axeelheaven.meetup.Main;
import com.axeelheaven.meetup.util.ValueComparator;

public class ScenariosManager {
	
	private HashMap<String, Integer> scenarios;
	private String selected_scenario = "";
	private HashMap<Player, Long> noClean;
	
	public ScenariosManager(final Main plugin) {
		this.scenarios = new HashMap<String, Integer>();
		this.noClean = new HashMap<Player, Long>();
		scenarios.put("NoClean", 0);
		scenarios.put("TimeBomb", 0);
		scenarios.put("Rodless", 0); //YA
		scenarios.put("KillSwitch", 0); //YA
		scenarios.put("Fireless", 0); //YA
		scenarios.put("Absorptionless", 0); //YA
		scenarios.put("NoFall", 0); //YA
	}
	
	public HashMap<Player, Long> getNoClean() {
		return this.noClean;
	}
	
	public void setSelectedScenario() {
		this.selected_scenario = this.getScenarioMostVoted();
	}
	
	public HashMap<String, Integer> getScenarios() {
		return this.scenarios;
	}
	
	public String getSelectedScenario() {
		return this.selected_scenario;
	}
	
	public int getVotes(final String name) {
		return this.scenarios.get(name).intValue();
	}

	public void addVote(final String name) {
		this.scenarios.put(name, Integer.valueOf(this.getVotes(name) + 1));
	}
	
	private String getScenarioMostVoted(){
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		ValueComparator bvc = new ValueComparator(map);
	    TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(bvc);
	    for(final String a : this.scenarios.keySet()) {
	    	if(this.getVotes(a) > 0) {
		    	map.put(a, this.scenarios.get(a));
	    	}
	    }
	    sorted_map.putAll(map);
	    for (final String s : sorted_map.keySet()){
	    	return s;
	    }
		return "No Gamemode";
	}
	
}
