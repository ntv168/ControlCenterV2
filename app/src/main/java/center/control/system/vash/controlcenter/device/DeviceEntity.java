package center.control.system.vash.controlcenter.device;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Thuans on 5/28/2017.
 */

public class DeviceEntity extends TargetObject{
    public static final String [] types = new  String[]{"light","bell","door","fan","camera","tivi","airCondition","cooker","curtain"};
    public static final String [] typeNames = new  String[]{"đèn","chuông","cửa","quạt","camera","ti-vi","máy lạnh","nồi cơm","rèm cửa"};
    public static final String remoteTypes = new  String("tivi,airCondition");

    @SerializedName("port")
    private String port;
    @SerializedName("type")
    private String type;
    @SerializedName("attributeType")
    private String attributeType;
    @SerializedName("state")
    private String state;
    @SerializedName("areaId")
    private int areaId;

    Set<String> attributeSet;


    public String getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(String attributeType) {
        this.attributeType = attributeType;
        if (attributeType != null) {
            attributeSet = new HashSet(Arrays.asList(attributeType.split(",")));
        }
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
