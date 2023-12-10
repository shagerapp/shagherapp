package com.example.parking.Helpers;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CustomAdapter<T> extends BaseAdapter   {

    Context context;
    ArrayList<T> items;
    LayoutInflater  inflater;
    IViewOptions  callBack;

    public CustomAdapter(Context context, IViewOptions callBack, ArrayList<T> items) {

        if(context!=null)
        {
            this.context=context;
            this.callBack=callBack;
            this.items = items;
            this.inflater=LayoutInflater.from(context);
        }

    }
    @Override
    public int getCount()
    {
        return (items!=null)?items.size():0;
    }

    @Override
    public Object getItem(int i)
    {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int i, @Nullable View convertView, @Nullable  ViewGroup  viewGroup)
    {
       return this.callBack.getAdapterView(inflater,items.get(i));
    }
}