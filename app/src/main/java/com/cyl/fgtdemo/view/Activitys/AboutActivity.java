package com.cyl.fgtdemo.view.Activitys;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.cyl.CustomView.LineChart;
import com.cyl.CustomView.LineData;
import com.cyl.CustomView.PieChart;
import com.cyl.CustomView.PieData;
import com.cyl.fgtdemo.R;

import java.util.ArrayList;

public class AboutActivity extends AppCompatActivity {
    private ArrayList<PieData> mPieDatas = new ArrayList<>();
    // 颜色表
    private int[] mColors = {0xFFCCFF00, 0xFF6495ED, 0xFFE32636, 0xFF800000, 0xFF808000, 0xFFFF8C69, 0xFF808080,
            0xFFE6B800, 0xFF7CFC00};
   // float array1[2][2]={{1.2,2.3},{1.2,2.3}};
    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        return intent;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initData();
     /*   LineChart lineChart=(LineChart) findViewById(R.id.pieChart);
        lineChart.setLineDatas(mPieDatas);*/
        PieChart pieChart = (PieChart) findViewById(R.id.pieChart);
        pieChart.setPieData(mPieDatas);
    }
   private void initData(){
        for (int i=0; i<9; i++){
            PieData pieData = new PieData();
            pieData.setName("区域"+i);
            pieData.setValue((float)i+1);
            pieData.setColor(mColors[i]);
            mPieDatas.add(pieData);
        }
    }
/*private void initData(){
    for (int i=0; i<9; i++){
        LineData pieData = new LineData();
        pieData.setName("区域"+i);
     //   pieData.setValue(array1);
        pieData.setColor(mColors[i]);
        mPieDatas.add(pieData);
    }
}*/
}
