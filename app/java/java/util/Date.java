package java.util;


public class Date {


    private Calendar calendar;
    private long fastTime;


    public Date() {
        this(System.currentTimeMillis());
    }


    public Date(long date) {
        calendar = Calendar.getInstance();
        if (calendar != null) {
            calendar.setTimeInMillis(date);
        }
        fastTime = date;
    }


    public long getTime() {
        if (calendar != null) {
            return calendar.getTimeInMillis();
        } else {
            return fastTime;
        }
    }


    public void setTime(long time) {
        if (calendar != null) {
            calendar.setTimeInMillis(time);
        }
        fastTime = time;
    }


    public boolean equals(Object obj) {
        return obj != null && obj instanceof Date && getTime() == ((Date) obj).getTime();
    }


    public int hashCode() {
        long ht = getTime();
        return (int) ht ^ (int) (ht >> 32);
    }


    public String toString() {
        return CalendarImpl.toString(calendar);
    }
}

