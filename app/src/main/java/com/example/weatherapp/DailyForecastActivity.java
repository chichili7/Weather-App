package com.example.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.databinding.ActivityDailyForecastBinding;
import com.example.weatherapp.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DailyForecastActivity extends AppCompatActivity {

    private ActivityDailyForecastBinding binding;
    private DayAdapter DayAdapter;
    private RecyclerView recyclerView;
    private boolean forc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        binding = ActivityDailyForecastBinding.inflate(getLayoutInflater());

        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = binding.recyclerview;
        recyclerView.setAdapter(DayAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
         forc = getIntent().getBooleanExtra("boolean_value",true);
        String weatherJson = getIntent().getStringExtra("weather_data");
        if (weatherJson != null) {
            beanBag(weatherJson);
        } else {
            Toast.makeText(this, "Error: Weather data not available",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void beanBag(String weatherobj1){
        List<WeatherInfo.Days> daysList = new ArrayList<>();

        try {
            JSONObject weather_obj = new JSONObject(weatherobj1);
            String resolvedAddress = weather_obj.getString("resolvedAddress");
            String city = resolvedAddress.split(",")[0];
            String city1 = city + " 15-Day Forecast";
            binding.bar.setText(city1);
            JSONArray daysArray = weather_obj.getJSONArray("days");

            for (int i = 0; i < Math.min(15, daysArray.length()); i++) {
                JSONObject dayObj = daysArray.getJSONObject(i);
                long datetimeEpoch = dayObj.getLong("datetimeEpoch");
                double tempmax = dayObj.getDouble("tempmax");
                double tempmin = dayObj.getDouble("tempmin");
                int precipprob = dayObj.optInt("precipprob", 0);
                int uvindex = dayObj.optInt("uvindex", 0);
                double temp = dayObj.getDouble("temp");
                String conditions = dayObj.optString("conditions", "");
                String description = dayObj.optString("description", "");
                String icon = dayObj.optString("icon", "");

                JSONArray hoursArray = dayObj.getJSONArray("hours");

                double morning = hoursArray.length()>8 ? hoursArray.getJSONObject(8).optDouble("temp",0):0;
                double afternoon = hoursArray.length()>13 ? hoursArray.getJSONObject(13).optDouble("temp",0):0;
                double evening = hoursArray.length()>17 ? hoursArray.getJSONObject(17).optDouble("temp",0):0;
                double night = hoursArray.length()>23 ? hoursArray.getJSONObject(23).optDouble("temp",0):0;

                WeatherInfo.Days day = new WeatherInfo.Days(datetimeEpoch, tempmax, tempmin, precipprob,
                        uvindex, conditions, description, icon,morning,afternoon,evening,night);
                daysList.add(day);

            }
        }catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        DayAdapter = new DayAdapter(daysList,this,forc);
        recyclerView.setAdapter(DayAdapter);

    }
}