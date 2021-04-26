

package java.util;

class TimeZoneImpl extends TimeZone {

    static String HOME_ID = null;

    public TimeZoneImpl() {
    }


    private TimeZoneImpl(int rawOffset, String ID) {
        this.rawOffset = rawOffset;
        this.ID = ID;
        dstSavings = millisPerHour;
    }


    private TimeZoneImpl(int rawOffset, String ID,
                         int startMonth, int startDay, int startDayOfWeek,
                         int startTime, int endMonth, int endDay,
                         int endDayOfWeek, int endTime, int dstSavings) {
        this.ID = ID;
        this.rawOffset = rawOffset;
        this.startMonth = startMonth;
        this.startDay = startDay;
        this.startDayOfWeek = startDayOfWeek;
        this.startTime = startTime;
        this.endMonth = endMonth;
        this.endDay = endDay;
        this.endDayOfWeek = endDayOfWeek;
        this.endTime = endTime;
        this.dstSavings = dstSavings;
        decodeRules();
        if (dstSavings <= 0) {
            throw new IllegalArgumentException("Illegal DST savings");
        }
    }


    private static final int ONE_MINUTE = 60 * 1000;
    private static final int ONE_HOUR = 60 * ONE_MINUTE;
    private static final int ONE_DAY = 24 * ONE_HOUR;


    public int getOffset(int era, int year, int month, int day,
                         int dayOfWeek, int millis) {
        if (month < Calendar.JANUARY
                || month > Calendar.DECEMBER) {

            throw new IllegalArgumentException("Illegal month " + month);
        }
        return getOffset(era, year, month, day, dayOfWeek, millis,
                staticMonthLength[month]);
    }


    int getOffset(int era, int year, int month, int day, int dayOfWeek,
                  int millis, int monthLength) {
        if (true) {


            if ((era != 0 && era != 1)
                    || month < Calendar.JANUARY
                    || month > Calendar.DECEMBER
                    || day < 1
                    || day > monthLength
                    || dayOfWeek < Calendar.SUNDAY
                    || dayOfWeek > Calendar.SATURDAY
                    || millis < 0
                    || millis >= millisPerDay
                    || monthLength < 28
                    || monthLength > 31) {

                throw new IllegalArgumentException();
            }
        } else {


            if (era != 0 && era != 1) {
                throw new IllegalArgumentException("Illegal era " + era);
            }
            if (month < Calendar.JANUARY || month > Calendar.DECEMBER) {
                throw new IllegalArgumentException("Illegal month " + month);
            }
            if (day < 1 || day > monthLength) {
                throw new IllegalArgumentException("Illegal day " + day);
            }
            if (dayOfWeek < Calendar.SUNDAY || dayOfWeek > Calendar.SATURDAY) {
                throw new IllegalArgumentException("Illegal day of week " + dayOfWeek);
            }
            if (millis < 0 || millis >= millisPerDay) {
                throw new IllegalArgumentException("Illegal millis " + millis);
            }
            if (monthLength < 28 || monthLength > 31) {
                throw new IllegalArgumentException("Illegal month length " + monthLength);
            }
        }

        int result = rawOffset;


        if (!useDaylight || year < startYear || era != 1) return result;


        boolean southern = (startMonth > endMonth);


        int startCompare = compareToRule(month, monthLength, day, dayOfWeek, millis,
                startMode, startMonth, startDayOfWeek,
                startDay, startTime);
        int endCompare = 0;


        if (southern != (startCompare >= 0)) {


            millis += dstSavings;
            while (millis >= millisPerDay) {
                millis -= millisPerDay;
                ++day;
                dayOfWeek = 1 + (dayOfWeek % 7);
                if (day > monthLength) {
                    day = 1;


                    ++month;
                }
            }
            endCompare = compareToRule(month, monthLength, day, dayOfWeek, millis,
                    endMode, endMonth, endDayOfWeek,
                    endDay, endTime);
        }


        if ((!southern && (startCompare >= 0 && endCompare < 0)) ||
                (southern && (startCompare >= 0 || endCompare < 0))) {

            result += dstSavings;
        }

        return result;
    }


    private static int compareToRule(int month, int monthLen, int dayOfMonth,
                                     int dayOfWeek, int millis,
                                     int ruleMode, int ruleMonth, int ruleDayOfWeek,
                                     int ruleDay, int ruleMillis) {
        if (month < ruleMonth) return -1;
        else if (month > ruleMonth) return 1;

        int ruleDayOfMonth = 0;
        switch (ruleMode) {
            case DOM_MODE:
                ruleDayOfMonth = ruleDay;
                break;
            case DOW_IN_MONTH_MODE:

                if (ruleDay > 0) {
                    ruleDayOfMonth = 1 + (ruleDay - 1) * 7 +
                            (7 + ruleDayOfWeek - (dayOfWeek - dayOfMonth + 1)) % 7;
                } else {

                    ruleDayOfMonth = monthLen + (ruleDay + 1) * 7 -
                            (7 + (dayOfWeek + monthLen - dayOfMonth) - ruleDayOfWeek) % 7;
                }
                break;
            case DOW_GE_DOM_MODE:
                ruleDayOfMonth = ruleDay +
                        (49 + ruleDayOfWeek - ruleDay - dayOfWeek + dayOfMonth) % 7;
                break;
            case DOW_LE_DOM_MODE:
                ruleDayOfMonth = ruleDay -
                        (49 - ruleDayOfWeek + ruleDay + dayOfWeek - dayOfMonth) % 7;


                break;
        }

        if (dayOfMonth < ruleDayOfMonth) return -1;
        else if (dayOfMonth > ruleDayOfMonth) return 1;

        if (millis < ruleMillis) return -1;
        else if (millis > ruleMillis) return 1;
        else return 0;
    }


