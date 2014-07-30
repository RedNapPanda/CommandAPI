package com.not2excel.api.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Richmond Steele, William Reed
 * @since 12/16/13
 * All rights Reserved
 * Please read included LICENSE file
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandHandler
{
    /**
     * Label of the command
     * Sub-commands have '.' to split the child from the parent
     * /test => test
     * /test set => test.set
     *
     * @return command label
     */
    String command();

    /**
     * Aliases of the command
     * /test2 => /test
     * /test set2 => /test set
     *
     * @return command aliases
     */
    String[] aliases() default {};

    /**
     * Permission to use this command
     *
     * @return permission
     */
    String permission() default "";

    /**
     * Message to send to CommandSender if they do not have permission to use this command
     *
     * @return noPermission message
     */
    String noPermission() default "You don't have permission to do that.";

    /**
     * Usage for the command
     * /test
     * /test set [player]
     *
     * @return command usage
     */
    String usage() default "";

    /**
     * Description of command
     * /test => Testing the dynamic CommandAPI
     *
     * @return command description
     */
    String description() default "";

    /**
     * Minimum arguments the command must have
     * must be > 0
     * @return min
     */
    int min() default 0;

    /**
     * Max arguments the command can have
     * -1 is unlimited
     * @return max
     */
    int max() default -1;
    
    /**
     * Determines if you want the plugin to be only executed by a player and not on command line
     * @return true if you want this only to be used by an in game player
     */
    boolean playerOnly() default false;
}
