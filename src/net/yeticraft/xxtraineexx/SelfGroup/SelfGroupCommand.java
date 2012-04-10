package net.yeticraft.xxtraineexx.SelfGroup;



import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import java.util.ArrayList;

public class SelfGroupCommand implements CommandExecutor {
	private final SelfGroup plugin;
		
	public SelfGroupCommand(SelfGroup plugin) {
		this.plugin = plugin;
	}

	enum SubCommand {
		HELP,
		LIST,
		JOIN,
		LEAVE,
		CHECK,
		UNKNOWN;
		
		private static SubCommand toSubCommand(String str) {
			try {
				return valueOf(str.toUpperCase());
			} catch (Exception ex) {
				return UNKNOWN;
			}
		}
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				
  	if (args.length == 0) {
    		Util.replyError(sender, "Not enough arguments to command. Try /yf HELP");
			return true;
    	}
  	if (args.length > 2) {
		Util.replyError(sender, "Too many arguments to command. Try /yf HELP");
		return true;
	}
	 	
  	
    	switch (SubCommand.toSubCommand(args[0].toUpperCase())) {
	    	case HELP:
	    		Util.reply(sender, "YetiFaction help:");
	    		Util.reply(sender, " /%s help", command.getName());
	    		Util.reply(sender, "     Shows this help page");
	    		Util.reply(sender, " /%s list", command.getName());
	    		Util.reply(sender, "     Lists all available factions");
	    		Util.reply(sender, " /%s join <group>", command.getName());
	    		Util.reply(sender, "     Joins the given faction");
	    		Util.reply(sender, " /%s check", command.getName());
	    		Util.reply(sender, "     checks your current faction status");
	    		Util.reply(sender, " /%s leave <group>", command.getName());
	    		Util.reply(sender, "     leaves the given faction");
	    		break;
	    	case LIST:
	    		ArrayList<String> groups = plugin.getGroups();
	    		Util.reply(sender,  "Available groups: ");
	    		for (int i = 0; i < groups.size(); i++)
	    		{
	    			Util.reply(sender, "\t%s",groups.get(i));	    			
	    		}
	    		break;
	    	case JOIN:
	    		if (args.length == 1)
	    		{
	    			Util.reply(sender,  "Please specify a group to join.");
	    			return true;
	    		}
	    		if (plugin.JoinGroup(sender, args[1]))
	    		{
	    			Util.reply(sender,  "You have successfully joined group %s", args[1]);
	    		}
	    		break;
	    	case LEAVE:
	    		if (args.length == 1)
	    		{
	    			Util.reply(sender,  "Please specify a group to leave.");
	    			return true;
	    		}
	    		if (plugin.LeaveGroup(sender, args[1]))
	    		{	
	    			Util.reply(sender, "You have successfully left the group %s", args[1]);
	    			Util.reply(sender, "Your friends list has been cleared.");
	    		}
	    		break;
	    	case CHECK:
				plugin.checkGroup(sender);
				break;
	    	case UNKNOWN:
				Util.replyError(sender, "Unknown command. Use /yf help to list available commands.");
    	}
    	
		return true;
	}
}


