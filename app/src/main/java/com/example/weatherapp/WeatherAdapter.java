package com.example.weatherapp;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.databinding.SmallrecyclerviewBinding;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherViewholder>{

    private List<WeatherInfo.Hours> hoursList;
    private final MainActivity mainActivity;
    private boolean forc;


    public WeatherAdapter(List<WeatherInfo.Hours> hoursList, MainActivity mainActivity,boolean forc) {
        this.hoursList=hoursList;
        this.mainActivity = mainActivity;
        this.forc=forc;
    }

    @NonNull
    @Override
    public WeatherViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SmallrecyclerviewBinding binding =
                SmallrecyclerviewBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false);
        return new WeatherViewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewholder holder, int position) {
        WeatherInfo.Hours abc = hoursList.get(position);
        Date date1 = new Date(abc.getDatetimeEpoch()*1000);
        if(DateUtils.isToday(abc.getDatetimeEpoch()*1000)){
            holder.binding.textView11.setText("Today");
        }
        else {
            holder.binding.textView11.setText(new SimpleDateFormat("EEEE",Locale.getDefault()).format(date1));
        }
        long dt = abc.getDatetimeEpoch();
        String date = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date(dt * 1000));
        holder.binding.temp12.setText(date);
        double temp = abc.getTemp();
        int roundedTemp = (int) Math.round(temp);
        if(forc) {
            String temp1 = roundedTemp + " °F";
            holder.binding.textView13.setText(temp1);
        } else {
            String temp1 = roundedTemp + " °C";
            holder.binding.textView13.setText(temp1);
        }
        holder.binding.textView14.setText(abc.getConditions());
        String icon = abc.getIcon();
        icon = icon.replace("-", "_");
        int iconID = getId(icon, R.drawable.class);
        if (iconID == 0) {
            iconID = R.mipmap.ic_launcher;
        }
        holder.binding.imageView.setImageResource(iconID);

    }

    @Override
    public int getItemCount() {
        return hoursList.size();
    }

    public static int getId(String resourceName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resourceName);
            return idField.getInt(idField);
        } catch (Exception e) {
            return 0;
        }
    }

}
