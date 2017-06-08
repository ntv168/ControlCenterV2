package center.control.system.vash.controlcenter.nlp;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Thuans on 5/29/2017.
 */

public class TargetTernEntity {
    @SerializedName("id")
    private int id;
    @SerializedName("content")
    private String content;
    @SerializedName("tfidfPoint")
    private Double tfidfPoint;
    @SerializedName("detectDeviceId")
    private int detectDeviceId;
    @SerializedName("detectAreaId")
    private int detectAreaId;
    @SerializedName("detectScriptId")
    private int detectScriptId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Double getTfidfPoint() {
        return tfidfPoint;
    }

    public void setTfidfPoint(Double tfidfPoint) {
        this.tfidfPoint = tfidfPoint;
    }

    public int getDetectDeviceId() {
        return detectDeviceId;
    }

    public void setDetectDeviceId(int detectDeviceId) {
        this.detectDeviceId = detectDeviceId;
    }

    public int getDetectAreaId() {
        return detectAreaId;
    }

    public void setDetectAreaId(int detectAreaId) {
        this.detectAreaId = detectAreaId;
    }

    public int getDetectScriptId() {
        return detectScriptId;
    }

    public void setDetectScriptId(int detectScriptId) {
        this.detectScriptId = detectScriptId;
    }
}
