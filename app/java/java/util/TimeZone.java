


package java.util;


public abstract class TimeZone {

    private static TimeZoneImpl defaultZone = null;
    private static String platform = null;
    private static String classRoot = null;

    public TimeZone() {
    }


    public abstract int getOffset(int era, int year, int month, int day,
                                  int dayOfWeek, int millis);


    public abstract int getRawOffset();


    public abstract boolean useDaylightTime();


    public String getID() {
        return null;
    }


    public static synchronized TimeZone getTimeZone(String ID) {
        if (ID == null) {
            throw new NullPointerException();
        }
        getDefault();
        TimeZone tz = defaultZone.getInstance(ID);
        if (tz == null) {
            tz = defaultZone.getInstance("GMT");
        }
        return tz;
    }


    public static synchronized TimeZone getDefault() {
        if (defaultZone == null) {
            try {
                Class clazz = Class.forName("java.util.TimeZoneImpl");

                defaultZone = (TimeZoneImpl) clazz.newInstance();
                defaultZone = (TimeZoneImpl) defaultZone.getInstance(null);
            } catch (Exception x) {
            }
        }
        return defaultZone;
    }


    public static String[] getAvailableIDs() {
        getDefault();
        return defaultZone.getIDs();
    }

    public String getDisplayName() {
        return getID();
    }
}

