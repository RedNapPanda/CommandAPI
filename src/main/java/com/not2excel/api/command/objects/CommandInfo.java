package com.not2excel.api.command.objects;

import com.not2excel.api.command.CommandHandler;
import com.not2excel.api.command.handler.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Richmond Steele
 * @since 12/17/13
 * All rights Reserved
 * Please read included LICENSE file
 */
public class CommandInfo
{
    private final RegisteredCommand registeredCommand;
    private final ParentCommand     parent;
    private final CommandHandler    commandHandler;
    private final CommandSender     sender;
    private final String            command;
    private final String            usage;
    private final String            permission;
    private       List<String>      args;

    public CommandInfo(RegisteredCommand registeredCommand, ParentCommand parent, CommandHandler commandHandler,
                       CommandSender sender, String command, List<String> args, String usage, String permission)
    {
        this.registeredCommand = registeredCommand;
        this.parent = parent;
        this.commandHandler = commandHandler;
        this.sender = sender;
        this.command = command;
        this.args = args;
        this.usage = usage;
        this.permission = permission;
    }

    public RegisteredCommand getRegisteredCommand()
    {
        return registeredCommand;
    }

    public ParentCommand getParent()
    {
        return parent;
    }

    public CommandHandler getCommandHandler()
    {
        return commandHandler;
    }

    public CommandSender getSender()
    {
        return sender;
    }
    
    public Player getPlayer()
    {
    	if(isPlayer())
    	{
    		return (Player) sender;
    	}
    	return null;
    }
    
    public boolean isPlayer()
    {
    	return sender instanceof Player;
    }

    public String getCommand()
    {
        return command;
    }

    public List<String> getArgs()
    {
        return args;
    }

    public void setArgs(List<String> args)
    {
        this.args = args;
    }

    public int getArgsLength()
    {
        return args.size();
    }

    public String getPermission()
    {
        if (commandHandler == null)
        {
            return permission;
        }
        return commandHandler.permission();
    }

    public String noPermission()
    {
        if (commandHandler == null)
        {
            return "";
        }
        return commandHandler.noPermission();
    }

    public String getUsage()
    {
        if (commandHandler == null)
        {
            return usage;
        }
        return commandHandler.usage();
    }

    public String getDescription()
    {
        if (commandHandler == null)
        {
            return "";
        }
        return commandHandler.description();
    }

    public String getIndex(int index) throws CommandException
    {
        if(index > args.size())
        {
            throw new CommandException(sender, "<red>Invalid index number");
        }
        return args.get(index);
    }

    public String getIndex(int index, String defaultString)
    {
        if(index > args.size())
        {
            return defaultString;
        }
        return args.get(index);
    }

    public int getInt(int index) throws CommandException
    {
        if(index > args.size())
        {
            throw new CommandException(sender, "<red>Invalid index number");
        }
        int returnValue;
        try
        {
            returnValue = Integer.parseInt(args.get(index));
        }
        catch(NumberFormatException e)
        {
            throw new CommandException(sender, "<red>Index <gold>%d<red> is not an Integer", index);
        }
        return returnValue;
    }

    public int getInt(int index, int defaultValue)
    {
        if(index > args.size())
        {
            return defaultValue;
        }
        try
        {
            return Integer.parseInt(args.get(index));
        }
        catch(NumberFormatException e)
        {
            return defaultValue;
        }
    }

    public double getDouble(int index) throws CommandException
    {
        if(index > args.size())
        {
            throw new CommandException(sender, "<red>Invalid index number");
        }
        double returnValue;
        try
        {
            returnValue = Double.parseDouble(args.get(index));
        }
        catch(NumberFormatException e)
        {
            throw new CommandException(sender, "<red>Index <gold>%d<red> is not an Double", index);
        }
        return returnValue;
    }

    public double getDouble(int index, double defaultValue)
    {
        if(index > args.size())
        {
            return defaultValue;
        }
        try
        {
            return Double.parseDouble(args.get(index));
        }
        catch(NumberFormatException e)
        {
            return defaultValue;
        }
    }

    public String joinArgs(int index) throws CommandException
    {
        if(index > args.size())
        {
            throw new CommandException(sender, "<red>Invalid index number");
        }
        StringBuilder builder = new StringBuilder();
        for(int i = index; i < args.size(); ++i)
        {
            String arg = args.get(i);
            if(i != index)
            {
                builder.append(" ");
            }
            builder.append(arg);
        }
        return builder.toString();
    }
}
