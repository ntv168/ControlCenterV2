package center.control.system.vash.controlcenter.server;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

/**
 * Created by MYNVTSE61526 on 10/06/2017.
 */
public class LoginSmarthouseDTO {
    @SerializedName("staticAddress")
    @Expose
    private String staticAddress;
    @SerializedName("contractCode")
    @Expose
    private String contractCode;
    @SerializedName("ownerName")
    @Expose
    private String ownerName;
    @SerializedName("ownerAddress")
    @Expose
    private String ownerAddress;
    @SerializedName("ownerTel")
    @Expose
    private String ownerTel;
    @SerializedName("ownerCmnd")
    @Expose
    private String ownerCmnd;
    @SerializedName("contractId")
    @Expose
    private String contractId;
    @SerializedName("activeDay")
    @Expose
    private String activeDay;
    @SerializedName("virtualAssistantName")
    @Expose
    private String virtualAssistantName;
    @SerializedName("virtualAssistantType")
    @Expose
    private String virtualAssistantType;
    @SerializedName("virtualAssistantTypeId")
    @Expose
    private int virtualAssistantTypeId;


    public String getStaticAddress() {
        return staticAddress;
    }


    public String getContractCode() {
        return contractCode;
    }


    public String getOwnerName() {
        return ownerName;
    }


    public String getOwnerAddress() {
        return ownerAddress;
    }


    public String getOwnerTel() {
        return ownerTel;
    }

    public String getOwnerCmnd() {
        return ownerCmnd;
    }

    public String getContractId() {
        return contractId;
    }


    public String getActiveDay() {
        return activeDay;
    }

    public String getVirtualAssistantName() {
        return virtualAssistantName;
    }

    public String getVirtualAssistantType() {
        return virtualAssistantType;
    }
    public int getVirtualAssistantTypeId() {
        return virtualAssistantTypeId;
    }

}
