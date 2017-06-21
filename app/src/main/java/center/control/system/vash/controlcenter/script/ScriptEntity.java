package center.control.system.vash.controlcenter.script;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Thuans on 5/29/2017.
 */

public class ScriptEntity {
    public static String[] weekDays = {"T2","T3","T4","T5","T6","T7","CN"};
    public static int[] weekDayValue = {Calendar.MONDAY,Calendar.TUESDAY,Calendar.WEDNESDAY,
            Calendar.THURSDAY,Calendar.FRIDAY,Calendar.SATURDAY,Calendar.SUNDAY};
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("nickName")
    private String nickName;
    @SerializedName("hour")
    private int hour;
    @SerializedName("minute")
    private int minute;
    @SerializedName("weekDay")
    private String weekDay;
    Set<Integer> weeksDay;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
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
