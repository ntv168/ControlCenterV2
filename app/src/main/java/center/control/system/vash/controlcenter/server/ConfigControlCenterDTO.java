package center.control.system.vash.controlcenter.server;

import java.util.List;

/**
 * Created by MYNVTSE61526 on 08/07/2017.
 */
public class ConfigControlCenterDTO {
    private List<StateDTO> states;
    private List<EventDTO> events;

    public List<EventDTO> getEvents() {
        return events;
    }

    public void setEvents(List<EventDTO> events) {
        this.events = events;
    }

    public void setStates(List<StateDTO> states) {
        this.states = states;
    }

    public List<StateDTO> getStates() {
        return states;
    }
}
