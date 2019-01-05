package example.com.step;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface BackendService {

    @GET("intern/metric.json")
    Call<List<StatisticsModel>> getStatistics();
}
