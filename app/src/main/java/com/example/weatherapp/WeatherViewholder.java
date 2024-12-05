package com.example.weatherapp;

import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.databinding.SmallrecyclerviewBinding;

public class WeatherViewholder extends RecyclerView.ViewHolder {
SmallrecyclerviewBinding binding;


    public WeatherViewholder(SmallrecyclerviewBinding binding){
        super(binding.getRoot());
        this.binding=binding;
    }
}
