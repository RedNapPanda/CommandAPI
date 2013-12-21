package com.not2excel.commandAPI;


import com.not2excel.commandAPI.handler.DefaultHandler;
import com.not2excel.commandAPI.logging.LevelLogger;
import com.not2excel.commandAPI.logging.LogType;
import com.not2excel.commandAPI.objects.*;
import com.not2excel.commandAPI.utils.ClassEnumerator;
import com.not2excel.commandAPI.utils.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.help.GenericCommandHelpTopic;
import org.bukkit.help.HelpTopic;
import org.bukkit.help.HelpTopicComparator;
import org.bukkit.help.IndexHelpTopic;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Richmond Steele
 * @since 12/17/13
 * All rights Reserved
 * Please read included LICENSE file
 */
public class CommandManager
{
    private final Plugin plugin;
    private final Map<Integer, List<QueuedCommand>> queuedCommands     =
            new ConcurrentHashMap<Integer, List<QueuedCommand>>();
    private final Map<String, RegisteredCommand>    registeredCommands =
            new ConcurrentHashMap<String, RegisteredCommand>();
    private final LevelLogger logger;
    private       CommandMap  commandMap;

    public CommandManager(Plugin plugin)
    {
        this.plugin = plugin;
        this.logger = LevelLogger.getInstance();
        this.logger.setLogType("Command");
        this.logger.setTimeStamped(false);
    }

    public void registerHelp()
    {
        Set<HelpTopic> help = new TreeSet<HelpTopic>(HelpTopicComparator.helpTopicComparatorInstance());
        for (String s : registeredCommands.keySet())
        {
            Command cmd = commandMap.getCommand(s);
            if (cmd != null)
            {
                HelpTopic topic = new GenericCommandHelpTopic(cmd);
                help.add(topic);
            }
        }
        IndexHelpTopic topic = new IndexHelpTopic(plugin.getName(), "All commands for " + plugin.getName(), null, help,
                                                  "Below is a list of all " + plugin.getName() + " commands:");
        Bukkit.getServer().getHelpMap().addTopic(topic);
    }

    public void registerCommands()
    {
        logger.log(
                "WARNING: The CommandAPI cannot dynamically register commands from " +
                "classes that do not use the default constructor.");
        logger.log("SOLUTION: Please use the static registrar registerCommands(object) if you need to register " +
                   "commands from classes that do not use the default constructor.");
        Class[] classes = ClassEnumerator.getInstance().getClassesFromThisJar();
        if (classes == null || classes.length == 0)
        {
            return;
        }
        for (Class c : classes)
        {
            try
            {
                if (CommandListener.class.isAssignableFrom(c) &&
                    !c.isInterface() &&
                    !c.isEnum() &&
                    !c.isAnnotation())
                {
                    if(JavaPlugin.class.isAssignableFrom(c))
                    {
                        if(plugin.getClass().equals(c))
                        {
                            logger.log("Searching class: " + c.getSimpleName());
                            registerCommands(plugin);
                        }
                    }
                    else
                    {
                        logger.log("Searching class: " + c.getSimpleName());
                        registerCommands(c.newInstance());
                    }
                }
            }
            catch (InstantiationException e)
            {
                logger.log(LogType.ERROR, c.getSimpleName() + " does not use the default constructor");
                e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
                logger.log(LogType.ERROR, c.getSimpleName() + " does not use the default constructor");
                e.printStackTrace();
            }
        }
        processQueuedCommands();
    }

    private void processQueuedCommands()
    {
        synchronized (queuedCommands)
        {
            if (!queuedCommands.isEmpty())
            {
                logger.log("Processing Queued commands.");
            }
            else
            {
                logger.log("There are no Queued commands.");
            }
            int MAX_ITERATION = 0;
            for (int i : queuedCommands.keySet())
            {
                if (i > MAX_ITERATION)
                {
                    MAX_ITERATION = i;
                }
            }
            for (int i = 1; i <= MAX_ITERATION; i++)
            {
                List<QueuedCommand> queuedCommandList = queuedCommands.get(i);
                if (queuedCommandList == null || queuedCommandList.isEmpty())
                {
                    continue;
                }
                for (QueuedCommand queue : queuedCommandList)
                {
                    CommandHandler commandHandler = queue.getMethod().getAnnotation(CommandHandler.class);
                    String[] list = commandHandler.command().split("\\.");
                    RegisteredCommand registered;
                    synchronized (registeredCommands)
                    {
                        if (!registeredCommands.containsKey(list[0]))
                        {
                            logger.log("Registering Empty Base Command: " + list[0]);
                            RegisteredCommand registeredEmpty = new RegisteredCommand(null);
                            synchronized (registeredCommands)
                            {
                                registeredCommands.put(list[0], registeredEmpty);
                            }
                            AbstractCommand abstractCmd = new AbstractCommand(list[0]);
                            abstractCmd.setDescription("Use '/" + list[0] + " help' to view the subcommands.");
                            abstractCmd.setPermission("");
                            abstractCmd.setPermissionMessage("You don't have permission to do that.");
                            abstractCmd.setUsage("/" + list[0] + " <command>");
                            abstractCmd.setExecutor(registeredEmpty);
                            registerBaseCommand(abstractCmd);
                            continue;
                        }
                        registered = registeredCommands.get(list[0]);
                    }
                    registerChild(queue, commandHandler, registered, list[list.length - 1]);
                    for (String s : commandHandler.aliases())
                    {
                        registerChild(queue, commandHandler, registered, s);
                    }
                    logger.log("Registered queued command: " + commandHandler.command());
                }
            }
        }
    }

