package com.example.parking.Helpers;

import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;

public interface IViewOptions<T>   {

    public View getAdapterView(LayoutInflater inflater, T item);
    public void onSelectedCostumItem(T item);
    public void AppendItemToCostumListView(ArrayList<T> items);
}