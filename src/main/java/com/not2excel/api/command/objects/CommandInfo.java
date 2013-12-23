package com.not2excel.api.command.objects;

import com.not2excel.api.command.CommandHandler;
import org.bukkit.command.CommandSender;

/**
 * @author Richmond Steele
 * @since 12/17/13
 * All rights Reserved
 * Please read included LICENSE file
 */
public class CommandInfo
{
    private final RegisteredCommand registeredCommand;
    private final Parent            parent;
    private final CommandHandler    commandHandler;
    private final CommandSender     sender;
    private final String            command;
    private final String[]          args;
    private final String            usage;
    private final String            permission;

    public CommandInfo(RegisteredCommand registeredCommand, Parent parent, CommandHandler commandHandler,
                       CommandSender sender, String command, String[] args, String usage, String permission)
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

    public Parent getParent()
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

    public String getCommand()
    {
        return command;
    }

    public String[] getArgs()
    {
        return args;
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
}
