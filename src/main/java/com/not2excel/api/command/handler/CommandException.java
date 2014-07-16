package com.not2excel.api.command.handler;

import com.not2excel.api.util.Colorizer;
import org.bukkit.command.CommandSender;

/**
 * @author Richmond Steele
 * @since 12/17/13
 * All rights Reserved
 * Please read included LICENSE file
 */
public class CommandException extends Exception
{
    
    private static final long serialVersionUID = 7841254778605849087L;
    
    public CommandException(String s)
    {
        super(s);
    }

    public CommandException(CommandSender sender, String s)
    {
        super(s);
        Colorizer.send(sender, s);
    }

    public CommandException(CommandSender sender, String s, Object... objects)
    {
        super(s);
        Colorizer.send(sender, s, objects);
    }
}
