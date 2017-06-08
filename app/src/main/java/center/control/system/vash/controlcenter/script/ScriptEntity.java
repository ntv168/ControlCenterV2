package center.control.system.vash.controlcenter.script;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Thuans on 5/29/2017.
 */

public class ScriptEntity {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("nickName")
    private String nickName;

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
}
