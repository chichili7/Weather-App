package com.example.weatherapp;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherapp.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static RequestQueue queue;
    private static WeatherInfo weatherObj;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private WeatherAdapter WeatherAdapter;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST = 123;
    private static String locationString = "Unspecified Location";

    private static final String weatherURL ="https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline";
    private static final String API_KEY ="2BVBW7ZYSHY7EL7EAKQK57572";
    private List<WeatherInfo.Hours> hoursList;
    private ChartMaker chartMaker;
    private String apiResponse;
    private TreeMap<String, Double> tempData = new TreeMap<>();
    private String city;
    private boolean forc = true;
    private String units = "us";
    private String loc = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        binding.progressBar.setVisibility(View.VISIBLE);
        hoursList = new ArrayList<>();
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mFusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);
        determineLocation();

        chartMaker = new ChartMaker(this, binding);
        recyclerView = binding.recyclerView;
        recyclerView.setAdapter(WeatherAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
    }

    public void clicky(View view){
        forc = !forc;
        if(forc){
            binding.degreespng.setImageResource(R.drawable.units_f);
            units="us";
        }else {
            binding.degreespng.setImageResource(R.drawable.units_c);
            units="metric";
        }
        loc = locationString;
        if(!loc.isEmpty()){
            downloadWeatherByCity(loc);
        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void determineLocation() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST);
            return;
        }

        mFusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        locationString = getPlaceName(location);
                        downloadWeatherByCity(locationString);

                    } else {
                        Log.d(TAG, "determineLocation: NULL LOCATION");
                        // Fallback to last known location
                        mFusedLocationClient.getLastLocation()
                                .addOnSuccessListener(this, lastLocation -> {
                                    if (lastLocation != null) {
                                        locationString = getPlaceName(lastLocation);
                                        downloadWeatherByCity(locationString);
                                    } else {
                                        Toast.makeText(MainActivity.this,
                                                "Unable to determine location. Please check location services.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(this, e -> {
                    Log.d(TAG, "determineLocation: FAILURE");
                    Toast.makeText(MainActivity.this,
                            e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                determineLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
    private String getPlaceName(Location loc) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        StringBuilder sb = new StringBuilder();
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String cityName = address.getLocality();

                if (cityName == null || cityName.isEmpty()) {
                    cityName = address.getSubAdminArea();
                }

                if (cityName == null || cityName.isEmpty()) {
                    cityName = address.getAdminArea();
                }

                if (cityName != null && !cityName.isEmpty()) {
                    return cityName;
                } else {
                    return getString(R.string.cannot_determine_location);
                }
            } else {
                return getString(R.string.cannot_determine_location);
            }
        } catch (IOException e) {
            Log.d(TAG, "getPlaceName: " + e.getMessage());
            sb.append(getString(R.string.cannot_determine_place));
        }
        return sb.toString();
    }

    public void location(View v){
        EditText input = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String message = "For US locations, enter as 'City',\nor 'City,State'\n\n" +
                "For international locations enter\nas 'City,Country'";
        builder.setTitle("Enter a Location");
        builder.setMessage(message);
        builder.setView(input);
        builder.setPositiveButton("OK", (dialog, which) -> {
             locationString = input.getText().toString().trim();
                downloadWeatherByCity(locationString);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showLocationErrorDialog(String location) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Location Error")
                .setIcon(R.drawable.alert)
                .setMessage("The specified location '" + location + "' could not be resolved. Please try a different location.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void downloadWeatherByCity( String city) {

        if (!isNetworkAvailable()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.alert);
            builder.setTitle("No Internet Connection");
            builder.setMessage("This app requires an internet connection to function properly.Please check your connection and try again." );
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            builder.show();
            binding.progressBar.setVisibility(View.GONE);
            return;
        }

        queue = Volley.newRequestQueue(this);
        Uri.Builder buildURL = Uri.parse(weatherURL).buildUpon();
        buildURL.appendPath(city);
        buildURL.appendQueryParameter("unitGroup",units);
        buildURL.appendQueryParameter("key", API_KEY);
        String urlToUse = buildURL.build().toString();
        doDownload(urlToUse);
    }

    public void doDownload(String Url){
        Response.Listener<JSONObject> listener =
                response -> parseJSON(response.toString());

        Response.ErrorListener error = error1 -> {
                String location = city ;
                runOnUiThread(() -> showLocationErrorDialog(location));
            binding.progressBar.setVisibility(View.GONE);
        };
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, Url,
                        null, listener, error);

        queue.add(jsonObjectRequest);

    }
    private void processHourlyData(JSONArray hoursArray) throws JSONException {
        tempData.clear();
        for (int i = 0; i < hoursArray.length(); i++) {
            JSONObject hourObj = hoursArray.getJSONObject(i);
            String datetime = hourObj.getString("datetime");
            double temp = hourObj.getDouble("temp");
            if (!datetime.contains(":")) {
                datetime += ":00:00";
            } else if (datetime.split(":").length == 2) {
                datetime += ":00";
            }
            tempData.put(datetime, temp);
        }

        chartMaker.makeChart(tempData, System.currentTimeMillis());
    }

    public void displayChartTemp(float x, float y) {
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.US);
        String time = sdf.format(new Date((long) x));
        String temp = String.format(Locale.US, "%.1f°F", y);
        Toast.makeText(this, time + ": " + temp, Toast.LENGTH_SHORT).show();
    }

    public void parseJSON(String s) {
        try{
            this.apiResponse = s;
            JSONObject jObjMain = new JSONObject(s);

            double latitude = jObjMain.getDouble("latitude");
            double longitude = jObjMain.getDouble("longitude");
            String resolvedAddress = jObjMain.getString("resolvedAddress");

            List<WeatherInfo.Days> daysList = new ArrayList<>();
            hoursList = new ArrayList<>();

            JSONArray daysArray = jObjMain.getJSONArray("days");
            long currentTimeEpoch = System.currentTimeMillis() / 1000;

            JSONObject firstDay = daysArray.getJSONObject(0);
            JSONArray firstDayHours = firstDay.getJSONArray("hours");
            processHourlyData(firstDayHours);

            for (int i = 0; i < Math.min(15, daysArray.length()); i++) {
                JSONObject dayObj = daysArray.getJSONObject(i);
                long datetimeEpoch = dayObj.getLong("datetimeEpoch");
                double tempmax = dayObj.getDouble("tempmax");
                double tempmin = dayObj.getDouble("tempmin");
                int precipprob = dayObj.optInt("precipprob", 0);
                int uvindex = dayObj.optInt("uvindex", 0);
                String conditions = dayObj.optString("conditions", "");
                String description = dayObj.optString("description", "");
                String icon = dayObj.optString("icon", "");
                if (i < 3) {

                JSONArray hoursArray = dayObj.getJSONArray("hours");

                for (int j = 0; j < hoursArray.length(); j++) {
                    JSONObject hourObj = hoursArray.getJSONObject(j);
                    String datetime = hourObj.getString("datetime");
                    long datetimeEpochHour = hourObj.getLong("datetimeEpoch");
                    if (datetimeEpochHour > currentTimeEpoch) {
                        double temp = hourObj.getDouble("temp");
                        double feelslike = hourObj.getDouble("feelslike");
                        double humidity = hourObj.getDouble("humidity");
                        double windgust = hourObj.optDouble("windgust", 0.0);
                        double windspeed = hourObj.getDouble("windspeed");
                        double winddir = hourObj.getDouble("winddir");
                        double visibility = hourObj.getDouble("visibility");
                        int cloudcover = hourObj.getInt("cloudcover");
                        int uvindexHour = hourObj.getInt("uvindex");
                        String conditionsHour = hourObj.optString("conditions", "");
                        String iconHour = hourObj.optString("icon", "");

                        WeatherInfo.Hours hourData = new WeatherInfo.Hours(
                                datetime, datetimeEpochHour, temp, feelslike, humidity,
                                windgust, windspeed, winddir, visibility, cloudcover,
                                uvindexHour, conditionsHour, iconHour
                        );
                        hoursList.add(hourData);
                    }
                    }
                }

                WeatherInfo.Days day = new WeatherInfo.Days(datetimeEpoch, tempmax, tempmin, precipprob,
                        uvindex, conditions, description, icon);
                daysList.add(day);
            }

            List<WeatherInfo.Alerts> alertsList = new ArrayList<>();
            if (jObjMain.has("alerts")) {
                JSONArray alertsArray = jObjMain.getJSONArray("alerts");
                for (int i = 0; i < alertsArray.length(); i++) {
                    JSONObject alertObj = alertsArray.getJSONObject(i);
                    String event = alertObj.optString("event", "");
                    String headline = alertObj.optString("headline", "");
                    String id = alertObj.optString("id", "");
                    String description = alertObj.optString("description", "");

                    WeatherInfo.Alerts alert = new WeatherInfo.Alerts(event, headline, id, description);
                    alertsList.add(alert);
                }
            }

            JSONObject currentConditionsObj = jObjMain.getJSONObject("currentConditions");
            long datetimeEpoch = currentConditionsObj.getLong("datetimeEpoch");
            int temp = currentConditionsObj.getInt("temp");
            int feelslike = currentConditionsObj.getInt("feelslike");
            double humidity = currentConditionsObj.getDouble("humidity");
            double windgust = currentConditionsObj.optDouble("windgust", 0.0);
            double windspeed = currentConditionsObj.getDouble("windspeed");
            double winddir = currentConditionsObj.getDouble("winddir");
            double visibility = currentConditionsObj.getDouble("visibility");
            int cloudcover = currentConditionsObj.getInt("cloudcover");
            int uvindex = currentConditionsObj.getInt("uvindex");
            String conditions = currentConditionsObj.optString("conditions", "");
            String icon = currentConditionsObj.optString("icon", "");
            long sunriseEpoch = currentConditionsObj.optLong("sunriseEpoch", 0);
            long sunsetEpoch = currentConditionsObj.optLong("sunsetEpoch", 0);

            WeatherInfo.CurrentConditions currentConditions = new WeatherInfo.CurrentConditions(
                    datetimeEpoch, temp, feelslike, humidity, windgust, windspeed, winddir, visibility,
                    cloudcover, uvindex, conditions, icon, sunriseEpoch, sunsetEpoch);

            weatherObj = new WeatherInfo(latitude, longitude, resolvedAddress, daysList, alertsList, currentConditions);
            updateData(weatherObj);

            WeatherAdapter = new WeatherAdapter(hoursList, this,forc);
            recyclerView.setAdapter(WeatherAdapter);
            ColorDrawable overlay = new ColorDrawable(Color.WHITE);
            overlay.setAlpha(50);
            recyclerView.setForeground(overlay);
            WeatherAdapter.notifyDataSetChanged();

        }
        catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void updateData(WeatherInfo weatherObj){
        if (weatherObj == null) {
            Toast.makeText(this, "Please Enter a Valid City Name", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullAddress = weatherObj.getResolvedAddress();
        String city = fullAddress.split(",")[0];
        String date2 = new SimpleDateFormat("EEE MMM dd hh:mm a", Locale.getDefault()).format(new Date());
        String displayText = city + ", " + date2;
        binding.resolvedaddress.setText(displayText);
        double temp = weatherObj.getCurrentConditions().getTemp();
        int roundedTemp = (int) Math.round(temp);
        if(forc){
            String temp1 = roundedTemp+"°F";
            binding.temparature.setText(temp1);
        } else {
            String temp1 = roundedTemp+"°C";
            binding.temparature.setText(temp1);
        }

        ColorMaker.setColorGradient(binding.main, temp, "F", "main");
        ColorMaker.setColorGradient(binding.iconbar, temp, "F", "iconBar");

        double feels = weatherObj.getCurrentConditions().getFeelslike();
        int roundedTemp1 = (int) Math.round(feels);
        if(forc) {
            String feelslike = "Feels Like " + roundedTemp1 + " °F";
            binding.feelslike.setText(feelslike);
        } else {
            String feelslike = "Feels Like " + roundedTemp1 + " °C";
            binding.feelslike.setText(feelslike);
        }
        String mix = String.format("%s (%d%% clouds)", weatherObj.getCurrentConditions().getConditions(), Math.round(weatherObj.getCurrentConditions().getCloudcover()));

        binding.weatherdescription.setText(mix);
        double winds = weatherObj.getCurrentConditions().getWindspeed();
        String windSpeed = String.valueOf(winds);
        double windg = weatherObj.getCurrentConditions().getWindgust();
        String windGust = String.valueOf(windg);
        double wind = weatherObj.getCurrentConditions().getWinddir();
        String winddirection = getDirection(wind);
        if(forc) {
            String windy = "Winds: " + winddirection + " at " + windSpeed + " mph gusting to " + windGust + " mph";
            binding.winddirection.setText(windy);
        } else {
            String windy = "Winds: " + winddirection + " at " + windSpeed + " kmph gusting to " + windGust + " kmph";
            binding.winddirection.setText(windy);
        }
        double hum = weatherObj.getCurrentConditions().getHumidity();
        String humString = String.format("Humidity: %d%%", Math.round(hum));
        binding.humidity.setText(humString);
        double uv= weatherObj.getCurrentConditions().getUvindex();
        String uvindex = "UV Index: " +uv;
        binding.uvindex.setText(uvindex);
        double vis = weatherObj.getCurrentConditions().getVisibility();
        if(forc) {
            String visString = "Visibility: " + vis + " mi";
            binding.visibility.setText(visString);
        } else {
            String visString = "Visibility: " + vis + " km";
            binding.visibility.setText(visString);
        }
        long dt = weatherObj.getCurrentConditions().getSunriseEpoch();
        String date = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date(dt * 1000));
        binding.sunrise.setText("Sunrise: "+date);
        long dt1 = weatherObj.getCurrentConditions().getSunsetEpoch();
        String date1 = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date(dt1 * 1000));
        binding.sunset.setText("Sunset: "+date1);
        String icon = weatherObj.getCurrentConditions().getIcon();
        icon = icon.replace("-", "_");
        int iconID = getId(icon, R.drawable.class);
        if (iconID == 0) {
            iconID = R.mipmap.ic_launcher;
        }
        binding.weatherpng.setImageResource(iconID);
        binding.progressBar.setVisibility(View.GONE);
    }

    public static int getId(String resourceName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resourceName);
            return idField.getInt(idField);
        } catch (Exception e) {
            return 0;
        }
    }


    private String getDirection(double degrees) {
        if (degrees >= 337.5 || degrees < 22.5)
            return "N";
        if (degrees >= 22.5 && degrees < 67.5)
            return "NE";
        if (degrees >= 67.5 && degrees < 112.5)
            return "E";
        if (degrees >= 112.5 && degrees < 157.5)
            return "SE";
        if (degrees >= 157.5 && degrees < 202.5)
            return "S";
        if (degrees >= 202.5 && degrees < 247.5)
            return "SW";
        if (degrees >= 247.5 && degrees < 292.5)
            return "W";
        if (degrees >= 292.5 && degrees < 337.5)
            return "NW";
        return "X";
    }

    public void onClick(View view){
        if(apiResponse!= null) {
            Intent intent = new Intent(this, DailyForecastActivity.class);
            intent.putExtra("weather_data", apiResponse);
            intent.putExtra("boolean_value",forc);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Weather data not yet available",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void click1(View view){
        Toast.makeText(this,"Feature Not Implemented",Toast.LENGTH_SHORT).show();
    }
    public void click2(View view){
        Toast.makeText(this,"Feature Not Implemented",Toast.LENGTH_SHORT).show();
    }
    public void click3(View view){
        Toast.makeText(this,"Feature Not Implemented",Toast.LENGTH_SHORT).show();
    }
}