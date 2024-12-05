package com.example.weatherapp;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.databinding.LargerecyclerviewBinding;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DayAdapter extends RecyclerView.Adapter<DayViewholder>{

    private List<WeatherInfo.Days> daysList;
    private WeatherInfo weatherInfo;
    private final DailyForecastActivity dailyForecastActivity;
    private boolean forc;

    public DayAdapter(List<WeatherInfo.Days> daysList, DailyForecastActivity dailyForecastActivity,boolean forc) {
        this.daysList=daysList;
        this.dailyForecastActivity = dailyForecastActivity;
        this.forc=forc;

    }
    @NonNull
    @Override
    public DayViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LargerecyclerviewBinding binding =
                LargerecyclerviewBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false);
        return new DayViewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewholder holder, int position) {
        WeatherInfo.Days abc1 = daysList.get(position);
        Date date1 = new Date(abc1.getDatetimeEpoch()*1000);
        ColorMaker.setColorGradient(holder.itemView,abc1.getTempmax(), "F", "main");
        holder.binding.datetime.setText(new SimpleDateFormat("EEEE,MM/d", Locale.getDefault()).format(date1));
        String icon = abc1.getIcon();
        icon = icon.replace("-", "_");
        int iconID = getId(icon, R.drawable.class);
        if (iconID == 0) {
            iconID = R.mipmap.ic_launcher;
        }
        holder.binding.imageView2.setImageResource(iconID);
        double tempmaxi = abc1.getTempmax();
        int roundedTemp = (int) Math.round(tempmaxi);
        String tempmaxi1 = String.valueOf(roundedTemp);
        double tempmini = abc1.getTempmin();
        int roundedTemp1 = (int) Math.round(tempmini);
        String tempmini1 = String.valueOf(roundedTemp1);
        if(forc) {
            String tempmaxmin = tempmaxi1 + "°F/" + tempmini1 + "°F";
            holder.binding.bigtemp.setText(tempmaxmin);
        } else {
            String tempmaxmin = tempmaxi1 + "°C/" + tempmini1 + "°C";
            holder.binding.bigtemp.setText(tempmaxmin);
        }
        holder.binding.description.setText(abc1.getDescription());
        int precip = abc1.getPrecipprob();
        String precipitation = "("+precip+"% precip.)";
        holder.binding.precip.setText(precipitation);
        int uv = abc1.getUvindex();
        String uvindex = "UV Index: "+uv;
        holder.binding.UVindex12.setText(uvindex);
        double mor = abc1.getMorning();
        int mor1 = (int) Math.round(mor);
        if(forc) {
            String morn = mor1 + "°F";
            holder.binding.mrgtemp.setText(morn);
        }else {
            String morn = mor1 + "°C";
            holder.binding.mrgtemp.setText(morn);
        }
        double aftn = abc1.getAfternoon();
        int aft1 = (int) Math.round(aftn);
        if(forc) {
            String aftnn = aft1 + "°F";
            holder.binding.aftnontemp.setText(aftnn);
        } else {
            String aftnn = aft1 + "°C";
            holder.binding.aftnontemp.setText(aftnn);
        }
        double eve = abc1.getEvening();
        int eve1 = (int) Math.round(eve);
        if(forc) {
            String even = eve1 + "°F";
            holder.binding.evetemp.setText(even);
        }else {
            String even = eve1 + "°C";
            holder.binding.evetemp.setText(even);
        }
        double nig = abc1.getNight();
        int nig1 = (int) Math.round(nig);
        if(forc) {
            String nigh = nig1 + "°F";
            holder.binding.nighttemp.setText(nigh);
        }else {
            String nigh = nig1 + "°C";
            holder.binding.nighttemp.setText(nigh);
        }
        holder.binding.mrg.setText("Morning");
        holder.binding.aftrnoon.setText("Afternoon");
        holder.binding.eve.setText("Evening");
        holder.binding.night.setText("Night");

    }

    @Override
    public int getItemCount() {
        return daysList.size();
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
