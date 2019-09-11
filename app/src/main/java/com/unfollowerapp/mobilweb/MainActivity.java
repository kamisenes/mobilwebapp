package com.unfollowerapp.mobilweb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Button btngiris;
    SharedPreferences sharedPreferences;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    Spinner spin;
    EditText edturl;
    List<String> categories;
    List<URLDATA> urldata;
    URLDATA url_d;
    Context mcon;
    public static List<String> stlist;
    Bundle url;
    public static  String android_id;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    public static final String Firebase_Server_URL = "https://webmobilapp-3040c.firebaseio.com/";


    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this, "Hoşgeldiniz", Toast.LENGTH_SHORT).show();
        btngiris = (Button) findViewById(R.id.btngiris);
        mcon = getApplicationContext();
        FirebaseApp.initializeApp(this);
        urldata = new ArrayList<>();
        stlist=new ArrayList<>();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("messages");

        android_id = Settings.Secure.getString((mcon).getContentResolver(),
                Settings.Secure.ANDROID_ID);
        edturl = (EditText) findViewById(R.id.edturl);
        spin = (Spinner) findViewById(R.id.spin_url);
        spin.setOnItemSelectedListener(MainActivity.this);
        url = new Bundle();
        categories = new ArrayList<String>();
        btngiris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String urltext = edturl.getText().toString();
                if (urltext.length() > 0) {
                    url.putString("url", urltext);
                    Intent intent = new Intent(MainActivity.this, webview_Act.class);
                    intent.putExtras(url);
                    //addArtist();


                    startActivity(intent);
                }
            }
        });

        final List<String> universityList = new ArrayList<>();


       DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(android_id);

      /*  rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    int deger = Integer.parseInt(postSnapshot.getKey());
                    String val = postSnapshot.getValue(String.class);
                    Log.d("keys",val + " _"+deger);
                    universityList.add(val);


                }

                ArrayAdapter<String>dataAdapter= new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, universityList);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // attaching data adapter to spinner
                spin.setAdapter(dataAdapter);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("firebasevalues", databaseError.toException() + "..");

            }
        });*/

       rootRef.child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                Log.d("keys", "Value is: " + map);


                if (map!=null) {
                    for (Object value : map.values()) {
                        universityList.add(value.toString());
                    }


                    ArrayAdapter<String>dataAdapter= new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, universityList);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // attaching data adapter to spinner
                    spin.setAdapter(dataAdapter);
                }






            }






            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item = adapterView.getItemAtPosition(i).toString();

        // Showing selected spinner item
        Toast.makeText(adapterView.getContext(), "Secilen URL: " + item, Toast.LENGTH_LONG).show();
        edturl.setText(item);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}
