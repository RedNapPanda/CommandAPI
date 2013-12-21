package com.not2excel.commandAPI.objects;

import com.not2excel.commandAPI.CommandHandler;
import com.not2excel.commandAPI.handler.CommandException;
import com.not2excel.commandAPI.handler.Handler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map.Entry;

/**
 * @author Richmond Steele
 * @since 12/17/13
 * All rights Reserved
 * Please read included LICENSE file
 */
public class RegisteredCommand extends Parent implements CommandExecutor, Handler
{
    private final QueuedCommand queuedCommand;

    public RegisteredCommand(QueuedCommand queuedCommand)
    {
        this.queuedCommand = queuedCommand;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings)
    {
        processCommand(sender, command, s, strings);
        return true;
    }

    private void processCommand(CommandSender sender, Command command, String s, String[] strings)
    {
        if (strings.length == 0)
        {
            if(queuedCommand != null)
            {
                try
                {
                    CommandHandler commandHandler = getMethod().getAnnotation(CommandHandler.class);
                    handleCommand(new CommandInfo(this, commandHandler, sender, s, strings));
                }
                catch (CommandException e)
                {
                    sender.sendMessage("§cFailed to handle command properly.");
                }
            }
            else
            {
                displayDefaultUsage(sender, s);
            }
        }
        if (strings.length > 0)
        {
            if (strings[0].equalsIgnoreCase("help") && !childCommands.containsKey("help"))
            {
                if (command.getUsage().equals(""))
                {
                    displayDefaultUsage(sender, s);
                }
                else
                {
                    sender.sendMessage(command.getUsage());
                }
                return;
            }
            synchronized (childCommands)
            {
                ChildCommand child = childCommands.get(strings[0]);
                if (child == null)
                {
                    if (command.getUsage().equals(""))
                    {
                        displayDefaultUsage(sender, s);
                    }
                    else
                    {
                        sender.sendMessage(command.getUsage());
                    }
                    return;
                }
                if (!child.checkPermission(sender))
                {
                    sender.sendMessage("§c" + child.getCommandHandler().noPermission());
                }
                String[] args = new String[strings.length - 1];
                System.arraycopy(strings, 1, args, 0, strings.length - 1);
                CommandInfo info = new CommandInfo(this, child.getCommandHandler(), sender, strings[0], args);
                try
                {
                    child.getHandler().handleCommand(info);
                }
                catch (CommandException e)
                {
                    sender.sendMessage("§cFailed to handle command properly.");
                }
            }
        }
    }

    @Override
    public void handleCommand(CommandInfo info) throws CommandException
    {
        try
        {
            this.getMethod().invoke(queuedCommand.getObject(), info);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
    }

    private void displayDefaultUsage(CommandSender sender, String command)
    {
        sender.sendMessage("§b<=====EXceL Command API=====>");
        sender.sendMessage(String.format("§dUsage: /%s %s", command,
                                         childCommands.isEmpty() ? "" : "<command> [<args>]"));
        for (Entry<String, ChildCommand> entry : childCommands.entrySet())
        {
            String usage = entry.getValue().getCommandHandler().usage();
            String description = entry.getValue().getCommandHandler().description();
            sender.sendMessage(String.format("/%s %s %s %s", command, entry.getKey(), usage, description));
        }
    }

    private Method getMethod()
    {
        return queuedCommand.getMethod();
    }

    public CommandHandler getCommandHandler()
    {
        return getMethod().getAnnotation(CommandHandler.class);
    }
}
