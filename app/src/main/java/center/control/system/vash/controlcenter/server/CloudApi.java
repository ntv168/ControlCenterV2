package center.control.system.vash.controlcenter.server;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Thuans on 6/15/2017.
 */

public  interface CloudApi {
    @POST("/api/SmartHouse/Login")
    Call<LoginSmarthouseDTO> mobileLogin(@Body HouseKeyDTO customer);

}
