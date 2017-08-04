package center.control.system.vash.controlcenter.server;

import java.sql.Date;
import java.util.UUID;

/**
 * Created by MYNVTSE61526 on 11/07/2017.
 */
public class SmartHouseRequestDTO {
    private int id;
    private String contractId;
    private String requestDate;
    private String requestContent;
    private boolean requestHandle;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getRequestContent() {
        return requestContent;
    }

    public void setRequestContent(String requestContent) {
        this.requestContent = requestContent;
    }

    public boolean isRequestHandle() {
        return requestHandle;
    }

    public void setRequestHandle(boolean requestHandle) {
        this.requestHandle = requestHandle;
    }
}
