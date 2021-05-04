package com.axeelheaven.meetup.nms;

import org.bukkit.entity.Player;

import com.axeelheaven.meetup.Main;

public abstract interface NMS {
	
	public abstract void BiomeSwap(final Main main);
	
	public abstract void moutPlayer(final Player player);
	
	public abstract void sendTitle(final Player player, final String title, final String subtitle, final int fadeIn, final int stay, final int fadeOut);
	
	public abstract void sendActionBar(final Player player, final String meessage);
	
}
