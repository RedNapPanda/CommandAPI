package com.not2excel.api.command.objects;

import com.not2excel.api.command.CommandHandler;
import com.not2excel.api.command.handler.CommandException;
import com.not2excel.api.command.handler.DefaultHandler;
import com.not2excel.api.command.handler.Handler;
import com.not2excel.api.util.Colorizer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

/**
 * @author Richmond Steele
 * @since 12/17/13
 * All rights Reserved
 * Please read included LICENSE file
 */
public class RegisteredCommand extends ParentCommand implements CommandExecutor, Handler
{
    private final QueuedCommand queuedCommand;
    private String  command = "";
    private Handler handler = this;

    public RegisteredCommand(QueuedCommand queuedCommand)
    {
        this.queuedCommand = queuedCommand;
        this.setHandler(new DefaultHandler(queuedCommand));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args)
    {

//        processCommand(sender, command, s, args);
        try
        {
            CommandHandler commandHandler = getMethod().getAnnotation(CommandHandler.class);
            List<String> strings = Arrays.asList(args);
            getHandler().handleCommand(new CommandInfo(this, this, commandHandler, sender, s,
                                                       sortQuotedArgs(strings), commandHandler.usage(),
                                                       commandHandler.permission()));
        }
        catch (CommandException e)
        {
            Colorizer.send(sender, "<red>Failed to handle command properly.");
        }
        return true;
    }

//    private void processCommand(CommandSender sender, Command command, String s, String[] args)
//    {
//        List<String> strings = Arrays.asList(args);
//        if (strings.size() == 0)
//        {
//            if (queuedCommand != null)
//            {
//                try
//                {
//                    CommandHandler commandHandler = getMethod().getAnnotation(CommandHandler.class);
//                    getHandler().handleCommand(new CommandInfo(this, this, commandHandler, sender, s,
//                                                               sortQuotedArgs(strings), commandHandler.usage(),
//                                                               commandHandler.permission()));
//                }
//                catch (CommandException e)
//                {
//                    Colorizer.send(sender, "<red>Failed to handle command properly.");
//                }
//            }
//            else
//            {
//                displayDefaultUsage(sender, s, this);
//            }
//        }
//        if (strings.size() > 0)
//        {
//            if (strings.get(0).equalsIgnoreCase("help") && !childCommands.containsKey("help"))
//            {
//                if (command.getUsage().equals(""))
//                {
//                    displayDefaultUsage(sender, s, this);
//                }
//                else
//                {
//                    sender.sendMessage(command.getUsage());
//                }
//                return;
//            }
//            synchronized (childCommands)
//            {
//                ChildCommand child = childCommands.get(strings.get(0));
//                if (child == null)
//                {
//                    if (command.getUsage().equals(""))
//                    {
//                        displayDefaultUsage(sender, s, this);
//                    }
//                    else
//                    {
//                        sender.sendMessage(command.getUsage());
//                    }
//                    return;
//                }
//                if (!child.checkPermission(sender))
//                {
//                    Colorizer.send(sender, "<red>" + child.getCommandHandler().noPermission());
//                }
//                CommandInfo info = new CommandInfo(this, child, child.getCommandHandler(), sender, strings.get(0),
//                                                   strings.subList(1, strings.size() - 1),
//                                                   child.getUsage(), child.getPermission());
//                try
//                {
//                    child.getHandler().handleCommand(info);
//                }
//                catch (CommandException e)
//                {
//                    Colorizer.send(sender, "<red>Failed to handle command properly.");
//                }
//            }
//        }
//    }

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

    public void displayDefaultUsage(CommandSender sender, String command, ParentCommand parent)
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

    public void recursivelyDisplayChildUsage(CommandSender sender, ParentCommand parent, String prefix)
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

    public List<String> sortQuotedArgs(List<String> args)
    {
        return sortEnclosedArgs(args, '"');
    }

    private List<String> sortEnclosedArgs(List<String> args, char c)
    {
        List<String> strings = new ArrayList<String>(args.size());
        for (int i = 1; i < args.size(); ++i)
        {
            String arg = args.get(i);
            if (arg.length() == 0)
            {
                continue;
            }
            if (arg.charAt(0) == c)
            {
                int j;
                final StringBuilder builder = new StringBuilder();
                for (j = i + 1; j < args.size(); ++j)
                {
                    String arg2 = args.get(j);
                    if (arg2.charAt(arg2.length() - 1) == c && arg2.length() > 1)
                    {
                        builder.append(j != i ? " " : "").append(arg2.substring(j == i ? 1 : 0, arg2.length() - 1));
                    }
                    else
                    {
                        builder.append(j == i ? arg2.substring(1) : " " + arg2);
                    }
                }
                if (j < args.size())
                {
                    arg = builder.toString();
                    i = j;
                }
            }
            strings.add(arg);
        }
        return strings;
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
