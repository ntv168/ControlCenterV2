package center.control.system.vash.controlcenter.device;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Thuans on 5/28/2017.
 */

public class DeviceEntity {
    public static final String [] types = new  String[]{"light","bell","door","fan","camera","tivi","airCondition","cooker","curtain"};
    public static final String [] typeNames = new  String[]{"đèn","chuông","cửa","quạt","camera","ti-vi","máy lạnh","nồi cơm","rèm cửa"};
    @SerializedName("id")
    private int id;
    @SerializedName("port")
    private String port;
    @SerializedName("name")
    private String name;
    @SerializedName("type")
    private String type;
    @SerializedName("attributeType")
    private String attributeType;
    @SerializedName("iconId")
    private String iconId;
    @SerializedName("state")
    private String state;
    @SerializedName("areaId")
    private int areaId;
    @SerializedName("nickName")
    private String nickName;

    Set<String> attributeSet;

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

    public String getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(String attributeType) {
        this.attributeType = attributeType;
        if (attributeType != null) {
            attributeSet = new HashSet(Arrays.asList(attributeType.split(",")));
        }
    }

    public String getIconId() {
        return iconId;
    }

    public void setIconId(String iconId) {
        this.iconId = iconId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
