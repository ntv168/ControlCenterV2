package center.control.system.vash.controlcenter.server;

/**
 * Created by MYNVTSE61526 on 08/07/2017.
 */
public class StateDTO {
    private int id;
    private int delay;
    private String name;
    private int during;
    private String notification;
    private String nextEvent;
    private int timeoutState;

    public int getTimeoutState() {
        return timeoutState;
    }

    public void setTimeoutState(int timeoutState) {
        this.timeoutState = timeoutState;
    }

    public String getNextEvent() {
        return nextEvent;
    }

    public void setNextEvent(String nextEvent) {
        this.nextEvent = nextEvent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuring() {
        return during;
    }

    public void setDuring(int during) {
        this.during = during;
    }


    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }
}
