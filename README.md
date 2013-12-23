CommandAPI 
==========
[![Build Status](https://travis-ci.org/Not2EXceL/CommandAPI.png?branch=master)](https://travis-ci.org/Not2EXceL/CommandAPI)

__About:__ This is a CommandAPI that I developed so that I can avoid having to statically register any command whether using reflection and a CommandExecutor, or simply putting the commands into my plugin.yml. This is a purely annotation based API.  The functionality to statically add commands is there, I just have not implemented any ways that are easy for a user to do so. 

__How to use:__ Using this CommandAPI is super simple, and only requires 3 lines to register the commands, and obviously the commands themselves.

First in either your onEnable() or onLoad() you're going to want to do this:
```java
CommandManager commandManager = new CommandManager(this); //this == plugin instance
commandManager.registerCommands(); //registers commands from anywhere in the plugin jar
commandManager.registerHelp(); //registers a generated helptopic to bukkit
//so the /help PluginName displays our plugin's registered commands
```

Example commands to be registered: Here are some test commands to display how commands should be written to allow registration.  CommandListener is a required interface for any class you wish commands to be registered from.  This is to allow shrinkage of classes searched for commands, and increase registration time.
```java
public class TestCommand implements CommandListener //CommandListener is required
{

    /*
    command is the only required field for the annotation

    The base command is not required. If not created, a default one will be generated and will direct to the usage
    upon command use
     */
    @CommandHandler(command = "test")
    public static void testingCommand(CommandInfo info)
    {
        info.getSender().sendMessage("Test worked");
    }

    /*
    Sub commands can go as deep as you wish
    ie. test -> sub1 -> ... -> sub15
     */
       @CommandHandler(command = "test.test2", permission = "test.test2", noPermission = "LOL no permissions",
                    aliases = {"2", "testing"}, usage = "/test test2 <player>",
                    description = "Testing out all of the CommandHandler's attribute values")
    public void testingCommand2(CommandInfo info)
    {
        info.getSender().sendMessage("Test2 worked");
    }
}
```

__ChangeLog:__
    1.2:
        Added Maven Support
    1.1:
        Updated recursion to fix bug with sub commands below sub-2.
        Now properly displays help when displaying usage for sub commands
        Generates empty(default) sub commands if you don't create an annotation for them.
    1.0:
        Initial release.

__Source Code:__ Finally the source code is on github.  Its opensource and GPLv2 license.
https://github.com/Not2EXceL/CommandAPI
Duh its here XD

__Finally:__ Please leave any comments, suggestions, and/or bugs you may find while using this CommandAPI.
