package com.not2excel.commandAPI.objects;


import com.not2excel.commandAPI.CommandHandler;
import com.not2excel.commandAPI.handler.ErrorHandler;
import com.not2excel.commandAPI.handler.Handler;
import org.bukkit.command.CommandSender;

/**
 * @author Richmond Steele
 * @since 12/17/13
 * All rights Reserved
 * Please read included LICENSE file
 */
public class ChildCommand extends Parent
{
    private final CommandHandler commandHandler;
    private Handler handler = null;

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
        return commandHandler.permission().equals("") || sender.hasPermission(commandHandler.permission());
    }
}
