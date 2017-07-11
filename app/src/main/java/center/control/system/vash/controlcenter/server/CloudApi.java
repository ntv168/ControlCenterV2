package center.control.system.vash.controlcenter.server;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Thuans on 6/15/2017.
 */

public  interface CloudApi {
    @POST("/api/SmartHouse/Login")
    Call<LoginSmarthouseDTO> mobileLogin(@Body HouseKeyDTO customer);
    @GET("/api/VirtualAssistant/getVAData/{botId}")
    Call<BotDataCentralDTO> getDataVA( @Path("botId") int botId);
    @GET("/api/VirtualAssistant/getVAType")
    Call<List<AssistantTypeDTO>> getDataVAType();
    @POST("/api/SmartHouse/StaffCodeLogin")
    Call<StaffCodeDTO> staffLogin(@Body StaffCodeDTO staff);
    @GET("/api/Configuration/getConfigInContract/{id}")
    Call<ConfigControlCenterDTO> getConfig( @Path("id") String houseId);
    @POST("api/SmartHouseRequest/sendSmartHouseRequest")
    Call<SmartHouseRequestDTO> sendRequest(@Body SmartHouseRequestDTO requestDTO);


}
