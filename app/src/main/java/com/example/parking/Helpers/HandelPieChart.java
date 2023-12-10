package com.example.parking.Helpers;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class HandelPieChart {


    private Context context;
    private  int chart_R_id;
    private ArrayList<PieEntry> valueSet;
    private PieChart pie;
    public  HandelPieChart(Context context, int chart_R_id, View convertView)
    {
        this.context=context;
        this.chart_R_id=chart_R_id;
        this.valueSet=new ArrayList();
        this.pie = (PieChart) convertView.findViewById(chart_R_id);
    }

    public void DrawChart(String title, String centerText,String valueText) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            if(this.valueSet.size()<=0)
                this.valueSet.add(new PieEntry((float)0.0));

            PieDataSet data = new PieDataSet(this.valueSet, valueText);
            ArrayList<Integer> colors=new ArrayList<>();
            colors.add(Color.rgb(142, 0, 254));
            colors.add(Color.rgb(51, 51, 255));
            colors.add(Color.rgb(224, 224, 224));

            data.setColors(ColorTemplate.MATERIAL_COLORS);

//            data.setColors(ColorTemplate.createColors(ColorTemplate.createColors()));
            data.setValueTextSize(15);
            data.setDrawValues(false);

            //=============================================================
            PieData piedata = new PieData(data);

            this.pie.setMaxAngle(1000);
            this.pie.setDrawEntryLabels(false);
            this.pie.setData(piedata);
            this.pie.setTransparentCircleAlpha(2);
            this.pie.getDescription().setText(title);
            this.pie.getDescription().setTextSize(12);
            this.pie.getDescription().setEnabled(true);
            this.pie.getDescription().setYOffset(-15f);
            this.pie.setHoleRadius(50f);
            this.pie.setTransparentCircleRadius(85f);
            this.pie.animateXY(2000, 2000);
            this.pie.setCenterTextSize(15);
            this.pie.setCenterText(centerText);
            this.pie.invalidate();

            Legend legend = this.pie.getLegend();
            legend.setOrientation(Legend.LegendOrientation.VERTICAL);
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
            legend.setDrawInside(false);
            legend.setTextSize(13);
            legend.setFormToTextSpace(8f);
            this.pie.setExtraLeftOffset(20f);
            this.pie.setExtraBottomOffset(30f);

        }
        else
            Toast.makeText(this.context, "the android API  must be more than  "+ Build.VERSION.SDK_INT+" put this android API not spurted ", Toast.LENGTH_LONG).show();


    }

    public void setChartDataSet(long [] values,String [] labels)
    {
        double totalValue=0.0;
//        Long count= Long.valueOf(0);
        for (long val:values) {
            totalValue+=val;
        }

        int i=0;
        for (double item : values)
        {
            totalValue-=item;
            PieEntry val=new PieEntry((float)item);

            if(labels.length>0 && labels.length>i)
                val.setLabel(item+"  "+labels[i++]);

            this.valueSet.add(val);
        }

        if(totalValue>0)
            this.valueSet.add(0,new PieEntry((float)totalValue));
    }

    public void getChartDataSet()
    {

    }
}
