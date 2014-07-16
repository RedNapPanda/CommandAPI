package com.not2excel.api.command.handler;

import com.not2excel.api.command.objects.ChildCommand;
import com.not2excel.api.command.objects.CommandInfo;
import com.not2excel.api.command.objects.ParentCommand;
import com.not2excel.api.command.objects.QueuedCommand;
import com.not2excel.api.util.Colorizer;

import java.util.Collections;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author Richmond Steele
 * @since 12/18/13
 * All rights Reserved
 * Please read included LICENSE file
 */
public class DefaultHandler implements Handler
{
    private final QueuedCommand queue;

    public DefaultHandler(QueuedCommand queue)
    {
        this.queue = queue;
    }

    @Override
    public void handleCommand(CommandInfo info) throws CommandException
    {
        List<String> strings = info.getArgs();
        ParentCommand parentCommand = info.getParentCommand();
        String command = info.getCommand();

        if (strings.size() == 0 || parentCommand.getChildCommands().size() == 0)
        {
            if (queue != null)
            {
                try
                {
                    sendCommand(info, command); 
                }
                catch(CommandException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                info.getRegisteredCommand().displayDefaultUsage(info.getSender(), command, info.getParentCommand());
            }
        }
        else if (strings.size() > 0)
        {
            if (strings.get(0).equalsIgnoreCase("help") && !parentCommand.getChildCommands().containsKey("help"))
            {
                
                if (info.getUsage().equals(""))
                {
                    info.getRegisteredCommand().displayDefaultUsage(info.getSender(), command, info.getParentCommand());
                }
                else
                {
                    info.getSender().sendMessage(info.getUsage());
                }
                return;
            }
            ChildCommand child = parentCommand.getChildCommands().get(strings.get(0));
            if (child == null)
            {
                //needed to send parent command instead of throwing errors so that parent command can process args
                try
                {
                    sendCommand(info, command); 
                }
                catch(CommandException e)
                {
                    e.printStackTrace();
                }
                return;
            }
            if (!child.checkPermission(info.getSender()))
            {
                Colorizer.send(info.getSender(), "<red>" + child.getCommandHandler().noPermission());
                return;
            }
            CommandInfo cmdInfo = new CommandInfo(info.getRegisteredCommand(), child, child.getCommandHandler(),
                                                  info.getSender(), strings.get(0),
                                                  strings.size() == 1 ? 
                                                  Collections.<String> emptyList() :
                                                  strings.subList(1, strings.size() - 1),
                                                  info.getUsage(),
                                                  info.getPermission());
            try
            {
                child.getHandler().handleCommand(cmdInfo);
            }
            catch (CommandException e)
            {
                Colorizer.send(info.getSender(), "<red>Failed to handle command properly.");
            }
        }
    }
    
    private void sendCommand(CommandInfo info, String command) throws CommandException
    {
        if (info.getArgsLength() < info.getCommandHandler().min())
                {
                    info.getSender().sendMessage("Too few arguments.");
                    info.getRegisteredCommand().displayDefaultUsage(info.getSender(), command, info.getParentCommand());
                    throw new CommandException("Too few arguments.");
                }
                if (info.getCommandHandler().max() != -1 && info.getArgsLength() > info.getCommandHandler().max())
                {
                    info.getSender().sendMessage("Too many arguments.");
                    info.getRegisteredCommand().displayDefaultUsage(info.getSender(), command, info.getParentCommand());
                    throw new CommandException("Too many arguments.");
                }
                if (!info.getSender().hasPermission(info.getCommandHandler().permission()))
                {
                    Colorizer.send(info.getSender(), "<red>" + info.getCommandHandler().noPermission());
                    return;
                }
                try
                {
                    queue.getMethod().invoke(queue.getObject(), info);
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
}
