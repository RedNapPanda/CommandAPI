package com.not2excel.commandAPI.logging;

import com.not2excel.commandAPI.utils.StringUtil;

/**
 * @author Richmond Steele
 * @since 12/18/13
 * All rights Reserved
 * Please read included LICENSE file
 */
public enum LogType
{

    INFO,
    WARNING,
    ERROR,
    FATAL,
    TRACE,
    HOOK,
    SCAN,
    DEBUG,
    SUCCESS,
    IO;
    public String getLevel()
    {
        return StringUtil.capatilizeFirstLetterOnly(this.name()).trim();
    }
}

