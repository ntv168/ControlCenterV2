package center.control.system.vash.controlcenter.configuration;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import center.control.system.vash.controlcenter.command.CommandEntity;

/**
 * Created by Thuans on 7/2/2017.
 */

public class StateEntity {
    private int id;
    private int delaySec;
    private int duringSec;
    private List<CommandEntity> commands;
    private String nextEvIds;
    private String name;
    private String noticePattern;
    private List<EventEntity> events;
    public StateEntity(){
        this.commands = new ArrayList<>();
        this.events = new ArrayList<>();
    }

    public String getNextEvIds() {
        return nextEvIds;
    }

    public void setNextEvIds(String nextEvIds) {
        this.nextEvIds = nextEvIds;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDelaySec() {
        return delaySec;
    }

    public void setDelaySec(int delaySec) {
        this.delaySec = delaySec;
    }

    public int getDuringSec() {
        return duringSec;
    }

    public void setDuringSec(int duringSec) {
        this.duringSec = duringSec;
    }

    public List<CommandEntity> getCommands() {
        return commands;
    }

    public void setCommands(List<CommandEntity> commands) {
        this.commands = commands;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNoticePattern() {
        return noticePattern;
    }

    public void setNoticePattern(String noticePattern) {
        this.noticePattern = noticePattern;
    }

    public List<EventEntity> getEvents() {
        return events;
    }

    public void setEvents(List<EventEntity> events) {
        this.events = events;
    }

    public void addEvent(EventEntity... evs) {
        for (EventEntity ev: evs){
            Log.d("STATEEE Envti",ev.getSenName());
            this.events.add(ev);
        }
    }
}
