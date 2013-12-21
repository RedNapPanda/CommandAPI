package com.not2excel.commandAPI.objects;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Richmond Steele
 * @since 12/18/13
 * All rights Reserved
 * Please read included LICENSE file
 */
public class Parent
{
    protected final Map<String, ChildCommand> childCommands = new ConcurrentHashMap<String, ChildCommand>();

    public void addChild(String s, ChildCommand child)
    {
        {
            synchronized (childCommands)
            {
                childCommands.put(s.toLowerCase(), child);
            }
        }
    }

    public boolean hasChild(String s)
    {
        synchronized (childCommands)
        {
            return childCommands.containsKey(s.toLowerCase());

        }
    }

    public ChildCommand getChild(String s)
    {
        synchronized (childCommands)
        {
            return childCommands.get(s.toLowerCase());
        }
    }
}
