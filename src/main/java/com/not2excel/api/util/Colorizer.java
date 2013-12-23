package com.not2excel.api.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Richmond Steele
 * @since 12/16/13
 * All rights Reserved
 * Please read included LICENSE file
 */
public class Colorizer
{
    private static final int MAX_SIZE = 1000;
    private static final Map<String, String>    colorizedStrings = new ConcurrentHashMap<String, String>();
    private static final Map<String, ChatColor> customColors     = new ConcurrentHashMap<String, ChatColor>();

    static
    {
        addColor("purple", ChatColor.LIGHT_PURPLE);
        addColor("cyan", ChatColor.AQUA);
        addColor("dark_cyan", ChatColor.DARK_AQUA);
    }

    /**
     * Converts simple colors into ChatColor values
     * eg. <blue>test => §9test (actually its technically ChatColor.BLUE, not §9. despite them being the same)
     *
     * @param string
     *         input string
     * @return string with proper ChatColor inputted
     */
    public static String formatColors(String string)
    {
        synchronized (colorizedStrings)
        {
            if (colorizedStrings.containsKey(string))
            {
                return colorizedStrings.get(string);
            }
            else
            {
                Pattern p = Pattern.compile("<([a-zA-Z_]*)>");
                Matcher m = p.matcher(string);
                String colorized = string;
                while (m.find())
                {
                    colorized = colorized.replaceFirst(p.pattern(), convertToColorCode(m.group(1)));
                }
                colorizedStrings.put(string, colorized);
                if(colorizedStrings.size() > MAX_SIZE)
                {
                    reduceSize();
                }
                return colorized;
            }
        }
    }

    /**
     * Formats string and colorizes it
     *
     * @param string
     *         String containing colors and %s %d etc.
     * @param objects
     *         Objects to be formatted into the string
     * @return formatted and colorized String
     */
    public static String formatString(String string, Object... objects)
    {
        string = String.format(string, objects);
        return formatColors(string);
    }

    public static void send(CommandSender sender, String string, Object... objects)
    {
        sender.sendMessage(formatString(string, objects));
    }

    public static void addColor(String s, ChatColor color)
    {
        synchronized (customColors)
        {
            if(!customColors.containsKey(s.toUpperCase()))
            {
                customColors.put(s.toUpperCase(), color);
            }
        }
    }

    public static void removeColor(String s)
    {
        synchronized (customColors)
        {
            if(customColors.containsKey(s.toUpperCase()))
            {
                customColors.remove(s.toUpperCase());
            }
        }
    }

    /**
     * Wrapper for <code>ChatColor.valueOf()</code>
     *
     * @param s
     *         string to get color of
     * @return ChatColor char
     */
    private static String convertToColorCode(String s)
    {
        synchronized (customColors)
        {
            if(customColors.containsKey(s.toUpperCase()))
            {
                return customColors.get(s.toUpperCase()).toString();
            }
        }
        try
        {
            return ChatColor.valueOf(s.toUpperCase()).toString();
        }
        catch(Exception e)
        {
            return "<" + s + ">";
        }
    }

    private static void reduceSize()
    {
        synchronized (colorizedStrings)
        {
            Iterator<String> iterator = colorizedStrings.values().iterator();
            for(int i = colorizedStrings.size() / 10; i >= 0; --i)
            {
                if(!iterator.hasNext())
                {
                    break;
                }
                iterator.next();
                iterator.remove();
            }
        }
    }
}
