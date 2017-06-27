package center.control.system.vash.controlcenter.nlp;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Thuans on 5/30/2017.
 */

public class DetectSocialEntity {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("questionPattern")
    private String questionPattern;
    @SerializedName("replyPattern")
    private String replyPattern;

    public DetectSocialEntity(int id, String name, String questionPattern, String replyPattern) {
        this.id = id;
        this.name = name;
        this.questionPattern = questionPattern;
        this.replyPattern = replyPattern;
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

    public String getQuestionPattern() {
        return questionPattern;
    }

    public void setQuestionPattern(String questionPattern) {
        this.questionPattern = questionPattern;
    }

    public String getReplyPattern() {
        return replyPattern;
    }

    public void setReplyPattern(String replyPattern) {
        this.replyPattern = replyPattern;
    }
}
