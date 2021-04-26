package java.util;


class CalendarImpl extends Calendar {


    private static final int BC = 0;
    private static final int AD = 1;


    private static final int JAN_1_1_JULIAN_DAY = 1721426;


    private static final int EPOCH_JULIAN_DAY = 2440588;


    private static final int NUM_DAYS[]
            = {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};


    private static final int LEAP_NUM_DAYS[]
            = {0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335};


    private static final int ONE_SECOND = 1000;
    private static final int ONE_MINUTE = 60 * ONE_SECOND;
    private static final int ONE_HOUR = 60 * ONE_MINUTE;
    private static final long ONE_DAY = 24 * ONE_HOUR;
    private static final long ONE_WEEK = 7 * ONE_DAY;


    private static final long gregorianCutover = -12219292800000L;


    private static final int gregorianCutoverYear = 1582;

    public CalendarImpl() {
        super();
    }


    protected void computeFields() {
        int rawOffset = getTimeZone().getRawOffset();
        long localMillis = time + rawOffset;


        if (time > 0 && localMillis < 0 && rawOffset > 0) {
            localMillis = Long.MAX_VALUE;
        } else if (time < 0 && localMillis > 0 && rawOffset < 0) {
            localMillis = Long.MIN_VALUE;
        }


        timeToFields(localMillis);

        long days = (long) (localMillis / ONE_DAY);
        int millisInDay = (int) (localMillis - (days * ONE_DAY));

        if (millisInDay < 0) millisInDay += ONE_DAY;


        int dstOffset = getTimeZone().getOffset(AD,
                this.fields[YEAR],
                this.fields[MONTH],
                this.fields[DATE],
                this.fields[DAY_OF_WEEK],
                millisInDay) - rawOffset;


        millisInDay += dstOffset;


        if (millisInDay >= ONE_DAY) {
            long dstMillis = localMillis + dstOffset;
            millisInDay -= ONE_DAY;

            if (localMillis > 0 && dstMillis < 0 && dstOffset > 0) {
                dstMillis = Long.MAX_VALUE;
            } else if (localMillis < 0 && dstMillis > 0 && dstOffset < 0) {
                dstMillis = Long.MIN_VALUE;
            }
            timeToFields(dstMillis);
        }


        this.fields[MILLISECOND] = millisInDay % 1000;
        millisInDay /= 1000;

        this.fields[SECOND] = millisInDay % 60;
        millisInDay /= 60;

        this.fields[MINUTE] = millisInDay % 60;
        millisInDay /= 60;

        this.fields[HOUR_OF_DAY] = millisInDay;
        this.fields[AM_PM] = millisInDay / 12;
        this.fields[HOUR] = millisInDay % 12;
    }


    private final void timeToFields(long theTime) {
        int dayOfYear, weekCount, rawYear;
        boolean isLeap;


        if (theTime >= gregorianCutover) {


            long gregorianEpochDay =
                    millisToJulianDay(theTime) - JAN_1_1_JULIAN_DAY;


            int[] rem = new int[1];


            int n400 = floorDivide(gregorianEpochDay, 146097, rem);


            int n100 = floorDivide(rem[0], 36524, rem);


            int n4 = floorDivide(rem[0], 1461, rem);

            int n1 = floorDivide(rem[0], 365, rem);
            rawYear = 400 * n400 + 100 * n100 + 4 * n4 + n1;


            dayOfYear = rem[0];


            if (n100 == 4 || n1 == 4) {
                dayOfYear = 365;
            } else {
                ++rawYear;
            }


            isLeap =
                    ((rawYear & 0x3) == 0) && (rawYear % 100 != 0 || rawYear % 400 == 0);


            this.fields[DAY_OF_WEEK] = (int) ((gregorianEpochDay + 1) % 7);
        } else {


            long julianEpochDay =
                    millisToJulianDay(theTime) - (JAN_1_1_JULIAN_DAY - 2);

            rawYear = (int) floorDivide(4 * julianEpochDay + 1464, 1461);


            long january1 = 365 * (rawYear - 1) + floorDivide(rawYear - 1, 4);
            dayOfYear = (int) (julianEpochDay - january1);


            isLeap = ((rawYear & 0x3) == 0);


            this.fields[DAY_OF_WEEK] = (int) ((julianEpochDay - 1) % 7);
        }


        int correction = 0;


        int march1 = isLeap ? 60 : 59;

        if (dayOfYear >= march1) correction = isLeap ? 1 : 2;


        int month_field = (12 * (dayOfYear + correction) + 6) / 367;


        int date_field = dayOfYear -
                (isLeap ? LEAP_NUM_DAYS[month_field] : NUM_DAYS[month_field]) + 1;


        this.fields[DAY_OF_WEEK] += (this.fields[DAY_OF_WEEK] < 0) ? (SUNDAY + 7) : SUNDAY;

        this.fields[YEAR] = rawYear;


        if (this.fields[YEAR] < 1) {
            this.fields[YEAR] = 1 - this.fields[YEAR];
        }


        this.fields[MONTH] = month_field + JANUARY;
        this.fields[DATE] = date_field;
    }