    public int getRawOffset() {


        return rawOffset;
    }


    public boolean useDaylightTime() {
        return useDaylight;
    }


    public String getID() {
        return ID;
    }


    public synchronized TimeZone getInstance(String ID) {
        if (ID == null) {
            if (HOME_ID == null) {
                HOME_ID = System.getProperty("com.sun.cldc.util.mini.TimeZoneImpl.timezone");
                if (HOME_ID == null)
                    HOME_ID = "UTC";
            }
            ID = HOME_ID;
        }
        for (int i = 0; i < zones.length; i++) {
            if (zones[i].getID().equals(ID))
                return zones[i];
        }
        return null;
    }


    public synchronized String[] getIDs() {
        if (ids == null) {
            ids = new String[zones.length];
            for (int i = 0; i < zones.length; i++)
                ids[i] = zones[i].getID();
        }
        return ids;
    }


    private String ID;

    static String[] ids = null;


    private int startMonth;


    private int startDay;


    private int startDayOfWeek;


    private int startTime;


    private int endMonth;


    private int endDay;


    private int endDayOfWeek;


    private int endTime;


    private int startYear;


    private int rawOffset;


    private boolean useDaylight = false;

    private static final int millisPerHour = 60 * 60 * 1000;
    private static final int millisPerDay = 24 * millisPerHour;


