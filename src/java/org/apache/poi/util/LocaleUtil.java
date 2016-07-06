/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */
        

package org.apache.poi.util;

import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * This utility class is used to set locale and time zone settings beside
 * of the JDK internal {@link java.util.Locale#setDefault(Locale)} and
 * {@link java.util.TimeZone#setDefault(TimeZone)} methods, because
 * the locale/time zone specific handling of certain office documents -
 * maybe for different time zones / locales ... - shouldn't affect
 * other java components.
 * 
 * The settings are saved in a {@link java.lang.ThreadLocal},
 * so they only apply to the current thread and can't be set globally.
 */
public final class LocaleUtil {
    private LocaleUtil() {
        // no instances of this class
    }
    
    /**
     * Excel doesn't store TimeZone information in the file, so if in doubt,
     *  use UTC to perform calculations
     */
    public static final TimeZone TIMEZONE_UTC = TimeZone.getTimeZone("UTC");
    
    /**
     * Default encoding for unknown byte encodings of native files
     * (at least it's better than to rely on a platform dependent encoding
     * for legacy stuff ...)
     */
    public static final Charset CHARSET_1252 = Charset.forName("CP1252");

    private static final ThreadLocal<TimeZone> userTimeZone = new ThreadLocal<TimeZone>() {
        @Override
        @SuppressForbidden("implementation around default locales in POI")
        protected TimeZone initialValue() {
            return TimeZone.getDefault();
        }
    };

    private static final ThreadLocal<Locale> userLocale = new ThreadLocal<Locale>() {
        @Override
        @SuppressForbidden("implementation around default locales in POI")
        protected Locale initialValue() {
            return Locale.getDefault();
        }
    };
    
    /**
     * As time zone information is not stored in any format, it can be
     * set before any date calculations take place.
     * This setting is specific to the current thread.
     *
     * @param timezone the timezone under which date calculations take place
     */
    public static void setUserTimeZone(TimeZone timezone) {
        userTimeZone.set(timezone);
    }
    
    /**
     * @return the time zone which is used for date calculations, defaults to UTC
     */
    public static TimeZone getUserTimeZone() {
        return userTimeZone.get();
    }

    /**
     * Sets default user locale.
     * This setting is specific to the current thread.
     */
    public static void setUserLocale(Locale locale) {
        userLocale.set(locale);
    }

    /**
     * @return the default user locale, defaults to {@link Locale#ROOT}
     */
    public static Locale getUserLocale() {
        return userLocale.get();
    }

    /**
     * @return a calendar for the user locale and time zone
     */
    public static Calendar getLocaleCalendar() {
        return getLocaleCalendar(getUserTimeZone());        
    }

    /**
     * Convenience method - month is 0-based as in java.util.Calendar
     *
     * @param year
     * @param month
     * @param day
     * @return a calendar for the user locale and time zone, and the given date
     */
    public static Calendar getLocaleCalendar(int year, int month, int day) {
        return getLocaleCalendar(year, month, day, 0, 0, 0);
    }

    /**
     * Convenience method - month is 0-based as in java.util.Calendar
     *
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @param second
     * @return a calendar for the user locale and time zone, and the given date
     */
    public static Calendar getLocaleCalendar(int year, int month, int day, int hour, int minute, int second) {
        Calendar cal = getLocaleCalendar();
        cal.set(year,  month, day, hour, minute, second);
        cal.clear(Calendar.MILLISECOND);
        return cal;
    }
    
    /**
     * @return a calendar for the user locale and time zone
     */
    public static Calendar getLocaleCalendar(TimeZone timeZone) {
        return Calendar.getInstance(timeZone, getUserLocale());        
    }
}

