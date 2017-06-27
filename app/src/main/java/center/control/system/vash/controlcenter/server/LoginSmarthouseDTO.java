package center.control.system.vash.controlcenter.server;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

/**
 * Created by MYNVTSE61526 on 10/06/2017.
 */
public class LoginSmarthouseDTO {
    @SerializedName("houseId")
    @Expose
    private String houseId;
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

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    public String getStaticAddress() {
        return staticAddress;
    }

    public void setStaticAddress(String staticAddress) {
        this.staticAddress = staticAddress;
    }

    public String getContractCode() {
        return contractCode;
    }

    public void setContractCode(String contractCode) {
        this.contractCode = contractCode;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerAddress() {
        return ownerAddress;
    }

    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

    public String getOwnerTel() {
        return ownerTel;
    }

    public void setOwnerTel(String ownerTel) {
        this.ownerTel = ownerTel;
    }

    public String getOwnerCmnd() {
        return ownerCmnd;
    }

    public void setOwnerCmnd(String ownerCmnd) {
        this.ownerCmnd = ownerCmnd;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getActiveDay() {
        return activeDay;
    }

    public void setActiveDay(String activeDay) {
        this.activeDay = activeDay;
    }

    public String getVirtualAssistantName() {
        return virtualAssistantName;
    }

    public void setVirtualAssistantName(String virtualAssistantName) {
        this.virtualAssistantName = virtualAssistantName;
    }

    public String getVirtualAssistantType() {
        return virtualAssistantType;
    }

    public void setVirtualAssistantType(String virtualAssistantType) {
        this.virtualAssistantType = virtualAssistantType;
    }

    public int getVirtualAssistantTypeId() {
        return virtualAssistantTypeId;
    }

    public void setVirtualAssistantTypeId(int virtualAssistantTypeId) {
        this.virtualAssistantTypeId = virtualAssistantTypeId;
    }
}
