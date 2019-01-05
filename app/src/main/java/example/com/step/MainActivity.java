package example.com.step;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements NumberPickerDialog.NumberDialogListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String BASE_URL = "https://intern-f6251.firebaseio.com/";
    private static final String SHARES_PREF = "shares_pref";
    private static final String EVERYDAY_GOAL_STEPS = "steps";

    // view
    RecyclerView mRecyclerView;
    ProgressBar mSpinner;
    Button mTargetButton;
    Toolbar mToolbar;

    // var
    List<StatisticsModel> statistics;
    StatisticsAdapter mAdapter;
    SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate : called");

        mToolbar = findViewById(R.id.toolbar);
        mRecyclerView = findViewById(R.id.list_of_statistics);
        mSpinner = findViewById(R.id.progress);
        mTargetButton = findViewById(R.id.target_btn);
        mPreferences = getSharedPreferences(SHARES_PREF, Context.MODE_PRIVATE);

        mTargetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "aimButton onClick : clicked");
                openDialog();
            }
        });

        initRecyclerView();

        getStatisticsData();
    }

    public void openDialog() {
        Log.d(TAG, "openDialog : called");
        NumberPickerDialog dialog = new NumberPickerDialog();
        dialog.show(getSupportFragmentManager(), TAG);
    }

    private void getStatisticsData() {
        Log.d(TAG, "getStatisticsData : called");
        HttpLoggingInterceptor logging =
                new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder();
        httpClient.addInterceptor(logging)
                .connectTimeout(5, TimeUnit.MINUTES);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        BackendService service = retrofit.create(BackendService.class);

        service.getStatistics().enqueue(new Callback<List<StatisticsModel>>() {
            @Override
            public void onResponse(final Call<List<StatisticsModel>> call, final Response<List<StatisticsModel>> response) {
                Log.d(TAG, "onResponse : called");
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        statistics = response.body();
                        mAdapter = new StatisticsAdapter(statistics, mPreferences.getString(EVERYDAY_GOAL_STEPS, String.valueOf(4000)));
                        mRecyclerView.setAdapter(mAdapter);
                    }
                }
                mSpinner.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(final Call<List<StatisticsModel>> call, final Throwable t) {
                Log.d(TAG, "something went wrong, exception: " + t.getMessage());
                mSpinner.setVisibility(View.GONE);
            }
        });
    }

    private void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void applyNumber(final String num) {
        Log.d(TAG, "applyNumber : called with number: " + num);
        Toast.makeText(this, "Your everyday goal: " + num + " steps", Toast.LENGTH_SHORT).show();
        // save new number of steps
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(EVERYDAY_GOAL_STEPS, num);
        editor.apply();
        // and refresh recycler view
        getStatisticsData();
    }
}
