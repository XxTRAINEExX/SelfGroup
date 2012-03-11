package me.XxTRANEExX.SelfGroup;


import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import ru.tehkode.permissions.*;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import info.kanlaki101.blockprotection.utilities.BPConfigHandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SelfGroup extends JavaPlugin {
	public String pluginName = "YetiFaction"; // Need to do this because there is no way to load the PDF at initialisation time.
	public final Log logger = new Log(pluginName);
	@SuppressWarnings("unused")
	private SelfGroupListener sListener = null;
		
	public FileConfiguration config;
	public ArrayList<String> valid_groups;
	public int switch_delay; // Days a player must wait to switch again.
	
	DateFormat date_format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	@Override
	public void onDisable() {
		PluginDescriptionFile pdffile = this.getDescription();
		this.logger.info(pdffile.getName() + " version " + pdffile.getVersion() + " is disabled.");
	}
	@Override
	public void onEnable() {
		sListener = new SelfGroupListener(this);
		PluginDescriptionFile pdffile = this.getDescription();
		this.logger.info(pdffile.getName() + " version " + pdffile.getVersion() + " is enabled.");
		Util.init(this, pluginName, logger);
		
		
		// start real code
		
		CommandExecutor SelfGroupCommandExecutor = new SelfGroupCommand(this);
		getCommand("yetifaction").setExecutor(SelfGroupCommandExecutor);
    	getCommand("yf").setExecutor(SelfGroupCommandExecutor);
    	
    	// Read the config file
    	config = getConfig();
    	
    	// Read the groups from the config file
    	List<String> grps = config.getStringList("groups");
    	if ((grps == null) || (grps.size() == 0))
    	{
    		// If no list was supplied, use the yeti groups
    		grps = new ArrayList<String>();
    		grps.add("Sasquai");
    		grps.add("Sasquai_Front");
    		grps.add("Almas");
    		grps.add("Yowie_Front");
    		grps.add("Yowie");
    		config.set("groups", grps);
    		saveConfig();
    	}
    	  
    	// Store the valid groups in our working list
    	valid_groups = new ArrayList<String>();
    	for (int i = 0; i < grps.size(); i++)
    	{
    		// valid_groups.add(grps.get(i).toLowerCase()); // Removed so case sensitivity can come in to play    		
    		valid_groups.add(grps.get(i));
    	}
    	
    	switch_delay = config.getInt("switch-delay", 1);
    	if (switch_delay == 1)
    	{
    		config.set("switch-delay", 1);
    		saveConfig();
    	}
    	
	}
	
	public ArrayList<String> getGroups()
	{	
		return valid_groups;
	}

	public Boolean JoinGroup(CommandSender sender, String group)
	{
		PermissionManager pex = PermissionsEx.getPermissionManager();
		PermissionUser pUser = pex.getUser(sender.getName());
        Boolean group_valid = false;
		// group = group.toLowerCase(); //Removed this line to bring case sensitivity back in to play
        
        for (int i = 0; i < valid_groups.size(); i++)
        {
        	if (valid_groups.get(i).compareTo(group) == 0)
        	{
        		if (pUser.inGroup(group))
        		{
        			Util.reply(sender, "Sorry, you're already in the group %s", group);
        			return false;
        		}
        		group_valid = true;
        	}
        	else if (pUser.inGroup(valid_groups.get(i)))
        	{
        		Util.reply(sender, "Unable to join group %s because you're already in %s",
        				   group, valid_groups.get(i));
        		return false;
        	}
        }
        
        if (group_valid)
        {
    		String now = date_format.format(Calendar.getInstance().getTime());
    		config.set("change_times."+sender.getName(), now);
    		config.set("last_groups."+sender.getName(), group);
    		saveConfig();
    		this.logger.info("User %s has joined the %s group", sender.getName(), group);
        	pUser.addGroup(group);
        	return true;
        }
        else
        {
        	Util.reply(sender, "Sorry, %s is not a valid group name", group);
        	return false;
        }
	}
	
	public Boolean LeaveGroup(CommandSender sender, String group)
	{
		PermissionManager pex = PermissionsEx.getPermissionManager();
		PermissionUser pUser = pex.getUser(sender.getName());
		Date now = Calendar.getInstance().getTime();
		// group = group.toLowerCase(); //Removed this line to bring case sensitivity back in to play
		
		// Check the last time the user made an update and see if they've waited long enough
		String last_updated = config.getString("change_times."+sender.getName());
		//String last_group = config.getString("last_groups."+sender.getName());
		if (last_updated != null)
		{
		try
		{
			Date last = date_format.parse(last_updated);
			long last_seconds = last.getTime() / 1000; // seconds since epoch for last
			long now_seconds = now.getTime() / 1000;  // seconds since epoch for now
			// seconds needed for switch delay = 
			// delayed days x hours in a day x mins in hour x secs in min 
			long need_millis = (long)switch_delay * 24 * 60 * 60;
			if ((now_seconds - last_seconds) < need_millis)
			{
				long still_need = need_millis - (now_seconds - last_seconds);
				String msg = "You just joined group " + group + 
						" so you have to wait.  Try again in:";
				long need_days = still_need / (24 * 60 * 60);
				still_need = still_need % (24 * 60 * 60);
				long need_hours = still_need / (60 * 60);
				still_need = still_need % (60 * 60);
				long need_minutes = still_need / 60;
				still_need = still_need % 60;
				msg += need_days + "days, " + need_hours + "hrs, " 
						+ need_minutes + "min, and " + still_need + "s";
				Util.reply(sender, msg);
				return false;
			}
		}
		catch (ParseException pe) { /*ignore if it didn't parse */}
		}
        for (int i = 0; i < valid_groups.size(); i++)
        {
        	if (valid_groups.get(i).compareTo(group) == 0)
        	{
        		if (!pUser.inGroup(group))
        		{
        			Util.reply(sender, "Sorry, you're not in the group %s", group);
        			return false;
        		}
        		else
        		{
            		this.logger.info("User %s has left the %s group", sender.getName(), group);
        			pUser.removeGroup(group);
        			// Remove all friends to stop cheaters
        			BPConfigHandler.loadFriendsList();
        			BPConfigHandler.friendslist.set(pUser.getName(), null); //Delete it
        			BPConfigHandler.saveFriendsList(); //Save
        			return true;
        		}
        	}
        }
        // If we made it here, we didn't find the group they tried to remove
        Util.reply(sender, "Sorry, %s is not a valid group", group);
        return false;
	}
}