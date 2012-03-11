package net.yeticraft.xxtraineexx.SelfGroup;

import org.bukkit.event.Listener;
import org.bukkit.event.server.*;

public class SelfGroupListener implements Listener {
	public static SelfGroup plugin;
	
	public SelfGroupListener(SelfGroup instance) {
		instance.getServer().getPluginManager().registerEvents(this, instance);
		plugin = instance;
	}
	
	
	public void onPluginEnable (PluginEnableEvent event) {
		plugin.logger.info("Plugin detected: %s", event.getPlugin().toString());
	}
	
}
