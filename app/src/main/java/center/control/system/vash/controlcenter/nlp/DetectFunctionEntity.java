package center.control.system.vash.controlcenter.nlp;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Thuans on 5/30/2017.
 */

public class DetectFunctionEntity {
    @SerializedName("id")
    private int id;
    @SerializedName("functionName")
    private String functionName;
    @SerializedName("successPattern")
    private String successPattern;
    @SerializedName("failPattern")
    private String failPattern;
    @SerializedName("remindPattern")
    private String remindPattern;

    public DetectFunctionEntity(int id, String functionName, String successPattern, String failPattern, String remindPattern) {
        this.id = id;
        this.functionName = functionName;
        this.successPattern = successPattern;
        this.failPattern = failPattern;
        this.remindPattern = remindPattern;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getSuccessPattern() {
        return successPattern;
    }

    public void setSuccessPattern(String successPattern) {
        this.successPattern = successPattern;
    }

    public String getFailPattern() {
        return failPattern;
    }

    public void setFailPattern(String failPattern) {
        this.failPattern = failPattern;
    }

    public String getRemindPattern() {
        return remindPattern;
    }

    public void setRemindPattern(String remindPattern) {
        this.remindPattern = remindPattern;
    }
}
