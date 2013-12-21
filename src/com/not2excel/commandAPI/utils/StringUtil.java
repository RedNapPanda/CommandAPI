package com.not2excel.commandAPI.utils;

import java.util.Random;

/**
 * @author Richmond Steele
 * @since 12/18/13
 * All rights Reserved
 * Please read included LICENSE file
 */
public class StringUtil
{
    private static final Random random = new Random();


    public static boolean containsChar(final char c, final Character[] charSet)
    {
        for (final char test : charSet)
        {
            if (test == c)
            { return true; }
        }
        return false;
    }

    /**
     * Generates a random string
     * @param length - length of the random string
     * @param charArray - include a char array if you wish the random string to only include those chars
     * @return random string
     */
    public static String randomString(final int length, final Character[]... charArray)
    {
        final StringBuilder stringBuilder = new StringBuilder();
        final int min = 33, max = 126;
        if (charArray.length > 0)
        {
            final Character[] chars = charArray[0];
            stringBuilder.append(chars[random.nextInt(chars.length - 1)]);
        }
        else
        {
            for (int i = 0; i < length; i++)
            {
                final char c = (char) (min + (int) (Math.random() * ((max - min) + 1)));
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Capatilizes the first letter of a string and lower cases the rest
     * @param string
     * @return
     */
    public static String capatilizeFirstLetterOnly(final String string)
    {
        return string.substring(0, 1).toUpperCase().concat(string.substring(1).toLowerCase());
    }
}
