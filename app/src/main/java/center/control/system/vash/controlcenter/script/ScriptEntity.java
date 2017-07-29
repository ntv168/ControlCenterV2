package center.control.system.vash.controlcenter.script;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import center.control.system.vash.controlcenter.device.TargetObject;

/**
 * Created by Thuans on 5/29/2017.
 */

public class ScriptEntity extends TargetObject{
    public static String[] weekDays = {"T2","T3","T4","T5","T6","T7","CN"};
    public static int[] weekDayValue = {Calendar.MONDAY,Calendar.TUESDAY,Calendar.WEDNESDAY,
            Calendar.THURSDAY,Calendar.FRIDAY,Calendar.SATURDAY,Calendar.SUNDAY};

    @SerializedName("hour")
    private int hour;
    @SerializedName("minute")
    private int minute;
    @SerializedName("weekDay")
    private String weekDay;
    private Set<Integer> weeksDay;
    private boolean onlyOneTime;
    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setOnlyOneTime(boolean onlyOneTime) {
        this.onlyOneTime = onlyOneTime;
    }

    public boolean isOnlyOneTime() {
        return onlyOneTime;
    }

    private double detectScore;

    public double getDetectScore() {
        return detectScore;
    }

    public void setDetectScore(double detectScore) {
        this.detectScore = detectScore;
    }



    public void setWeeksDay(String weekDay) {
        this.weekDay = weekDay;
        if (weekDay != null) {
            this.weeksDay =  new HashSet(Arrays.asList(weekDay.split(",")));
        }
    }

    public String getWeekDay() {
        return weekDay;
    }

    public Set<Integer> getWeeksDay() {
        return weeksDay;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