    static String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    static String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};


    public static String toString(Calendar calendar) {


        if (calendar == null) {
            return "Thu Jan 01 00:00:00 UTC 1970";
        }

        int dow = calendar.get(Calendar.DAY_OF_WEEK);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour_of_day = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        int year = calendar.get(Calendar.YEAR);

        String yr = Integer.toString(year);

        TimeZone zone = calendar.getTimeZone();
        String zoneID = zone.getID();
        if (zoneID == null) zoneID = "";


        StringBuffer sb = new StringBuffer(25 + zoneID.length() + yr.length());

        sb.append(days[dow - 1]).append(' ');
        sb.append(months[month]).append(' ');
        appendTwoDigits(sb, day).append(' ');
        appendTwoDigits(sb, hour_of_day).append(':');
        appendTwoDigits(sb, minute).append(':');
        appendTwoDigits(sb, seconds).append(' ');
        if (zoneID.length() > 0) sb.append(zoneID).append(' ');
        appendFourDigits(sb, year);

        return sb.toString();
    }


    public static String toISO8601String(Calendar calendar) {


        if (calendar == null) {
            return "0000 00 00 00 00 00 +0000";
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour_of_day = calendar.get(Calendar.HOUR_OF_DAY);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);

        String yr = Integer.toString(year);


        StringBuffer sb = new StringBuffer(25 + yr.length());

        appendFourDigits(sb, year).append(' ');
        appendTwoDigits(sb, month).append(' ');
        appendTwoDigits(sb, day).append(' ');
        appendTwoDigits(sb, hour_of_day).append(' ');
        appendTwoDigits(sb, minute).append(' ');
        appendTwoDigits(sb, seconds).append(' ');


        TimeZone t = calendar.getTimeZone();
        int zoneOffsetInMinutes = t.getRawOffset() / 1000 / 60;

        if (zoneOffsetInMinutes < 0) {
            zoneOffsetInMinutes = Math.abs(zoneOffsetInMinutes);
            sb.append('-');
        } else {
            sb.append('+');
        }

        int zoneHours = zoneOffsetInMinutes / 60;
        int zoneMinutes = zoneOffsetInMinutes % 60;

        appendTwoDigits(sb, zoneHours);
        appendTwoDigits(sb, zoneMinutes);

        return sb.toString();
    }

    private static final StringBuffer appendFourDigits(StringBuffer sb, int number) {
        if (number >= 0 && number < 1000) {
            sb.append('0');
            if (number < 100) {
                sb.append('0');
            }
            if (number < 10) {
                sb.append('0');
            }
        }
        return sb.append(number);
    }

    private static final StringBuffer appendTwoDigits(StringBuffer sb, int number) {
        if (number < 10) {
            sb.append('0');
        }
        return sb.append(number);
    }


    protected void computeTime() {

        correctTime();


        int year = this.fields[YEAR];
        boolean isGregorian = year >= gregorianCutoverYear;
        long julianDay = calculateJulianDay(isGregorian, year);
        long millis = julianDayToMillis(julianDay);


        if (isGregorian != (millis >= gregorianCutover) &&
                julianDay != -106749550580L) {

            julianDay = calculateJulianDay(!isGregorian, year);
            millis = julianDayToMillis(julianDay);
        }


        int millisInDay = 0;


        millisInDay += this.fields[HOUR_OF_DAY];
        millisInDay *= 60;


        millisInDay += this.fields[MINUTE];
        millisInDay *= 60;


        millisInDay += this.fields[SECOND];
        millisInDay *= 1000;


        millisInDay += this.fields[MILLISECOND];


        int zoneOffset = getTimeZone().getRawOffset();


        millis += millisInDay;


        int[] normalizedMillisInDay = new int[1];
        floorDivide(millis, (int) ONE_DAY, normalizedMillisInDay);


        int dow = julianDayToDayOfWeek(julianDay);


        int dstOffset = getTimeZone().getOffset(AD,
                this.fields[YEAR],
                this.fields[MONTH],
                this.fields[DATE],
                dow,
                normalizedMillisInDay[0]) -
                zoneOffset;


        time = millis - zoneOffset - dstOffset;
    }


    private final long calculateJulianDay(boolean isGregorian, int year) {
        int month = 0;
        long millis = 0;

        month = this.fields[MONTH] - JANUARY;


        if (month < 0 || month > 11) {
            int[] rem = new int[1];
            year += floorDivide(month, 12, rem);
            month = rem[0];
        }

        boolean isLeap = year % 4 == 0;

        long julianDay =
                365L * (year - 1) + floorDivide((year - 1), 4) + (JAN_1_1_JULIAN_DAY - 3);

        if (isGregorian) {
            isLeap = isLeap && ((year % 100 != 0) || (year % 400 == 0));

            julianDay +=
                    floorDivide((year - 1), 400) - floorDivide((year - 1), 100) + 2;
        }


        julianDay += isLeap ? LEAP_NUM_DAYS[month] : NUM_DAYS[month];
        julianDay += this.fields[DATE];
        return julianDay;
    }


    private void correctTime() {
        int value;

        if (isSet[HOUR_OF_DAY]) {
            value = this.fields[HOUR_OF_DAY] % 24;
            this.fields[HOUR_OF_DAY] = value;
            this.fields[AM_PM] = (value < 12) ? AM : PM;
            this.isSet[HOUR_OF_DAY] = false;
            return;
        }

        if (isSet[AM_PM]) {


            if (this.fields[AM_PM] != AM && this.fields[AM_PM] != PM) {
                value = this.fields[HOUR_OF_DAY];
                this.fields[AM_PM] = (value < 12) ? AM : PM;
            }
            this.isSet[AM_PM] = false;
        }

        if (isSet[HOUR]) {
            value = this.fields[HOUR];
            if (value > 12) {
                this.fields[HOUR_OF_DAY] = (value % 12) + 12;
                this.fields[HOUR] = value % 12;
                this.fields[AM_PM] = PM;
            } else {
                if (this.fields[AM_PM] == PM) {
                    this.fields[HOUR_OF_DAY] = value + 12;
                } else {
                    this.fields[HOUR_OF_DAY] = value;
                }
            }
            this.isSet[HOUR] = false;
        }
    }


    private static final long millisToJulianDay(long millis) {
        return EPOCH_JULIAN_DAY + floorDivide(millis, ONE_DAY);
    }


    private static final long julianDayToMillis(long julian) {
        return (julian - EPOCH_JULIAN_DAY) * ONE_DAY;
    }

    private static final int julianDayToDayOfWeek(long julian) {


        int dayOfWeek = (int) ((julian + 1) % 7);
        return dayOfWeek + ((dayOfWeek < 0) ? (7 + SUNDAY) : SUNDAY);
    }


    private static final long floorDivide(long numerator, long denominator) {


        return (numerator >= 0) ?
                numerator / denominator :
                ((numerator + 1) / denominator) - 1;
    }


    private static final int floorDivide(int numerator, int denominator) {


        return (numerator >= 0) ?
                numerator / denominator :
                ((numerator + 1) / denominator) - 1;
    }


    private static final int
    floorDivide(int numerator, int denominator, int[] remainder) {

        if (numerator >= 0) {
            remainder[0] = numerator % denominator;
            return numerator / denominator;
        }
        int quotient = ((numerator + 1) / denominator) - 1;
        remainder[0] = numerator - (quotient * denominator);
        return quotient;
    }


    private static final int
    floorDivide(long numerator, int denominator, int[] remainder) {

        if (numerator >= 0) {
            remainder[0] = (int) (numerator % denominator);
            return (int) (numerator / denominator);
        }
        int quotient = (int) (((numerator + 1) / denominator) - 1);
        remainder[0] = (int) (numerator - (quotient * denominator));
        return quotient;
    }
}