    private final byte monthLength[] = staticMonthLength;
    private final static byte staticMonthLength[] = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};


    private int startMode;


    private int endMode;


    private int dstSavings;


    private static final int DOM_MODE = 1;
    private static final int DOW_IN_MONTH_MODE = 2;
    private static final int DOW_GE_DOM_MODE = 3;
    private static final int DOW_LE_DOM_MODE = 4;


    private void decodeRules() {
        decodeStartRule();
        decodeEndRule();
    }


    private void decodeStartRule() {
        useDaylight = (startDay != 0) && (endDay != 0);
        if (startDay != 0) {
            if (startMonth < Calendar.JANUARY || startMonth > Calendar.DECEMBER) {
                throw new IllegalArgumentException(
                        "Illegal start month " + startMonth);
            }
            if (startTime < 0 || startTime > millisPerDay) {
                throw new IllegalArgumentException(
                        "Illegal start time " + startTime);
            }
            if (startDayOfWeek == 0) {
                startMode = DOM_MODE;
            } else {
                if (startDayOfWeek > 0) {
                    startMode = DOW_IN_MONTH_MODE;
                } else {
                    startDayOfWeek = -startDayOfWeek;
                    if (startDay > 0) {
                        startMode = DOW_GE_DOM_MODE;
                    } else {
                        startDay = -startDay;
                        startMode = DOW_LE_DOM_MODE;
                    }
                }
                if (startDayOfWeek > Calendar.SATURDAY) {
                    throw new IllegalArgumentException(
                            "Illegal start day of week " + startDayOfWeek);
                }
            }
            if (startMode == DOW_IN_MONTH_MODE) {
                if (startDay < -5 || startDay > 5) {
                    throw new IllegalArgumentException(
                            "Illegal start day of week in month " + startDay);
                }
            } else if (startDay > staticMonthLength[startMonth]) {
                throw new IllegalArgumentException(
                        "Illegal start day " + startDay);
            }
        }
    }


    private void decodeEndRule() {
        useDaylight = (startDay != 0) && (endDay != 0);
        if (endDay != 0) {
            if (endMonth < Calendar.JANUARY || endMonth > Calendar.DECEMBER) {
                throw new IllegalArgumentException(
                        "Illegal end month " + endMonth);
            }
            if (endTime < 0 || endTime > millisPerDay) {
                throw new IllegalArgumentException(
                        "Illegal end time " + endTime);
            }
            if (endDayOfWeek == 0) {
                endMode = DOM_MODE;
            } else {
                if (endDayOfWeek > 0) {
                    endMode = DOW_IN_MONTH_MODE;
                } else {
                    endDayOfWeek = -endDayOfWeek;
                    if (endDay > 0) {
                        endMode = DOW_GE_DOM_MODE;
                    } else {
                        endDay = -endDay;
                        endMode = DOW_LE_DOM_MODE;
                    }
                }
                if (endDayOfWeek > Calendar.SATURDAY) {
                    throw new IllegalArgumentException(
                            "Illegal end day of week " + endDayOfWeek);
                }
            }
            if (endMode == DOW_IN_MONTH_MODE) {
                if (endDay < -5 || endDay > 5) {
                    throw new IllegalArgumentException(
                            "Illegal end day of week in month " + endDay);
                }
            } else if (endDay > staticMonthLength[endMonth]) {
                throw new IllegalArgumentException(
                        "Illegal end day " + endDay);
            }
        }
    }

    static TimeZone zones[] = {


            new TimeZoneImpl(0 * ONE_HOUR, "GMT"),

            new TimeZoneImpl(0 * ONE_HOUR, "UTC"),


            new TimeZoneImpl(-10 * ONE_HOUR, "America/Adak",
                    Calendar.APRIL, 1, -Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(-9 * ONE_HOUR, "America/Anchorage",
                    Calendar.APRIL, 1, -Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(-9 * ONE_HOUR, "AST",
                    Calendar.APRIL, 1, -Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),

            new TimeZoneImpl(-8 * ONE_HOUR, "America/Vancouver",
                    Calendar.APRIL, 1, -Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(-8 * ONE_HOUR, "America/Tijuana",
                    Calendar.APRIL, 1, -Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(-8 * ONE_HOUR, "America/Los_Angeles",
                    Calendar.APRIL, 1, -Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(-8 * ONE_HOUR, "PST",
                    Calendar.APRIL, 1, -Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),

            new TimeZoneImpl(-7 * ONE_HOUR, "America/Dawson_Creek"),


            new TimeZoneImpl(-7 * ONE_HOUR, "America/Phoenix"),

            new TimeZoneImpl(-7 * ONE_HOUR, "PNT"),

            new TimeZoneImpl(-7 * ONE_HOUR, "America/Edmonton",
                    Calendar.APRIL, 1, -Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(-7 * ONE_HOUR, "America/Mazatlan",
                    Calendar.APRIL, 1, -Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(-7 * ONE_HOUR, "America/Denver",
                    Calendar.APRIL, 1, -Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(-7 * ONE_HOUR, "MST",
                    Calendar.APRIL, 1, -Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),

            new TimeZoneImpl(-6 * ONE_HOUR, "America/Belize"),


            new TimeZoneImpl(-6 * ONE_HOUR, "America/Regina"),


            new TimeZoneImpl(-6 * ONE_HOUR, "America/Guatemala"),


            new TimeZoneImpl(-6 * ONE_HOUR, "America/Tegucigalpa"),


            new TimeZoneImpl(-6 * ONE_HOUR, "America/El_Salvador"),


            new TimeZoneImpl(-6 * ONE_HOUR, "America/Costa_Rica"),


            new TimeZoneImpl(-6 * ONE_HOUR, "America/Winnipeg",
                    Calendar.APRIL, 1, -Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(-6 * ONE_HOUR, "America/Mexico_City",
                    Calendar.APRIL, 1, -Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(-6 * ONE_HOUR, "America/Chicago",
                    Calendar.APRIL, 1, -Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(-6 * ONE_HOUR, "CST",
                    Calendar.APRIL, 1, -Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),

            new TimeZoneImpl(-5 * ONE_HOUR, "America/Porto_Acre"),


            new TimeZoneImpl(-5 * ONE_HOUR, "America/Bogota"),


            new TimeZoneImpl(-5 * ONE_HOUR, "America/Guayaquil"),


            new TimeZoneImpl(-5 * ONE_HOUR, "America/Jamaica"),


            new TimeZoneImpl(-5 * ONE_HOUR, "America/Cayman"),


            new TimeZoneImpl(-5 * ONE_HOUR, "America/Managua"),


            new TimeZoneImpl(-5 * ONE_HOUR, "America/Panama"),


            new TimeZoneImpl(-5 * ONE_HOUR, "America/Lima"),


            new TimeZoneImpl(-5 * ONE_HOUR, "America/Indianapolis"),

            new TimeZoneImpl(-5 * ONE_HOUR, "IET"),

            new TimeZoneImpl(-5 * ONE_HOUR, "America/Nassau",
                    Calendar.APRIL, 1, -Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(-5 * ONE_HOUR, "America/Montreal",
                    Calendar.APRIL, 1, -Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(-5 * ONE_HOUR, "America/Havana",
                    Calendar.APRIL, 1, -Calendar.SUNDAY, 0 * ONE_HOUR,
                    Calendar.OCTOBER, 8, -Calendar.SUNDAY, 1 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(-5 * ONE_HOUR, "America/Port-au-Prince",
                    Calendar.APRIL, 1, -Calendar.SUNDAY, 1 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(-5 * ONE_HOUR, "America/Grand_Turk",
                    Calendar.APRIL, 1, -Calendar.SUNDAY, 0 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 0 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(-5 * ONE_HOUR, "America/New_York",
                    Calendar.APRIL, 1, -Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(-5 * ONE_HOUR, "EST",
                    Calendar.APRIL, 1, -Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),

            new TimeZoneImpl(-4 * ONE_HOUR, "America/Antigua"),


            new TimeZoneImpl(-4 * ONE_HOUR, "America/Anguilla"),


            new TimeZoneImpl(-4 * ONE_HOUR, "America/Curacao"),


            new TimeZoneImpl(-4 * ONE_HOUR, "America/Aruba"),


            new TimeZoneImpl(-4 * ONE_HOUR, "America/Barbados"),


            new TimeZoneImpl(-4 * ONE_HOUR, "America/La_Paz"),


            new TimeZoneImpl(-4 * ONE_HOUR, "America/Manaus"),


            new TimeZoneImpl(-4 * ONE_HOUR, "America/Dominica"),


            new TimeZoneImpl(-4 * ONE_HOUR, "America/Santo_Domingo"),


            new TimeZoneImpl(-4 * ONE_HOUR, "America/Grenada"),


            new TimeZoneImpl(-4 * ONE_HOUR, "America/Guadeloupe"),


            new TimeZoneImpl(-4 * ONE_HOUR, "America/Guyana"),


            new TimeZoneImpl(-4 * ONE_HOUR, "America/St_Kitts"),


            new TimeZoneImpl(-4 * ONE_HOUR, "America/St_Lucia"),


            new TimeZoneImpl(-4 * ONE_HOUR, "America/Martinique"),


            new TimeZoneImpl(-4 * ONE_HOUR, "America/Montserrat"),


            new TimeZoneImpl(-4 * ONE_HOUR, "America/Puerto_Rico"),

            new TimeZoneImpl(-4 * ONE_HOUR, "PRT"),

            new TimeZoneImpl(-4 * ONE_HOUR, "America/Port_of_Spain"),


            new TimeZoneImpl(-4 * ONE_HOUR, "America/St_Vincent"),


            new TimeZoneImpl(-4 * ONE_HOUR, "America/Tortola"),


            new TimeZoneImpl(-4 * ONE_HOUR, "America/St_Thomas"),


            new TimeZoneImpl(-4 * ONE_HOUR, "America/Caracas"),


            new TimeZoneImpl(-4 * ONE_HOUR, "America/Cuiaba",
                    Calendar.OCTOBER, 1, -Calendar.SUNDAY, 0 * ONE_HOUR,
                    Calendar.FEBRUARY, 11, -Calendar.SUNDAY, 0 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(-4 * ONE_HOUR, "America/Halifax",
                    Calendar.APRIL, 1, -Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(-4 * ONE_HOUR, "America/Thule",
                    Calendar.APRIL, 1, -Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(-4 * ONE_HOUR, "America/Asuncion",
                    Calendar.OCTOBER, 1, 0, 0 * ONE_HOUR,
                    Calendar.MARCH, 1, 0, 0 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(-4 * ONE_HOUR, "America/Santiago",
                    Calendar.OCTOBER, 9, -Calendar.SUNDAY, 0 * ONE_HOUR,
                    Calendar.MARCH, 9, -Calendar.SUNDAY, 0 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl((int) (-3.5 * ONE_HOUR), "America/St_Johns",
                    Calendar.APRIL, 1, -Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl((int) (-3.5 * ONE_HOUR), "CNT",
                    Calendar.APRIL, 1, -Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),

            new TimeZoneImpl(-3 * ONE_HOUR, "America/Fortaleza"),


            new TimeZoneImpl(-3 * ONE_HOUR, "America/Cayenne"),


            new TimeZoneImpl(-3 * ONE_HOUR, "America/Paramaribo"),


            new TimeZoneImpl(-3 * ONE_HOUR, "America/Montevideo"),


            new TimeZoneImpl(-3 * ONE_HOUR, "America/Buenos_Aires"),

            new TimeZoneImpl(-3 * ONE_HOUR, "AGT"),

            new TimeZoneImpl(-3 * ONE_HOUR, "America/Godthab",
                    Calendar.MARCH, -1, Calendar.SATURDAY, 22 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SATURDAY, 22 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(-3 * ONE_HOUR, "America/Miquelon",
                    Calendar.APRIL, 1, -Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(-3 * ONE_HOUR, "America/Sao_Paulo",
                    Calendar.OCTOBER, 1, -Calendar.SUNDAY, 0 * ONE_HOUR,
                    Calendar.FEBRUARY, 11, -Calendar.SUNDAY, 0 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(-3 * ONE_HOUR, "BET",
                    Calendar.OCTOBER, 1, -Calendar.SUNDAY, 0 * ONE_HOUR,
                    Calendar.FEBRUARY, 11, -Calendar.SUNDAY, 0 * ONE_HOUR, 1 * ONE_HOUR),

            new TimeZoneImpl(-2 * ONE_HOUR, "America/Noronha"),


            new TimeZoneImpl(-1 * ONE_HOUR, "America/Scoresbysund",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 0 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 0 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(-4 * ONE_HOUR, "Antarctica/Palmer",
                    Calendar.OCTOBER, 9, -Calendar.SUNDAY, 0 * ONE_HOUR,
                    Calendar.MARCH, 9, -Calendar.SUNDAY, 0 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(6 * ONE_HOUR, "Antarctica/Mawson"),


            new TimeZoneImpl(8 * ONE_HOUR, "Antarctica/Casey"),


            new TimeZoneImpl(10 * ONE_HOUR, "Antarctica/DumontDUrville"),


            new TimeZoneImpl(12 * ONE_HOUR, "Antarctica/McMurdo",
                    Calendar.OCTOBER, 1, -Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.MARCH, 15, -Calendar.SUNDAY, 3 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(8 * ONE_HOUR, "Australia/Perth"),


            new TimeZoneImpl((int) (9.5 * ONE_HOUR), "Australia/Darwin"),

            new TimeZoneImpl((int) (9.5 * ONE_HOUR), "ACT"),

            new TimeZoneImpl((int) (9.5 * ONE_HOUR), "Australia/Adelaide",
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.MARCH, -1, Calendar.SUNDAY, 3 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(10 * ONE_HOUR, "Australia/Brisbane"),


            new TimeZoneImpl(10 * ONE_HOUR, "Australia/Sydney",
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.MARCH, -1, Calendar.SUNDAY, 3 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(10 * ONE_HOUR, "AET",
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.MARCH, -1, Calendar.SUNDAY, 3 * ONE_HOUR, 1 * ONE_HOUR),

            new TimeZoneImpl((int) (10.5 * ONE_HOUR), "Australia/Lord_Howe",
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.MARCH, -1, Calendar.SUNDAY, 3 * ONE_HOUR, (int) (0.5 * ONE_HOUR)),


            new TimeZoneImpl(-4 * ONE_HOUR, "Atlantic/Bermuda",
                    Calendar.APRIL, 1, -Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(-4 * ONE_HOUR, "Atlantic/Stanley",
                    Calendar.SEPTEMBER, 8, -Calendar.SUNDAY, 0 * ONE_HOUR,
                    Calendar.APRIL, 16, -Calendar.SUNDAY, 0 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(-2 * ONE_HOUR, "Atlantic/South_Georgia"),


            new TimeZoneImpl(-1 * ONE_HOUR, "Atlantic/Jan_Mayen"),


            new TimeZoneImpl(-1 * ONE_HOUR, "Atlantic/Cape_Verde"),


            new TimeZoneImpl(-1 * ONE_HOUR, "Atlantic/Azores",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 0 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 0 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(0 * ONE_HOUR, "Atlantic/Reykjavik"),


            new TimeZoneImpl(0 * ONE_HOUR, "Atlantic/Faeroe",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 1 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 1 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(0 * ONE_HOUR, "Atlantic/Canary",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 1 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 1 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(0 * ONE_HOUR, "Africa/Ouagadougou"),


            new TimeZoneImpl(0 * ONE_HOUR, "Africa/Abidjan"),


            new TimeZoneImpl(0 * ONE_HOUR, "Africa/Accra"),


            new TimeZoneImpl(0 * ONE_HOUR, "Africa/Banjul"),


            new TimeZoneImpl(0 * ONE_HOUR, "Africa/Conakry"),


            new TimeZoneImpl(0 * ONE_HOUR, "Africa/Bissau"),


            new TimeZoneImpl(0 * ONE_HOUR, "Africa/Monrovia"),


            new TimeZoneImpl(0 * ONE_HOUR, "Africa/Casablanca"),


            new TimeZoneImpl(0 * ONE_HOUR, "Africa/Timbuktu"),


            new TimeZoneImpl(0 * ONE_HOUR, "Africa/Nouakchott"),


            new TimeZoneImpl(0 * ONE_HOUR, "Atlantic/St_Helena"),


            new TimeZoneImpl(0 * ONE_HOUR, "Africa/Freetown"),


            new TimeZoneImpl(0 * ONE_HOUR, "Africa/Dakar"),


            new TimeZoneImpl(0 * ONE_HOUR, "Africa/Sao_Tome"),


            new TimeZoneImpl(0 * ONE_HOUR, "Africa/Lome"),


            new TimeZoneImpl(1 * ONE_HOUR, "Africa/Luanda"),


            new TimeZoneImpl(1 * ONE_HOUR, "Africa/Porto-Novo"),


            new TimeZoneImpl(1 * ONE_HOUR, "Africa/Bangui"),


            new TimeZoneImpl(1 * ONE_HOUR, "Africa/Kinshasa"),


            new TimeZoneImpl(1 * ONE_HOUR, "Africa/Douala"),


            new TimeZoneImpl(1 * ONE_HOUR, "Africa/Libreville"),


            new TimeZoneImpl(1 * ONE_HOUR, "Africa/Malabo"),


            new TimeZoneImpl(1 * ONE_HOUR, "Africa/Niamey"),


            new TimeZoneImpl(1 * ONE_HOUR, "Africa/Lagos"),


            new TimeZoneImpl(1 * ONE_HOUR, "Africa/Ndjamena"),


            new TimeZoneImpl(1 * ONE_HOUR, "Africa/Tunis"),


            new TimeZoneImpl(1 * ONE_HOUR, "Africa/Algiers"),


            new TimeZoneImpl(1 * ONE_HOUR, "Africa/Tripoli",
                    Calendar.MARCH, -1, Calendar.THURSDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, 1, -Calendar.THURSDAY, 3 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(1 * ONE_HOUR, "Africa/Windhoek",
                    Calendar.SEPTEMBER, 1, -Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.APRIL, 1, -Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(2 * ONE_HOUR, "Africa/Bujumbura"),


            new TimeZoneImpl(2 * ONE_HOUR, "Africa/Gaborone"),


            new TimeZoneImpl(2 * ONE_HOUR, "Africa/Lubumbashi"),


            new TimeZoneImpl(2 * ONE_HOUR, "Africa/Maseru"),


            new TimeZoneImpl(2 * ONE_HOUR, "Africa/Blantyre"),


            new TimeZoneImpl(2 * ONE_HOUR, "Africa/Maputo"),


            new TimeZoneImpl(2 * ONE_HOUR, "Africa/Kigali"),


            new TimeZoneImpl(2 * ONE_HOUR, "Africa/Khartoum"),


            new TimeZoneImpl(2 * ONE_HOUR, "Africa/Mbabane"),


            new TimeZoneImpl(2 * ONE_HOUR, "Africa/Lusaka"),


            new TimeZoneImpl(2 * ONE_HOUR, "Africa/Harare"),

            new TimeZoneImpl(2 * ONE_HOUR, "CAT"),

            new TimeZoneImpl(2 * ONE_HOUR, "Africa/Johannesburg"),


            new TimeZoneImpl(2 * ONE_HOUR, "Africa/Cairo",
                    Calendar.APRIL, -1, Calendar.FRIDAY, 1 * ONE_HOUR,
                    Calendar.SEPTEMBER, -1, Calendar.FRIDAY, 3 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(2 * ONE_HOUR, "ART",
                    Calendar.APRIL, -1, Calendar.FRIDAY, 1 * ONE_HOUR,
                    Calendar.SEPTEMBER, -1, Calendar.FRIDAY, 3 * ONE_HOUR, 1 * ONE_HOUR),

            new TimeZoneImpl(3 * ONE_HOUR, "Africa/Djibouti"),


            new TimeZoneImpl(3 * ONE_HOUR, "Africa/Asmera"),


            new TimeZoneImpl(3 * ONE_HOUR, "Africa/Addis_Ababa"),

            new TimeZoneImpl(3 * ONE_HOUR, "EAT"),

            new TimeZoneImpl(3 * ONE_HOUR, "Africa/Nairobi"),


            new TimeZoneImpl(3 * ONE_HOUR, "Africa/Mogadishu"),


            new TimeZoneImpl(3 * ONE_HOUR, "Africa/Dar_es_Salaam"),


            new TimeZoneImpl(3 * ONE_HOUR, "Africa/Kampala"),


            new TimeZoneImpl(0 * ONE_HOUR, "Europe/Dublin",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 1 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 1 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(0 * ONE_HOUR, "Europe/Lisbon",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 1 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 1 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(0 * ONE_HOUR, "Europe/London",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 1 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 1 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(1 * ONE_HOUR, "Europe/Andorra",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(1 * ONE_HOUR, "Europe/Tirane",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(1 * ONE_HOUR, "Europe/Vienna",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(1 * ONE_HOUR, "Europe/Brussels",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(1 * ONE_HOUR, "Europe/Zurich",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(1 * ONE_HOUR, "Europe/Prague",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(1 * ONE_HOUR, "Europe/Berlin",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(1 * ONE_HOUR, "Europe/Copenhagen",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(1 * ONE_HOUR, "Europe/Madrid",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(1 * ONE_HOUR, "Europe/Gibraltar",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(1 * ONE_HOUR, "Europe/Budapest",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(1 * ONE_HOUR, "Europe/Rome",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(1 * ONE_HOUR, "Europe/Vaduz",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(1 * ONE_HOUR, "Europe/Luxembourg",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(1 * ONE_HOUR, "Europe/Monaco",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(1 * ONE_HOUR, "Europe/Malta",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(1 * ONE_HOUR, "Europe/Amsterdam",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(1 * ONE_HOUR, "Europe/Oslo",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(1 * ONE_HOUR, "Europe/Warsaw",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 1 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(1 * ONE_HOUR, "Europe/Stockholm",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(1 * ONE_HOUR, "Europe/Belgrade",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(1 * ONE_HOUR, "Europe/Paris",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(1 * ONE_HOUR, "ECT",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR, 1 * ONE_HOUR),
            new TimeZoneImpl(2 * ONE_HOUR, "Europe/Sofia",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 0 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 0 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(2 * ONE_HOUR, "Europe/Minsk",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 3 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(2 * ONE_HOUR, "Europe/Tallinn",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 3 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(2 * ONE_HOUR, "Europe/Helsinki",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 3 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 3 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(2 * ONE_HOUR, "Europe/Athens",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 3 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 3 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(2 * ONE_HOUR, "Europe/Vilnius",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 3 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(2 * ONE_HOUR, "Europe/Riga",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.SEPTEMBER, -1, Calendar.SUNDAY, 3 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(2 * ONE_HOUR, "Europe/Chisinau",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 0 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 0 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(2 * ONE_HOUR, "Europe/Bucharest",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 0 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 0 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(2 * ONE_HOUR, "Europe/Kaliningrad",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 3 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(2 * ONE_HOUR, "Europe/Kiev",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 3 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 3 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(2 * ONE_HOUR, "Europe/Istanbul",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 3 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 3 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(2 * ONE_HOUR, "EET",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 3 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 3 * ONE_HOUR, 1 * ONE_HOUR),

            new TimeZoneImpl(3 * ONE_HOUR, "Europe/Simferopol",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 3 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 3 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(3 * ONE_HOUR, "Europe/Moscow",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 3 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(4 * ONE_HOUR, "Europe/Samara",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 3 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(2 * ONE_HOUR, "Asia/Nicosia",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 0 * ONE_HOUR,
                    Calendar.SEPTEMBER, -1, Calendar.SUNDAY, 0 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(2 * ONE_HOUR, "Asia/Jerusalem",
                    Calendar.MARCH, 15, -Calendar.FRIDAY, 0 * ONE_HOUR,
                    Calendar.SEPTEMBER, 1, -Calendar.SUNDAY, 0 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(2 * ONE_HOUR, "Asia/Amman",
                    Calendar.APRIL, 1, -Calendar.FRIDAY, 0 * ONE_HOUR,
                    Calendar.SEPTEMBER, 15, -Calendar.FRIDAY, 1 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(2 * ONE_HOUR, "Asia/Beirut",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 0 * ONE_HOUR,
                    Calendar.SEPTEMBER, -1, Calendar.SUNDAY, 0 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(2 * ONE_HOUR, "Asia/Damascus",
                    Calendar.APRIL, 1, 0, 0 * ONE_HOUR,
                    Calendar.OCTOBER, 1, 0, 0 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(3 * ONE_HOUR, "Asia/Bahrain"),


            new TimeZoneImpl(3 * ONE_HOUR, "Asia/Kuwait"),


            new TimeZoneImpl(3 * ONE_HOUR, "Asia/Qatar"),


            new TimeZoneImpl(3 * ONE_HOUR, "Asia/Aden"),


            new TimeZoneImpl(3 * ONE_HOUR, "Asia/Riyadh"),


            new TimeZoneImpl(3 * ONE_HOUR, "Asia/Baghdad",
                    Calendar.APRIL, 1, 0, 3 * ONE_HOUR,
                    Calendar.OCTOBER, 1, 0, 4 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl((int) (3.5 * ONE_HOUR), "Asia/Tehran",
                    Calendar.MARCH, 21, 0, 0 * ONE_HOUR,
                    Calendar.SEPTEMBER, 23, 0, 0 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl((int) (3.5 * ONE_HOUR), "MET",
                    Calendar.MARCH, 21, 0, 0 * ONE_HOUR,
                    Calendar.SEPTEMBER, 23, 0, 0 * ONE_HOUR, 1 * ONE_HOUR),

            new TimeZoneImpl(4 * ONE_HOUR, "Asia/Dubai"),


            new TimeZoneImpl(4 * ONE_HOUR, "Asia/Muscat"),


            new TimeZoneImpl(4 * ONE_HOUR, "Asia/Yerevan"),

            new TimeZoneImpl(4 * ONE_HOUR, "NET"),

            new TimeZoneImpl(4 * ONE_HOUR, "Asia/Baku",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 5 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 5 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(4 * ONE_HOUR, "Asia/Aqtau",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 0 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 0 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl((int) (4.5 * ONE_HOUR), "Asia/Kabul"),


            new TimeZoneImpl(5 * ONE_HOUR, "Asia/Tbilisi"),


            new TimeZoneImpl(5 * ONE_HOUR, "Asia/Dushanbe"),


            new TimeZoneImpl(5 * ONE_HOUR, "Asia/Ashkhabad"),


            new TimeZoneImpl(5 * ONE_HOUR, "Asia/Tashkent"),


            new TimeZoneImpl(5 * ONE_HOUR, "Asia/Karachi"),

            new TimeZoneImpl(5 * ONE_HOUR, "PLT"),

            new TimeZoneImpl(5 * ONE_HOUR, "Asia/Bishkek",
                    Calendar.APRIL, 7, -Calendar.SUNDAY, 0 * ONE_HOUR,
                    Calendar.SEPTEMBER, -1, Calendar.SUNDAY, 0 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(5 * ONE_HOUR, "Asia/Aqtobe",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 0 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 0 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(5 * ONE_HOUR, "Asia/Yekaterinburg",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 3 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl((int) (5.5 * ONE_HOUR), "Asia/Calcutta"),

            new TimeZoneImpl((int) (5.5 * ONE_HOUR), "IST"),

            new TimeZoneImpl((int) (5.75 * ONE_HOUR), "Asia/Katmandu"),


            new TimeZoneImpl(6 * ONE_HOUR, "Asia/Thimbu"),


            new TimeZoneImpl(6 * ONE_HOUR, "Asia/Colombo"),


            new TimeZoneImpl(6 * ONE_HOUR, "Asia/Dacca"),

            new TimeZoneImpl(6 * ONE_HOUR, "BST"),

            new TimeZoneImpl(6 * ONE_HOUR, "Asia/Alma-Ata",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 0 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 0 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(6 * ONE_HOUR, "Asia/Novosibirsk",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 3 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl((int) (6.5 * ONE_HOUR), "Asia/Rangoon"),


            new TimeZoneImpl(7 * ONE_HOUR, "Asia/Jakarta"),


            new TimeZoneImpl(7 * ONE_HOUR, "Asia/Phnom_Penh"),


            new TimeZoneImpl(7 * ONE_HOUR, "Asia/Vientiane"),


            new TimeZoneImpl(7 * ONE_HOUR, "Asia/Saigon"),

            new TimeZoneImpl(7 * ONE_HOUR, "VST"),

            new TimeZoneImpl(7 * ONE_HOUR, "Asia/Bangkok"),


            new TimeZoneImpl(7 * ONE_HOUR, "Asia/Krasnoyarsk",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 3 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(8 * ONE_HOUR, "Asia/Brunei"),


            new TimeZoneImpl(8 * ONE_HOUR, "Asia/Hong_Kong"),


            new TimeZoneImpl(8 * ONE_HOUR, "Asia/Ujung_Pandang"),


            new TimeZoneImpl(8 * ONE_HOUR, "Asia/Ishigaki"),


            new TimeZoneImpl(8 * ONE_HOUR, "Asia/Macao"),


            new TimeZoneImpl(8 * ONE_HOUR, "Asia/Kuala_Lumpur"),


            new TimeZoneImpl(8 * ONE_HOUR, "Asia/Manila"),


            new TimeZoneImpl(8 * ONE_HOUR, "Asia/Singapore"),


            new TimeZoneImpl(8 * ONE_HOUR, "Asia/Taipei"),


            new TimeZoneImpl(8 * ONE_HOUR, "Asia/Shanghai"),

            new TimeZoneImpl(8 * ONE_HOUR, "CTT"),

            new TimeZoneImpl(8 * ONE_HOUR, "Asia/Ulan_Bator",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 0 * ONE_HOUR,
                    Calendar.SEPTEMBER, -1, Calendar.SUNDAY, 0 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(8 * ONE_HOUR, "Asia/Irkutsk",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 3 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(9 * ONE_HOUR, "Asia/Jayapura"),


            new TimeZoneImpl(9 * ONE_HOUR, "Asia/Pyongyang"),


            new TimeZoneImpl(9 * ONE_HOUR, "Asia/Seoul"),


            new TimeZoneImpl(9 * ONE_HOUR, "Asia/Tokyo"),

            new TimeZoneImpl(9 * ONE_HOUR, "JST"),

            new TimeZoneImpl(9 * ONE_HOUR, "Asia/Yakutsk",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 3 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(10 * ONE_HOUR, "Asia/Vladivostok",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 3 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(11 * ONE_HOUR, "Asia/Magadan",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 3 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(12 * ONE_HOUR, "Asia/Kamchatka",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 3 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(13 * ONE_HOUR, "Asia/Anadyr",
                    Calendar.MARCH, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 3 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(3 * ONE_HOUR, "Indian/Comoro"),


            new TimeZoneImpl(3 * ONE_HOUR, "Indian/Antananarivo"),


            new TimeZoneImpl(3 * ONE_HOUR, "Indian/Mayotte"),


            new TimeZoneImpl(4 * ONE_HOUR, "Indian/Mauritius"),


            new TimeZoneImpl(4 * ONE_HOUR, "Indian/Reunion"),


            new TimeZoneImpl(4 * ONE_HOUR, "Indian/Mahe"),


            new TimeZoneImpl(5 * ONE_HOUR, "Indian/Kerguelen"),


            new TimeZoneImpl(5 * ONE_HOUR, "Indian/Chagos"),


            new TimeZoneImpl(5 * ONE_HOUR, "Indian/Maldives"),


            new TimeZoneImpl((int) (6.5 * ONE_HOUR), "Indian/Cocos"),


            new TimeZoneImpl(7 * ONE_HOUR, "Indian/Christmas"),


            new TimeZoneImpl(9 * ONE_HOUR, "Pacific/Palau"),


            new TimeZoneImpl(10 * ONE_HOUR, "Pacific/Truk"),


            new TimeZoneImpl(10 * ONE_HOUR, "Pacific/Guam"),


            new TimeZoneImpl(10 * ONE_HOUR, "Pacific/Saipan"),


            new TimeZoneImpl(10 * ONE_HOUR, "Pacific/Port_Moresby"),


            new TimeZoneImpl(11 * ONE_HOUR, "Pacific/Ponape"),


            new TimeZoneImpl(11 * ONE_HOUR, "Pacific/Efate"),


            new TimeZoneImpl(11 * ONE_HOUR, "Pacific/Guadalcanal"),

            new TimeZoneImpl(11 * ONE_HOUR, "SST"),

            new TimeZoneImpl(11 * ONE_HOUR, "Pacific/Noumea",
                    Calendar.NOVEMBER, -1, Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.MARCH, 1, -Calendar.SUNDAY, 3 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(-11 * ONE_HOUR, "Pacific/Niue"),


            new TimeZoneImpl(-11 * ONE_HOUR, "Pacific/Apia"),

            new TimeZoneImpl(-11 * ONE_HOUR, "MIT"),

            new TimeZoneImpl(-11 * ONE_HOUR, "Pacific/Pago_Pago"),


            new TimeZoneImpl(-10 * ONE_HOUR, "Pacific/Tahiti"),


            new TimeZoneImpl(-10 * ONE_HOUR, "Pacific/Fakaofo"),


            new TimeZoneImpl(-10 * ONE_HOUR, "Pacific/Honolulu"),

            new TimeZoneImpl(-10 * ONE_HOUR, "HST"),

            new TimeZoneImpl(-10 * ONE_HOUR, "Pacific/Rarotonga",
                    Calendar.OCTOBER, -1, Calendar.SUNDAY, 0 * ONE_HOUR,
                    Calendar.MARCH, 1, -Calendar.SUNDAY, 0 * ONE_HOUR, (int) (0.5 * ONE_HOUR)),


            new TimeZoneImpl((int) (-9.5 * ONE_HOUR), "Pacific/Marquesas"),


            new TimeZoneImpl(-9 * ONE_HOUR, "Pacific/Gambier"),


            new TimeZoneImpl((int) (-8.5 * ONE_HOUR), "Pacific/Pitcairn"),


            new TimeZoneImpl(-6 * ONE_HOUR, "Pacific/Galapagos"),


            new TimeZoneImpl(-6 * ONE_HOUR, "Pacific/Easter",
                    Calendar.OCTOBER, 9, -Calendar.SUNDAY, 0 * ONE_HOUR,
                    Calendar.MARCH, 9, -Calendar.SUNDAY, 0 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl((int) (11.5 * ONE_HOUR), "Pacific/Norfolk"),


            new TimeZoneImpl(12 * ONE_HOUR, "Pacific/Kosrae"),


            new TimeZoneImpl(12 * ONE_HOUR, "Pacific/Tarawa"),


            new TimeZoneImpl(12 * ONE_HOUR, "Pacific/Majuro"),


            new TimeZoneImpl(12 * ONE_HOUR, "Pacific/Nauru"),


            new TimeZoneImpl(12 * ONE_HOUR, "Pacific/Funafuti"),


            new TimeZoneImpl(12 * ONE_HOUR, "Pacific/Wake"),


            new TimeZoneImpl(12 * ONE_HOUR, "Pacific/Wallis"),


            new TimeZoneImpl(12 * ONE_HOUR, "Pacific/Fiji"),


            new TimeZoneImpl(12 * ONE_HOUR, "Pacific/Auckland",
                    Calendar.OCTOBER, 1, -Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.MARCH, 15, -Calendar.SUNDAY, 3 * ONE_HOUR, 1 * ONE_HOUR),


            new TimeZoneImpl(12 * ONE_HOUR, "NST",
                    Calendar.OCTOBER, 1, -Calendar.SUNDAY, 2 * ONE_HOUR,
                    Calendar.MARCH, 15, -Calendar.SUNDAY, 3 * ONE_HOUR, 1 * ONE_HOUR),

            new TimeZoneImpl((int) (12.75 * ONE_HOUR), "Pacific/Chatham",
                    Calendar.OCTOBER, 1, -Calendar.SUNDAY, (int) (2.75 * ONE_HOUR),
                    Calendar.MARCH, 15, -Calendar.SUNDAY, (int) (3.75 * ONE_HOUR), 1 * ONE_HOUR),


            new TimeZoneImpl(13 * ONE_HOUR, "Pacific/Enderbury"),


            new TimeZoneImpl(13 * ONE_HOUR, "Pacific/Tongatapu"),


            new TimeZoneImpl(14 * ONE_HOUR, "Pacific/Kiritimati"),


    };
}

