package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class FilterActivity extends AppCompatActivity {
    // ArrayList for person names, email Id's and mobile numbers
    ArrayList<String> namePlace = new ArrayList<>();
    ArrayList<String> addressPlace = new ArrayList<>();
    private ListView lv;


    String[] sortArray = {"Name","Distance"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);



        ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.activity_listview,R.id.label,sortArray);
        lv = (ListView) findViewById(R.id.list);
        lv.setAdapter(adapter);


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
}
