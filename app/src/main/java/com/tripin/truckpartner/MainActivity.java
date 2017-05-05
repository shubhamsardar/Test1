package com.tripin.truckpartner;

import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class MainActivity extends AppCompatActivity {

    Button b1;
    TextView textViewvalue,textviewtimer;
    FirebaseRemoteConfig mFirebaseRemoteConfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ui
        b1 = (Button)findViewById(R.id.button);
        textViewvalue = (TextView)findViewById(R.id.textViewValue);
        textviewtimer = (TextView)findViewById(R.id.textViewtimer);

        final String s = FirebaseInstanceId.getInstance().getToken();
        Log.e("token:",s+"");

        //remote config
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_time_default);

        FirebaseRemoteConfigSettings configSettings =
                new FirebaseRemoteConfigSettings.Builder()
                        .setDeveloperModeEnabled(BuildConfig.DEBUG)
                        .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        fetchremoteconfig();



        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });







    }

    private void fetchremoteconfig() {

        // cacheExpirationSeconds is set to cacheExpiration here, indicating that any previously
// fetched and cached config would be considered expired because it would have been fetched
// more than cacheExpiration seconds ago. Thus the next fetch would go to the server unless
// throttling is in progress. The default expiration duration is 43200 (12 hours).

        int cacheExpiration = 3600;


        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Fetch Succeeded",
                                    Toast.LENGTH_SHORT).show();

                            // Once the config is successfully fetched it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            Toast.makeText(MainActivity.this, "Fetch Failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                        long v = mFirebaseRemoteConfig.getLong("trip_inquiry_timeout");
                        textViewvalue.setText(""+v);
                        starttimer(v);

                    }
                });
    }

    private void starttimer(long t) {

        new CountDownTimer(t, 1000) {

            public void onTick(long millisUntilFinished) {
                textviewtimer.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                textviewtimer.setText("done!");
            }
        }.start();

    }
}