    private void registerChild(QueuedCommand queue, CommandHandler commandHandler, RegisteredCommand registered,
                               String s)
    {
        ChildCommand child = new ChildCommand(commandHandler);
        Parent parent = recursivelyFindInnerMostParent(commandHandler.command(), registered, 1);
        if (parent.getClass().equals(registered.getClass()))
        {
            registered.addChild(s, child);
            registered.getChild(s).setHandler(new DefaultHandler(queue));
        }
        else
        {
            ChildCommand childParent = (ChildCommand) parent;
            childParent.addChild(s, child);
            childParent.getChild(s).setHandler(new DefaultHandler(queue));
        }
    }

    private Parent recursivelyFindInnerMostParent(String command, Parent parent, int start)
    {
        String[] list = command.split("\\.");
        return parent.hasChild(list[start]) ?
               recursivelyFindInnerMostParent(list[start], parent.getChild(list[start]), ++start) :
               parent;
    }

    public void registerCommands(Object classObject)
    {
        if (!CommandListener.class.isAssignableFrom(classObject.getClass()))
        {
            return;
        }
        for (Method method : classObject.getClass().getDeclaredMethods())
        {
            logger.log("Testing if method: " + method.getName() + " is a CommandHandler");
            CommandHandler commandHandler = method.getAnnotation(CommandHandler.class);
            if (commandHandler == null ||
                method.getParameterTypes()[0] != CommandInfo.class)
            {
                return;
            }
            logger.log("Method: " + method.getName() + " is a CommandHandler");
            Object object = classObject;
            if (Modifier.isStatic(method.getModifiers()))
            {
                object = null;
            }
            if (commandHandler.command().contains("."))
            {
                queueCommand(object, method, commandHandler);
            }
            else
            {
                registerBaseCommand(object, method, commandHandler);
            }
        }
        processQueuedCommands();
    }

    private void registerBaseCommand(Object classObject, Method method, CommandHandler commandHandler)
    {
        logger.log("Registering Base Command: " + commandHandler.command());
        QueuedCommand queue = new QueuedCommand(classObject, method);
        RegisteredCommand registered = new RegisteredCommand(queue);
        synchronized (registeredCommands)
        {
            registeredCommands.put(commandHandler.command(), registered);
        }
        AbstractCommand abstractCmd = new AbstractCommand(commandHandler.command());
        abstractCmd.setAliases(Arrays.asList(commandHandler.aliases()));
        abstractCmd.setDescription(commandHandler.description());
        abstractCmd.setPermission(commandHandler.permission());
        abstractCmd.setPermissionMessage(commandHandler.noPermission());
        abstractCmd.setUsage(commandHandler.usage());
        abstractCmd.setExecutor(registered);
        registerBaseCommand(abstractCmd);
    }

    private void registerBaseCommand(AbstractCommand command)
    {
        logger.log("Registering command: " + command.getName() + " to commandMap.");
        if (getCommandMap().getCommand(command.getName()) == null)
        {
            getCommandMap().register(plugin.getName(), command);
        }
    }

    private void queueCommand(Object classObject, Method method, CommandHandler commandHandler)
    {
        synchronized (queuedCommands)
        {
            logger.log("Queueing Command: " + commandHandler.command());
            QueuedCommand queue = new QueuedCommand(classObject, method);
            int numberOfChildren = commandHandler.command().split("\\.").length - 1;
            List<QueuedCommand> queueList = queuedCommands.get(numberOfChildren);
            if (queueList == null)
            {
                queueList = new LinkedList<QueuedCommand>();
            }
            if (!queueList.contains(queue))
            {
                queueList.add(queue);
            }
            queuedCommands.put(numberOfChildren, queueList);
        }
    }

    private CommandMap getCommandMap()
    {
        if (commandMap == null)
        {
            if (plugin.getServer().getPluginManager() instanceof SimplePluginManager)
            {
                try
                {
                    Object field = ReflectionUtils.getField(plugin.getServer()
                                                                  .getPluginManager(), "commandMap");
                    commandMap = (SimpleCommandMap) field;
                }
                catch (NoSuchFieldException e)
                {
                    e.printStackTrace();
                }
                catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return commandMap;
    }
}
