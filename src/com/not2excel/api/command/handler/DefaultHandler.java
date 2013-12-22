package com.not2excel.api.command.handler;

import com.not2excel.api.command.objects.ChildCommand;
import com.not2excel.api.command.objects.CommandInfo;
import com.not2excel.api.command.objects.Parent;
import com.not2excel.api.command.objects.QueuedCommand;
import com.not2excel.api.util.Colorizer;

import java.lang.reflect.InvocationTargetException;

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
        String[] strings = info.getArgs();
        Parent parent = info.getParent();
        String command = info.getCommand();

        if (strings.length == 0 || parent.getChildCommands().size() == 0)
        {
            if (queue != null)
            {
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
            else
            {
                info.getRegisteredCommand().displayDefaultUsage(info.getSender(), command, info.getParent());
            }
        }
        if (strings.length > 0)
        {
            if (strings[0].equalsIgnoreCase("help") && !parent.getChildCommands().containsKey("help"))
            {
                if (info.getUsage().equals(""))
                {
                    info.getRegisteredCommand().displayDefaultUsage(info.getSender(), command, info.getParent());
                }
                else
                {
                    info.getSender().sendMessage(info.getUsage());
                }
                return;
            }
            synchronized (parent.getChildCommands())
            {
                ChildCommand child = parent.getChildCommands().get(strings[0]);
                if (child == null)
                {
                    if (info.getUsage().equals(""))
                    {
                        info.getRegisteredCommand().displayDefaultUsage(info.getSender(), command, info.getParent());
                    }
                    else
                    {
                        info.getSender().sendMessage(info.getUsage());
                    }
                    return;
                }
                if (!child.checkPermission(info.getSender()))
                {
                    Colorizer.send(info.getSender(), "<red>" + child.getCommandHandler().noPermission());
                }
                String[] args = new String[strings.length - 1];
                System.arraycopy(strings, 1, args, 0, strings.length - 1);
                CommandInfo cmdInfo = new CommandInfo(info.getRegisteredCommand(), child, child.getCommandHandler(),
                                                      info.getSender(), strings[0], args, info.getUsage(),
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
    }
}
