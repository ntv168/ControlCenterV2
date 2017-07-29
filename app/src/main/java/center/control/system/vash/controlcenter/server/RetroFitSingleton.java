
package center.control.system.vash.controlcenter.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Thuans on 4/27/2017.
 */

public class RetroFitSingleton {
    private Retrofit retrofit;
    private CloudApi cloudApi;
    private static volatile RetroFitSingleton retroFitSingleton = null;


    private RetroFitSingleton() { }

    public static RetroFitSingleton getInstance() {
        if(retroFitSingleton == null) {
            synchronized(RetroFitSingleton.class) {

                if(retroFitSingleton == null) {
                    Gson gson = new GsonBuilder()
                            .setLenient()
                            .create();

                    retroFitSingleton= new RetroFitSingleton();
                    retroFitSingleton.retrofit = new Retrofit.Builder()
                            .baseUrl(VolleySingleton.SERVER_HOST)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();

                    retroFitSingleton.cloudApi = retroFitSingleton.retrofit.create(CloudApi.class);
                }
            }

        }
        return retroFitSingleton;
    }

    public CloudApi getCloudApi() {
        return cloudApi;
    }
}
