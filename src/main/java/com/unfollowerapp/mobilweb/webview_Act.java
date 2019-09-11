package com.unfollowerapp.mobilweb;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.unfollowerapp.mobilweb.MainActivity.android_id;

public class webview_Act extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {
    Context mCon;
    private WebView webView;
    String compURL="https://";
    SharedPreferences sharedPreferences;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
      List<String> category;
    private FirebaseDatabase mFirebaseDatabase;
    String url_data;
    FirebaseDatabase database;
    public String keyval;
    private static List<String> id;
    private static List<String> urls;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_);
        FirebaseApp.initializeApp(this);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
         database = FirebaseDatabase.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        urls=new ArrayList<>();
        id=new ArrayList<>();
        FloatingActionButton fab = findViewById(R.id.fab);
        // Change the ActionBar title text
                /*
                    public abstract void setTitle (CharSequence title)
                        Set the action bar's title. This will only be
                        displayed if DISPLAY_SHOW_TITLE is set.
                */
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        category=new ArrayList<String>();

        webView = (WebView) findViewById(R.id.webv);
        mCon=getApplicationContext();
        Bundle bundle = this.getIntent().getExtras();

        if(bundle !=null)
        {
            //ObtainBundleData in the object
            url_data = bundle.getString("url");
            String new_url=compURL+url_data+"/";
            loadWv(new_url);
            Log.d("urlbilgi",new_url);
            //Do something here if data  received
        }
        else
        {
            Toast.makeText(mCon, "URL BİLGİSİ GELMEDİ", Toast.LENGTH_SHORT).show();
        }

        WebChromeClient webChromeClient =new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
        };



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.webview_, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if (id==R.id.nav_urlekle){
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            setDialog("Cikis Yapmak Istediginize Emin Misiniz?");
        }
        else if (id==R.id.nav_urlekle){
            urlEkle();

        }else if (id==R.id.nav_urlsil){
            urlSil();

        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void urlSil() {


        final List<String> universityList = new ArrayList<>();

        LayoutInflater li = LayoutInflater.from(webview_Act.this);
        View promptsView = li.inflate(R.layout.popupurlsil, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                webview_Act.this);
        alertDialogBuilder.setView(promptsView);
        final Spinner spinner=(Spinner) promptsView.findViewById(R.id.spinurlsil);
        final Button sil=(Button) promptsView.findViewById(R.id.btnurlsil);
        spinner.setOnItemSelectedListener(webview_Act.this);
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(android_id);
        rootRef.child("posts").addValueEventListener(new ValueEventListener() {
                                                         @Override
                                                         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                             Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                                             Log.d("keys", "Value is: " + map);


                                                             if (map!=null){
                                                                 for (Object value : map.values()) {
                                                                     universityList.add(value.toString());
                                                                     urls.add(value.toString());
                                                                 }

                                                                 for (String key : map.keySet()) {
                                                                     id.add(key);
                                                                 }

                                                                 ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(webview_Act.this, android.R.layout.simple_spinner_item, universityList);
                                                                 dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                                 // attaching data adapter to spinner
                                                                 spinner.setAdapter(dataAdapter);


                                                             }


                                                         }

                                                         @Override
                                                         public void onCancelled(@NonNull DatabaseError databaseError) {

                                                         }


                                                     });


        sil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteURL(spinner.getSelectedItem().toString());


            }
        });
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {



                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();

    }

    private void veriekle(String str){

        DatabaseReference myRef = database.getReference(android_id);
        String key = myRef.child("URL_GET").push().getKey();

//then you can write in that node in this way
        myRef.child("posts").child(key).setValue(str);
    }
    private void verisil(String str){
        String silenecekveri=str;
        String bulunan;
        List<String> updlist=verigetir();
        for (int i =0;i<updlist.size();i++){
            bulunan=updlist.get(i);
            if(bulunan.equals(silenecekveri)){
                updlist.remove(i);
            }
        }
    }

    private List<String> verigetir() {
        final List<String> universityList = new ArrayList<>();


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(android_id);
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    int deger = Integer.parseInt(postSnapshot.getKey());
                    String val = postSnapshot.getValue(String.class);
                    Log.d("veriekleme23",val);
                    universityList.add(val);


                }



                Log.d("veriekleme23liste",universityList.size()+"    ");


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("firebasevalues", databaseError.toException() + "..");

            }
        });

        Log.d("verieklemesonrasi",universityList.size()+"  ");

        return universityList;
    }

    private void urlEkle() {
        LayoutInflater li = LayoutInflater.from(webview_Act.this);
        View promptsView = li.inflate(R.layout.popup, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                webview_Act.this);
        alertDialogBuilder.setView(promptsView);
        final EditText edtZiyaretKonusu=(EditText)promptsView.findViewById(R.id.edturlkay);
        final Button kaydet=(Button) promptsView.findViewById(R.id.btnurlkay);


        kaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtZiyaretKonusu.getText().length()>0) {
                    veriekle(edtZiyaretKonusu.getText().toString());

                    edtZiyaretKonusu.setText("");

                    Toast.makeText(mCon, "URL BASARIYLA KAYDEDILDI", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(mCon, "Lutfen Alani Doldurunuz", Toast.LENGTH_SHORT).show();

                }

            }
        });
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {



                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();

    }

    @SuppressLint("SetJavaScriptEnabled")
    public  void  loadWv(String url){



            webView.setWebViewClient(new WebViewClient() {
                ProgressDialog progressDialog;
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }

                //Show loader on url load
                public void onLoadResource (final WebView view, String url) {
                    if (progressDialog == null) {
                        // in standard case YourActivity.this
                        progressDialog = new ProgressDialog(view.getContext());
                        progressDialog.setMessage("Loading...");
                        progressDialog.show();
                    }
                }
                public void onPageFinished(WebView view, String url) {
                    try{
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                    }catch(Exception exception){
                        exception.printStackTrace();
                    }
                }

            });

            webView.getSettings().setJavaScriptEnabled(true);

    /*
    webView.getSettings().setLoadWithOverviewMode(true);
    webView.getSettings().setUseWideViewPort(true);
    webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
    webView.setScrollbarFadingEnabled(false);
    webView.getSettings().setBuiltInZoomControls(true);
    */

    /*
     String summary = "<html><body>You scored <b>192</b> points.</body></html>";
     webview.loadData(summary, "text/html", null);
     */

            //Load url in webview
            webView.loadUrl(url);
        }





    private void setDialog(String message){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle( "Uyarı" )
                .setMessage(message)
                .setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.setCancelable(true);
                    }
                })

                .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                if (webView != null) {
                                    if (webView.getParent() != null) {
                                        ((ViewGroup) webView.getParent()).removeView(webView);
                                    }
                                    webView.destroy();
                                }
                                Intent intent=new Intent(webview_Act.this,MainActivity.class);
                                startActivity(intent);

                            }
                        }


                ).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(adapterView.getContext(), "Secilen URL: " + item, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }



    private void deleteURL(String silinecekitem){


        if (silinecekitem.length()>0 && silinecekitem!=null){


            for (int i =0;i<id.size();i++){
                if (urls.get(i).equals(silinecekitem)){
                    keyval=id.get(i);
                    Log.d("asdas",keyval);
                    delURL();
                }
            }
        }






       /* FirebaseDatabase.getInstance().getReference(android_id).child("posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null && dataSnapshot.getValue() != null) {


                    //  for (DataSnapshot child : dataSnapshot.getChildren()) {
                    // if we want to get do operation in multiple data then write your code here
                    //  }
                    keyval = dataSnapshot.getKey();
                    delURL();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //add code in case you not get proper dat from firebase
            }
        });*/
    }
    private void delURL(){
        FirebaseDatabase.getInstance().getReference(android_id).child("posts").child(keyval).removeValue();

        urlSil();

    }
}
