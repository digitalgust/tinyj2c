


package java.util;

public abstract class Calendar {

    public final static int YEAR = 1;

    public final static int MONTH = 2;

    public final static int DATE = 5;

    public final static int DAY_OF_MONTH = 5;

    public final static int DAY_OF_WEEK = 7;

    public final static int AM_PM = 9;

    public final static int HOUR = 10;

    public final static int HOUR_OF_DAY = 11;

    public final static int MINUTE = 12;

    public final static int SECOND = 13;

    public final static int MILLISECOND = 14;

    public final static int SUNDAY = 1;

    public final static int MONDAY = 2;

    public final static int TUESDAY = 3;

    public final static int WEDNESDAY = 4;

    public final static int THURSDAY = 5;

    public final static int FRIDAY = 6;

    public final static int SATURDAY = 7;

    public final static int JANUARY = 0;

    public final static int FEBRUARY = 1;

    public final static int MARCH = 2;

    public final static int APRIL = 3;

    public final static int MAY = 4;

    public final static int JUNE = 5;

    public final static int JULY = 6;

    public final static int AUGUST = 7;

    public final static int SEPTEMBER = 8;

    public final static int OCTOBER = 9;

    public final static int NOVEMBER = 10;

    public final static int DECEMBER = 11;

    public final static int AM = 0;

    public final static int PM = 1;

    private final static int FIELDS = 15;


    protected int fields[];


    protected boolean isSet[];


    protected long time;


    private boolean isTimeSet;


    private TimeZone zone;

    private Date dateObj = null;


    protected Calendar() {
        fields = new int[FIELDS];
        isSet = new boolean[FIELDS];

        zone = TimeZone.getDefault();
        if (zone == null) {
            throw new RuntimeException("Could not find default timezone");
        }
        setTimeInMillis(System.currentTimeMillis());
    }


    public final Date getTime() {
        if (dateObj == null) {
            return dateObj = new Date(getTimeInMillis());
        } else {
            synchronized (dateObj) {
                dateObj.setTime(getTimeInMillis());
                return dateObj;
            }
        }
    }


    public final void setTime(Date date) {
        setTimeInMillis(date.getTime());
    }


    public static synchronized Calendar getInstance() {
        try {

            Class clazz = Class.forName("java.util.CalendarImpl");
            return (Calendar) clazz.newInstance();
        } catch (Exception x) {
        }
        return null;
    }


    public static synchronized Calendar getInstance(TimeZone zone) {
        Calendar cal = getInstance();
        cal.setTimeZone(zone);
        return cal;
    }


    public long getTimeInMillis() {
        if (!isTimeSet) {
            computeTime();
            isTimeSet = true;
        }
        return this.time;
    }


    public void setTimeInMillis(long millis) {
        isTimeSet = true;
        this.fields[DAY_OF_WEEK] = 0;
        this.time = millis;
        computeFields();
    }


    public final int get(int field) {
        if (field == DAY_OF_WEEK ||
                field == HOUR_OF_DAY ||
                field == AM_PM ||
                field == HOUR) {
            getTimeInMillis();
            computeFields();
        }
        return this.fields[field];
    }


    public final void set(int field, int value) {
        isTimeSet = false;

        this.isSet[field] = true;
        this.fields[field] = value;
    }


    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Calendar)) {
            return false;
        }

        Calendar that = (Calendar) obj;
        return getTimeInMillis() == that.getTimeInMillis() && zone.equals(that.zone);
    }


    public boolean before(Object when) {
        return (when instanceof Calendar
                && getTimeInMillis() < ((Calendar) when).getTimeInMillis());
    }


    public boolean after(Object when) {
        return (when instanceof Calendar
                && getTimeInMillis() > ((Calendar) when).getTimeInMillis());
    }


    public void setTimeZone(TimeZone value) {
        zone = value;
        getTimeInMillis();
        computeFields();
    }


    public TimeZone getTimeZone() {
        return zone;
    }


    protected abstract void computeFields();


    protected abstract void computeTime();
}

