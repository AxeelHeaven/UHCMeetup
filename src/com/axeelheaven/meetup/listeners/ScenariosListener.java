package com.axeelheaven.meetup.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.axeelheaven.meetup.Main;

public class ScenariosListener implements Listener {

	private Main plugin;
	
	public ScenariosListener(final Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void EntityDamageByEntity(final EntityDamageByEntityEvent event) {
		if(!(event.getEntity() instanceof Player)) {
			return;
		}
		if(event.getDamager() != null && !(event.getDamager() instanceof Player)) {
			return;
		}
		final Player player = (Player) event.getEntity();
		final Player damager = (Player) event.getDamager();
		if(this.plugin.getScenarioManager().getNoClean().containsKey(player)) {
			event.setCancelled(true);
			damager.sendMessage(this.plugin.text(damager, this.plugin.getLang().getString("noclean_protection")));
		}
		if(this.plugin.getScenarioManager().getNoClean().containsKey(damager)) {
			this.plugin.getScenarioManager().getNoClean().remove(damager);
			damager.sendMessage(this.plugin.text(damager, this.plugin.getLang().getString("pvpremoved_noclean")));
		}
	}
	
	@EventHandler
	public void EntityDamage(final EntityDamageEvent event) {
		if(!(event.getEntity() instanceof Player)) {
			return;
		}
		if(this.plugin.getScenarioManager().getSelectedScenario().equals("NoFall")) {
			if(event.getCause().equals(DamageCause.FALL)) {
				event.setCancelled(true);
			}
		}
		if(this.plugin.getScenarioManager().getSelectedScenario().equals("Fireless")) {
			if(event.getCause().equals(DamageCause.FIRE) || event.getCause().equals(DamageCause.FIRE_TICK) ||
					event.getCause().equals(DamageCause.LAVA)) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void PlayerDeath(final PlayerDeathEvent event) {
		if(this.plugin.getScenarioManager().getSelectedScenario().equals("NoClean")) {
			final Player player = event.getEntity();
			if(event.getEntity().getKiller() != null && event.getEntity().getKiller() instanceof Player) {
				final Player killer = player.getKiller();
				this.plugin.getScenarioManager().getNoClean().put(killer, (System.currentTimeMillis() + 20 * 1000));
				killer.sendMessage(this.plugin.text(killer, this.plugin.getLang().getString("noclean_entered")));
			}
		}
		if(this.plugin.getScenarioManager().getSelectedScenario().equals("KillSwitch")) {
			final Player player = event.getEntity();
			event.getDrops().clear();
			if(event.getEntity().getKiller() != null && event.getEntity().getKiller() instanceof Player) {
				final Player killer = player.getKiller();
				player.getInventory().setArmorContents(killer.getInventory().getArmorContents());
				player.getInventory().setContents(killer.getInventory().getContents());
				player.sendMessage(this.plugin.text(player, this.plugin.getLang().getString("death_killswitch")));
			}
		}
		if(this.plugin.getScenarioManager().getSelectedScenario().equals("Rodless")) {
			for(final ItemStack drops : event.getDrops()){
				if(drops.getType().equals(Material.FISHING_ROD)){
					event.getDrops().remove(drops);
				}
			}
		}
		if(this.plugin.getScenarioManager().getSelectedScenario().equals("TimeBomb")) {
			final Player victim = event.getEntity();
			final Location where = victim.getLocation();
			
            event.getDrops().clear();
            where.getBlock().setType(Material.CHEST);
            final Chest chest = (Chest)where.getBlock().getState();
            where.add(1.0, 0.0, 0.0).getBlock().setType(Material.CHEST);
            where.add(0.0, 1.0, 0.0).getBlock().setType(Material.AIR);
            where.add(1.0, 1.0, 0.0).getBlock().setType(Material.AIR);
            chest.getInventory().addItem(this.plugin.getGameManager().getGoldenHead());
            for (final ItemStack itemStack : victim.getInventory().getArmorContents()) {
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    chest.getInventory().addItem(itemStack );
                }
            }
            for (final ItemStack itemStack : victim.getInventory().getContents()) {
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    chest.getInventory().addItem(itemStack );
                }
            }
            new BukkitRunnable() {
                public void run() {
                    where.getBlock().setType(Material.AIR);
                    Bukkit.broadcastMessage(plugin.text(plugin.getLang().getString("timebomb").replace("<name>", victim.getName())));
                    where.getWorld().createExplosion(where.getBlockX() + 0.5, (double)(where.getBlockY() + 1), where.getBlockZ() + 0.5, 2.0f, false, true);
                    where.getWorld().strikeLightning(where);
                    final int raduis = 2;
                    final Block middle = where.getBlock();
                    for (int x = raduis; x >= -raduis; --x) {
                        for (int y = raduis; y >= -raduis; --y) {
                            for (int z = raduis; z >= -raduis; --z) {
                                if (middle.getRelative(x, y, z).getType().equals((Object)Material.CHEST)) {
                                    middle.getRelative(x, y, z).setType(Material.AIR);
                                }
                            }
                        }
                    }
                }
            }.runTaskLater((Plugin)this.plugin, 600L);
        }
	}
	
	@EventHandler
	public void Click(PlayerInteractEvent event) {
		if(this.plugin.getScenarioManager().getSelectedScenario().equals("Rodless") && event.getItem().getType().equals(Material.FISHING_ROD)){
			event.getPlayer().getInventory().remove(new ItemStack(Material.FISHING_ROD));
			event.setCancelled(true);
		}
	}


	@EventHandler 
	public void craft(PrepareItemCraftEvent event){
		if(this.plugin.getScenarioManager().getSelectedScenario().equals("Rodless") && event.getInventory().getResult().getType().equals(Material.FISHING_ROD)){
			event.getInventory().setResult(new ItemStack(Material.AIR));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void PlayerItemConsume(final PlayerItemConsumeEvent event) {
		final Player player = event.getPlayer();
		if (event.getItem().getType().equals(Material.GOLDEN_APPLE)) {
			if (this.plugin.getScenarioManager().getSelectedScenario().equals("Absorptionless")) {
				new BukkitRunnable() {
					@Override
					public void run() {
						if (player.hasPotionEffect(PotionEffectType.ABSORPTION)) {
							player.removePotionEffect(PotionEffectType.ABSORPTION);
						}
					}
				}.runTaskLater(this.plugin, 20L);
			}
		}
	}

}
