package com.not2excel.commandAPI.handler;

import com.not2excel.commandAPI.objects.CommandInfo;
import com.not2excel.commandAPI.objects.QueuedCommand;

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
