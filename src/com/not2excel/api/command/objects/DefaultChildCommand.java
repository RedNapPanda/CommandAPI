package com.not2excel.api.command.objects;

import com.not2excel.api.command.handler.DefaultHandler;

/**
 * @author Richmond Steele
 * @since 12/22/13
 * All rights Reserved
 * Please read included LICENSE file
 */
public class DefaultChildCommand extends ChildCommand
{
    public DefaultChildCommand(String command)
    {
        super(null);
        this.command = command;
        this.setHandler(new DefaultHandler(null));
    }

    public void setPermission(String permission)
    {
        this.permission = permission;
    }
}
