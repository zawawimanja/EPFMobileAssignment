package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.example.myapplication.FilterAdapter.Name;
import static com.example.myapplication.FilterAdapter.Name1;
import static com.example.myapplication.FilterAdapter.Name2;
import static com.example.myapplication.FilterAdapter.mypreference;

public class FilterActivity extends AppCompatActivity {
    // ArrayList for person names, email Id's and mobile numbers
    ArrayList<String> namePlace = new ArrayList<>();
    ArrayList<String> addressPlace = new ArrayList<>();
    private ListView lv;
    public static final String TAG="Filter";
    LinearLayout addressLinear,distanceLinear;
    ImageView imageIcon,imageIcon2;
    SharedPreferences sharedpreferences;
    SharedPreferences sharedpreferences1;
    public static final String mypreference1 = "mypref1";
    String sortData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        addressLinear=findViewById(R.id.addressLinear);
        distanceLinear=findViewById(R.id.distanceLinear);
        imageIcon=findViewById(R.id.imageIcon);
        imageIcon2=findViewById(R.id.imageIcon2);


        sharedpreferences1 = getSharedPreferences(mypreference1,
                Context.MODE_PRIVATE);
        sortData = sharedpreferences1.getString(Name2,"");
        if(sortData.contains("Name")){
            imageIcon.setVisibility(View.VISIBLE);
            imageIcon2.setVisibility(View.INVISIBLE);
        }else{
            imageIcon2.setVisibility(View.VISIBLE);
            imageIcon.setVisibility(View.INVISIBLE);
        }

        addressLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageIcon.setVisibility(View.VISIBLE);
                imageIcon2.setVisibility(View.INVISIBLE);

                sortData="Name";

                sharedpreferences1 = getSharedPreferences(mypreference1,  Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences1.edit();
                editor.putString(Name2, sortData);
                editor.commit();
            }
        });


        distanceLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageIcon2.setVisibility(View.VISIBLE);
                imageIcon.setVisibility(View.INVISIBLE);

                sortData="Distance";

                sharedpreferences1 = getSharedPreferences(mypreference1,  Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences1.edit();
                editor.putString(Name2, sortData);
                editor.commit();
            }
        });





        // get the reference of RecyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // set a LinearLayoutManager with default vertical orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        try {

            // get JSONObject from JSON file
            JSONArray jsonArray=new JSONArray(loadJSONFromAsset());


            // implement for loop for getting value list data
            for(int i=0;i<jsonArray.length();i++){

                // create a JSONObject for fetching single user data
                JSONObject userDetail = jsonArray.getJSONObject(i);
                // fetch email and name and store it in arraylist
                namePlace.add(userDetail.getString("value"));
                addressPlace.add(userDetail.getString("key"));

            }


//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //  call the constructor of CustomAdapter to send the reference and data to Adapter
          FilterAdapter filterAdapter = new FilterAdapter(FilterActivity.this, namePlace,addressPlace);

        recyclerView.setAdapter(filterAdapter); // set the Adapter to RecyclerView
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("StatesCode.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        String score1 = sharedpreferences.getString(Name,"");

        Log.i(TAG,"SPValue2"+score1);

        sharedpreferences1 = getSharedPreferences(mypreference1,  Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences1.edit();
        editor.putString(Name1, score1);
        editor.commit();

        Log.i(TAG,"Name1"+score1);


        Intent detailIntent = new Intent(this, MainActivity.class);
        detailIntent.putExtra(Name1,score1);
        startActivity(detailIntent);




    }
}
