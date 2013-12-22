package com.not2excel.api.command.objects;

import com.not2excel.api.command.CommandHandler;
import com.not2excel.api.command.handler.CommandException;
import com.not2excel.api.command.handler.Handler;
import com.not2excel.api.util.Colorizer;
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
    private String  command = "";
    private Handler handler = this;

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
            if (queuedCommand != null)
            {
                try
                {
                    CommandHandler commandHandler = getMethod().getAnnotation(CommandHandler.class);
                    getHandler().handleCommand(new CommandInfo(this, this, commandHandler, sender, s, strings,
                                                               commandHandler.usage(), commandHandler.permission()));
                }
                catch (CommandException e)
                {
                    Colorizer.send(sender, "<red>Failed to handle command properly.");
                }
            }
            else
            {
                displayDefaultUsage(sender, s, this);
            }
        }
        if (strings.length > 0)
        {
            if (strings[0].equalsIgnoreCase("help") && !childCommands.containsKey("help"))
            {
                if (command.getUsage().equals(""))
                {
                    displayDefaultUsage(sender, s, this);
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
                        displayDefaultUsage(sender, s, this);
                    }
                    else
                    {
                        sender.sendMessage(command.getUsage());
                    }
                    return;
                }
                if (!child.checkPermission(sender))
                {
                    Colorizer.send(sender, "<red>" + child.getCommandHandler().noPermission());
                }
                String[] args = new String[strings.length - 1];
                System.arraycopy(strings, 1, args, 0, strings.length - 1);
                CommandInfo info = new CommandInfo(this, child, child.getCommandHandler(), sender, strings[0], args,
                                                   child.getUsage(), child.getPermission());
                try
                {
                    child.getHandler().handleCommand(info);
                }
                catch (CommandException e)
                {
                    Colorizer.send(sender, "<red>Failed to handle command properly.");
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

    public void displayDefaultUsage(CommandSender sender, String command, Parent parent)
    {
        String prefix;
        Colorizer.send(sender, "<cyan><=====EXceL Command API=====>");
        if (command.equals(getCommand()))
        {
            Colorizer.send(sender, "<purple>Usage: /%s", command);
            prefix = command;
        }
        else
        {
            Colorizer.send(sender, "<purple>Usage for '%s'", command);
            prefix = recursivelyAddToPrefix(getCommand(), command);
        }
        recursivelyDisplayChildUsage(sender, parent, prefix);
    }

    public String recursivelyAddToPrefix(String prefix, String command)
    {
        for (Entry<String, ChildCommand> entry : getChildCommands().entrySet())
        {
            if (entry.getKey().equals(command))
            {
                prefix = prefix + " " + command;
            }
            else
            {
                prefix = prefix + " " + entry.getKey();
                recursivelyAddToPrefix(prefix, command);
            }
        }
        return prefix;
    }

    public void recursivelyDisplayChildUsage(CommandSender sender, Parent parent, String prefix)
    {
        for (Entry<String, ChildCommand> entry : parent.getChildCommands().entrySet())
        {
            String usage = entry.getValue().getUsage();
            String description = entry.getValue().getDescription();
            Colorizer.send(sender, "/%s %s %s %s", prefix, entry.getKey(), usage, description);
            if (!entry.getValue().getChildCommands().isEmpty())
            {
                prefix += " " + entry.getKey();
                recursivelyDisplayChildUsage(sender, entry.getValue(), prefix);
            }
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

    public Handler getHandler()
    {
        return handler;
    }

    public void setHandler(Handler handler)
    {
        this.handler = handler;
    }

    public String getPermission()
    {
        if (queuedCommand == null)
        {
            return "";
        }
        else
        {
            return getCommandHandler().permission();
        }
    }

    public String getCommand()
    {
        if (queuedCommand == null)
        {
            return command;
        }
        else
        {
            return getCommandHandler().command();
        }
    }

    public void setCommand(String command)
    {
        this.command = command;
    }
}
