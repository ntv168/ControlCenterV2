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
    @SerializedName("offlineAddress")
    @Expose
    private String offlineAddress;
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
    @SerializedName("newPassword")
    @Expose
    private String newPassword;
    @SerializedName("oldPassword")
    @Expose
    private String oldPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setStaticAddress(String staticAddress) {
        this.staticAddress = staticAddress;
    }

    public void setContractCode(String contractCode) {
        this.contractCode = contractCode;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

    public void setOwnerTel(String ownerTel) {
        this.ownerTel = ownerTel;
    }

    public void setOwnerCmnd(String ownerCmnd) {
        this.ownerCmnd = ownerCmnd;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public void setActiveDay(String activeDay) {
        this.activeDay = activeDay;
    }

    public void setVirtualAssistantName(String virtualAssistantName) {
        this.virtualAssistantName = virtualAssistantName;
    }

    public void setVirtualAssistantType(String virtualAssistantType) {
        this.virtualAssistantType = virtualAssistantType;
    }

    public void setVirtualAssistantTypeId(int virtualAssistantTypeId) {
        this.virtualAssistantTypeId = virtualAssistantTypeId;
    }

    public String getOfflineAddress() {
        return offlineAddress;
    }

    public void setOfflineAddress(String offlineAddress) {
        this.offlineAddress = offlineAddress;
    }

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
