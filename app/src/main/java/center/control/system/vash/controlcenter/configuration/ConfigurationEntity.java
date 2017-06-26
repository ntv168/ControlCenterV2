package center.control.system.vash.controlcenter.configuration;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import center.control.system.vash.controlcenter.script.CommandEntity;

/**
 * Created by Thuans on 6/23/2017.
 */

public class ConfigurationEntity {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    private List<String> triggerName;
    private List<CommandEntity> commands;

    public List<CommandEntity> getCommands() {
        return commands;
    }

    public void setCommands(List<CommandEntity> commands) {
        this.commands = commands;
    }

    public List<String> getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(List<String> triggerName) {
        this.triggerName = triggerName;
    }

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
}
