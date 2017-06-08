package center.control.system.vash.controlcenter.nlp;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Thuans on 5/29/2017.
 */

public class HumanTermEntity {
    @SerializedName("id")
    private int id;
    @SerializedName("content")
    private String content;
    @SerializedName("tfidfPoint")
    private Double tfidfPoint;
    @SerializedName("detectSocialId")
    private int detectSocialId;
    @SerializedName("detectFunctionId")
    private int detectFunctionId;

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

    public int getDetectSocialId() {
        return detectSocialId;
    }

    public void setDetectSocialId(int detectSocialId) {
        this.detectSocialId = detectSocialId;
    }

    public int getDetectFunctionId() {
        return detectFunctionId;
    }

    public void setDetectFunctionId(int detectFunctionId) {
        this.detectFunctionId = detectFunctionId;
    }
}
