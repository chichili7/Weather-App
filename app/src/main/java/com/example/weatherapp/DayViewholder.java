package com.example.weatherapp;

import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.databinding.LargerecyclerviewBinding;

public class DayViewholder extends RecyclerView.ViewHolder{
    LargerecyclerviewBinding binding;

    public DayViewholder(LargerecyclerviewBinding binding){
                super(binding.getRoot());
                this.binding=binding;
    }

}
