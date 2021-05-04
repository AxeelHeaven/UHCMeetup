package com.axeelheaven.meetup.api;

import com.axeelheaven.meetup.Main;

public class HMeetupAPI {
	
	private static HMeetupAPI instance;
	private Main plugin;
	
	public HMeetupAPI(final Main plugin) {
		this.plugin = plugin;
		instance = this;
	}
	
	public static HMeetupAPI getInstance() {
		return instance;
	}
	
}
