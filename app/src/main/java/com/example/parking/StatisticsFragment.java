package com.example.parking;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.parking.ApiClient.Controllers.BookingController;
import com.example.parking.ApiClient.Controllers.SpotController;
import com.example.parking.ApiClient.interfaces.IBookingStatistics;
import com.example.parking.Enum.Options;
import com.example.parking.Helpers.CurrentTime;
import com.example.parking.Helpers.HandelPieChart;
import com.example.parking.Helpers.ICallback;
import com.example.parking.Helpers.IViewOptions;
import com.example.parking.Models.Spot;
import com.example.parking.Models.StatisticsParkingGroup;
import com.example.parking.databinding.FragmentStatisticsBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticsFragment extends Fragment implements IBookingStatistics,IViewOptions<Spot>,ICallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private  BookingController bookingController;
    private  FragmentStatisticsBinding  binding;
    public StatisticsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatisticsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatisticsFragment newInstance(String param1, String param2)
    {

        StatisticsFragment fragment = new StatisticsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;

    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentStatisticsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        //here data must be an instance of the class MarsDataProvider
        return view;
    }
    @Override
    public void onStart()
    {
        super.onStart();

        StatisticsFragment.this.getActivity().setTitle(R.string.statistics);
        bookingController= new BookingController();

        binding.btnFilter.setOnClickListener(v->viewDatePicker());
        binding.btnCloseFilter.setOnClickListener(v->binding.backDatePicker.setVisibility(View.GONE));
        binding.btnSelectDate.setOnClickListener(v->onSelectDate());
        binding.btnAsync.setOnClickListener(v->uploadData());
        binding.datePicker.setOnDateChangedListener((view, year, monthOfYear, dayOfMonth) -> onChangedDate(view, year, monthOfYear, dayOfMonth));

        uploadData();
    }

    private void uploadData()
    {

       String date= CurrentTime.getCurrentDate("dd/MM/yyyy");
        binding.txtDate.setText(date);
        binding.progressBar.setVisibility(View.VISIBLE);
        new SpotController().getSpotsStatistics(this);
        bookingController.getBookingsStatisticsPerHours(CurrentTime
                .getCurrentDate("yyyy-MM-dd"),this);
    }


    private void onSelectDate() {

        int year= binding.datePicker.getYear();
        int month=  binding.datePicker.getMonth();
        int day= binding.datePicker.getDayOfMonth();
        if(month==0)
            month=12;

        String date=year+"-"+month+"-"+day;
        binding.txtDate.setText(day+"/"+month+"/"+year);
        bookingController.getBookingsStatisticsPerHours(date,this);
        binding.backDatePicker.setVisibility(View.GONE);

    }

    private void viewDatePicker() {

        binding.backDatePicker.setVisibility(View.VISIBLE);

        @SuppressLint("ResourceType")
        Animation animation = AnimationUtils.loadAnimation(StatisticsFragment.this.getActivity(), R.animator.animation_dialog);
        binding.backDatePicker.startAnimation(animation);
    }

    private void onChangedDate(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if(monthOfYear==0) monthOfYear=12;
        String date=year+"-"+monthOfYear+"-"+dayOfMonth;
        Toast.makeText(this.getContext(), date, Toast.LENGTH_SHORT).show();
    }


    private void insilizingPieChart(long []values)
    {

        long count= 0;
        for (long val:values)
            count+=val;

        //---------------------chart---------------
        HandelPieChart pieChart=new HandelPieChart(StatisticsFragment.this.getContext(),R.id.pieChart,binding.getRoot());
        pieChart.setChartDataSet(values,new String[]{" Available"," Under Reservation ","Booked up"});
        pieChart.DrawChart("",count+""," ");

    }
    private void insilizingBarChart( HashMap<String,Integer> statistics  )
    {
        BarDataSet  barDataSet = new BarDataSet(new ArrayList<BarEntry>(),"Hours ");
        String []keys=statistics.keySet().toArray(new String[statistics.size()]);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
        {


            List<BarEntry> barValues=new ArrayList<>();
            for(int i = 0; i<keys.length; i++)
            {
                Integer count=statistics.getOrDefault(keys[i],0);
                barValues.add(new BarEntry(i,count));
            }


            String label=" Number of Bookings";
            barDataSet = new BarDataSet(barValues, label);
            barDataSet.setColors(Color.rgb(142, 0, 254));
            barDataSet.setValueTextSize(11);
            barDataSet.setBarBorderColor(Color.rgb(142, 0, 254));
        }

        PrintChart(barDataSet,keys);
    }
    private void PrintChart(BarDataSet data,String[] xArray)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            BarData bardata = new BarData(data);
            BarChart bar = (BarChart) binding.barChart;
            bar.setFitBars(false);
            bar.setData(bardata);
            bar.getDescription().setText("");
            bar.animateXY(2000, 2000);
            bar.invalidate();
            //===============================================================
            XAxis xAxis = bar.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(xArray));
            xAxis.setGranularity(1f);
            xAxis.setLabelCount(xArray.length);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        }
        else
            Log.d("chart :","the android API  must be more than  "+ Build.VERSION.SDK_INT+" put this android API not spurted ");

    }

    @Override
    public void OnResponseCallback(Object response) {

    }

    @Override
    public void OnResponseCallback(Object response, Object data) {

        if(response!=null )
        {
            if(data!=null && response.toString().equals(Options.SpotStatistics.getValue()))
            {
                long [] values=(long [])data;
                insilizingPieChart(values);
                binding.progressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void OnResponseCallback(Object response, ArrayList<StatisticsParkingGroup> data) {

    }


    @Override
    public View getAdapterView(LayoutInflater inflater, Spot item) {
        return null;
    }

    @Override
    public void onSelectedCostumItem(Spot item) {

    }


    @Override
    public void AppendItemToCostumListView(ArrayList<Spot> items) {

    }


    @Override
    public void onBookingStatisticsResponse(HashMap<String, Integer> statistics) {

        if(statistics!=null)
        {

            Log.d("statistics",statistics.size()+"");
            insilizingBarChart(statistics);
        }

    }
}