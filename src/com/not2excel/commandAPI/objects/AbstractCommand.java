package com.not2excel.commandAPI.objects;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @author Richmond Steele
 * @since 12/16/13
 * All rights Reserved
 * Please read included LICENSE file
 */
public class AbstractCommand extends Command
{
    private CommandExecutor executor = null;

    public AbstractCommand(String name)
    {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings)
    {
        return executor != null && executor.onCommand(commandSender, this, s, strings);
    }

    public CommandExecutor getExecutor()
    {
        return executor;
    }

    public void setExecutor(CommandExecutor executor)
    {
        this.executor = executor;
    }
}
