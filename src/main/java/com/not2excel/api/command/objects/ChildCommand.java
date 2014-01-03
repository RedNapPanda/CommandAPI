package com.not2excel.api.command.objects;

import com.not2excel.api.command.CommandHandler;
import com.not2excel.api.command.handler.ErrorHandler;
import com.not2excel.api.command.handler.Handler;
import org.bukkit.command.CommandSender;

/**
 * @author Richmond Steele
 * @since 12/17/13
 * All rights Reserved
 * Please read included LICENSE file
 */
public class ChildCommand extends ParentCommand
{
    private final CommandHandler commandHandler;
    protected String  command     = "";
    protected String  usage       = "";
    protected String  description = "";
    protected String  permission  = "";
    private   Handler handler     = null;

    public ChildCommand(CommandHandler commandHandler)
    {
        this.commandHandler = commandHandler;
    }

    public CommandHandler getCommandHandler()
    {
        return commandHandler;
    }

    public Handler getHandler()
    {
        if (handler == null)
        {
            return new ErrorHandler();
        }
        return handler;
    }

    public void setHandler(Handler handler)
    {
        this.handler = handler;
    }

    public boolean checkPermission(CommandSender sender)
    {
        if(commandHandler == null)
        {
            return true;
        }
        return commandHandler.permission().equals("") || sender.hasPermission(commandHandler.permission());
    }

    public String getDescription()
    {
        if(commandHandler == null)
        {
            return description;
        }
        else
        {
            return commandHandler.description();
        }
    }

    public String getUsage()
    {
        if(commandHandler == null)
        {
            return usage;
        }
        else
        {
            return commandHandler.usage();
        }
    }

    public String getPermission()
    {
        if(commandHandler == null)
        {
            return permission;
        }
        else
        {
            return commandHandler.permission();
        }
    }

    public String getCommand()
    {
        if(commandHandler == null)
        {
            return command;
        }
        else
        {
        	String[] list = commandHandler.command().split("\\.");
            return list[list.length - 1 <= 0 ? 0 : list.length - 1];
        }
    }
}
