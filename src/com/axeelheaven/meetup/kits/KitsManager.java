package com.axeelheaven.meetup.kits;

import java.io.*;
import java.util.*;

import org.bukkit.configuration.file.*;
import org.bukkit.entity.Player;

import com.axeelheaven.meetup.Main;
import com.axeelheaven.meetup.util.InventoryUtil;

public class KitsManager {
	
	private Main plugin;
	private List<Kits> kits;
	private Random random;
	private List<Player> editing;
	
	public KitsManager(final Main main) {
		this.plugin = main;
		this.random = new Random();
		this.kits = new ArrayList<Kits>();
		this.editing = new ArrayList<Player>();
		this.loadKits();
		this.load();
	}
	
	public List<Player> getEditing(){
		return this.editing;
	}
	
	public Kits getRandom() {
		return this.kits.get(this.random.nextInt(this.kits.size()));
	}
	
	public List<Kits> getKits() {
		return this.kits;
	}
	
	public void loadKits(){
		final File localFile1 = new File(this.plugin.getDataFolder(), "kits");
	    if (!localFile1.exists()) {
	    	localFile1.mkdirs();
	    	for(int i = 0; i <= 9; i++) {
		        this.plugin.saveResource("kits/kit" + i + ".yml", true);
		        try {
					this.copyFolder(new File(this.plugin.getDataFolder(), "kit" + i + ".yml"), new File(localFile1, "kit" + i + ".yml"));
				} catch (IOException e) { }
	    	}
	    }
	}
	


	public void copyFolder(File source, File dest) throws IOException {
	    if (source.isDirectory()){
	    	if (!dest.exists()){
	    		dest.mkdir();
	    	}
	    	for(File file : source.listFiles()){
	    		File localFile1 = new File(source, (String)file.getName());
	    		File localFile2 = new File(dest, (String)file.getName());
		        
	    		copyFolder(localFile1, localFile2);
	    	}
	    }else{
	    	InputStream is = null;
		    OutputStream os = null;
		    try {
		        is = new FileInputStream(source);
		        os = new FileOutputStream(dest);
		        byte[] buffer = new byte[1024];
		        int length;
		        while ((length = is.read(buffer)) > 0) {
		            os.write(buffer, 0, length);
		        }
		    } finally {
		    	if(is != null){
			        is.close();
		    	}
		    	if(os != null){
			        os.close();
		    	}
		    }
	    }
	    
	}
	
	private void load() {
		final File fileGames = new File(this.plugin.getDataFolder(), "kits");
	    if ((fileGames.exists()) && (fileGames.listFiles().length > 0)){
	        for(final File file : fileGames.listFiles()){
	        	if(file.getName().contains(".yml")){
	        		final FileConfiguration config = YamlConfiguration.loadConfiguration(file);
	        		this.kits.add(new Kits(InventoryUtil.fromString(config.getString("inventory")), InventoryUtil.fromString(config.getString("armor"))));
	        	}
	        }
	    }
	}
	
}
