package com.not2excel.commandAPI.objects;


import com.not2excel.commandAPI.CommandHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Richmond Steele
 * @since 12/17/13
 * All rights Reserved
 * Please read included LICENSE file
 */
public class CommandInfo
{
    private final RegisteredCommand registeredCommand;
    private final CommandHandler    commandHandler;
    private final CommandSender     sender;
    private final String            command;
    private final String[]          args;

    public CommandInfo(RegisteredCommand registeredCommand, CommandHandler commandHandler, CommandSender sender,
                       String command, String[] args)
    {
        this.registeredCommand = registeredCommand;
        this.commandHandler = commandHandler;
        this.sender = sender;
        this.command = command;
        this.args = args;
    }

    public RegisteredCommand getRegisteredCommand()
    {
        return registeredCommand;
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
        return commandHandler.permission();
    }

    public String noPermission()
    {
        return commandHandler.noPermission();
    }

    public String getUsage()
    {
        return commandHandler.usage();
    }

    public String getDescription()
    {
        return commandHandler.description();
    }

    public Player getPlayer()
    {
        return isPlayer() ? (Player) sender : null;
    }

    public boolean isPlayer()
    {
        return sender instanceof Player;
    }
}
