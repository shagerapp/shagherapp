package com.example.parking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.parking.Enum.NotifictionType;
import com.example.parking.databinding.ActivityMainBinding;

public class NotifiyActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifiy);
        Intent intent=getIntent();



        String title=intent.getStringExtra("title");
        String body=intent.getStringExtra("body");
        String topic=intent.getStringExtra("topic");
        long type=intent.getLongExtra("type",0);

        Log.d("extend33 : ", type+"");
//        Toast.makeText(this, tag, Toast.LENGTH_SHORT).show();
        Button btnExtend=findViewById(R.id.DialogBoxBtnExtention);

        btnExtend.setOnClickListener(v ->{
            Intent intent2=new Intent(NotifiyActivity.this, MainActivity.class);
             intent2.putExtra("fregment", "showBooking");
            startActivity(intent2);
        });

        if(type==NotifictionType.NOTIFY_2.ordinal())
            btnExtend.setVisibility(View.VISIBLE);

        TextView ntitle=findViewById(R.id.notifiy_title);
        TextView nbody=findViewById(R.id.notifiy_body);

//        Toast.makeText(NotifiyActivity.this, msgType, Toast.LENGTH_SHORT).show();
        ntitle.setText(title);
        nbody.setText(body);

    }

    public  void   OnClickBtnOK(View v)
    {
        Intent intent = new Intent(NotifiyActivity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }
    public  void   OnClickBtnCancel(View v)
    {
        Intent intent = new Intent(NotifiyActivity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    private void replaceFregment(Fragment fragment)
    {
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }


}