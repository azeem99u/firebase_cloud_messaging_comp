package com.example.fcmdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    TextView outputTxt;
    Button btnSubTennisTopic,btnUnSubTennisTopic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main) ;
        outputTxt = findViewById(R.id.textView);
        btnSubTennisTopic = findViewById(R.id.btnSubTennis);
        btnUnSubTennisTopic = findViewById(R.id.btnUnSubTennis);

        btnSubTennisTopic.setOnClickListener(view -> {
            FirebaseMessaging.getInstance().subscribeToTopic("tennis-topic").addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Toast.makeText(this, "Tennis Topic Subscribed", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Re-try!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnUnSubTennisTopic.setOnClickListener(view -> {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("tennis-topic").addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Toast.makeText(this, "Tennis Topic UnSubscribed", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "error !", Toast.LENGTH_SHORT).show();
                }
            });
        });

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                storeRegIdInPref(task.getResult());

                Log.d("mytag", "key: "+task.getResult());

            }else {
                String s = task.getException().getLocalizedMessage();
                Log.d("mytag", "onComplete: "+s);
            }
        });

        if (getIntent()!=null && getIntent().hasExtra("key1")){

            outputTxt.setText("");
            for (String key : getIntent().getExtras().keySet()){
                outputTxt.append(key+":\t"+getIntent().getStringExtra(key)+"\n");
            }

        }
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("mypref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.apply();
    }
}