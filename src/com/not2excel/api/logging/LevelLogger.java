package com.not2excel.api.logging;

/**
 * @author Richmond Steele
 * @since 12/18/13
 * All rights Reserved
 * Please read included LICENSE file
 */
public class LevelLogger extends Logger
{
    /**
     * Singleton instance
     */
    private static volatile LevelLogger instance;
    private String  logType     = LogType.INFO.getLevel();
    private boolean timeStamped = true;

    /**
     * Returns the singleton instance
     *
     * @return instance
     */
    public static LevelLogger getInstance()
    {
        if (instance == null)
        { instance = getNewInstance(); }
        return instance;
    }

    /**
     * Creates a new instance of this class
     *
     * @return instance of this class
     */
    public static LevelLogger getNewInstance()
    {
        return new LevelLogger();
    }

    @SuppressWarnings("unchecked")
    public void log(final Object data)
    {
        if(timeStamped)
        {
            this.log(logType, data);
        }
        else
        {
            this.logTimeless(logType, data);
        }
    }

    @SuppressWarnings("unchecked")
    public void log(final LogType logType, final Object data)
    {
        if(timeStamped)
        {
            this.log(logType.getLevel(), data);
        }
        else
        {
            this.logTimeless(logType.getLevel(), data);
        }
    }

    public String getLogType()
    {
        return logType;
    }

    public void setLogType(String logType)
    {
        this.logType = logType;
    }

    public boolean isTimeStamped()
    {
        return timeStamped;
    }

    public void setTimeStamped(boolean timeStamped)
    {
        this.timeStamped = timeStamped;
    }
}
