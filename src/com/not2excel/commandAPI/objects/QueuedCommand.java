package com.not2excel.commandAPI.objects;

import java.lang.reflect.Method;

/**
 * @author Richmond Steele
 * @since 12/17/13
 * All rights Reserved
 * Please read included LICENSE file
 */
public class QueuedCommand
{
    private final Object object;
    private final Method method;

    public QueuedCommand(Object object, Method method)
    {
        this.object = object;
        this.method = method;
    }

    public Object getObject()
    {
        return object;
    }

    public Method getMethod()
    {
        return method;
    }
}
