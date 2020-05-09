package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static com.example.myapplication.FilterActivity.mypreference1;
import static com.example.myapplication.FilterAdapter.Name;
import static com.example.myapplication.FilterAdapter.Name1;
import static com.example.myapplication.FilterAdapter.Name2;
import static com.example.myapplication.FilterAdapter.mypreference;

public class MainActivity extends AppCompatActivity {

    public static final String TAG="Main";

    RecyclerView recyclerView;
    private ListView lv;
    SearchView searchView;
    ArrayList<String> namePlace = new ArrayList<>();
    ArrayList<String> addressPlace = new ArrayList<>();
    ArrayList<String> faxNumberList = new ArrayList<>();
    MainAdapter filterAdapter;
    SharedPreferences sharedpreferences1;
    String sortData,filterState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = (SearchView) findViewById(R.id.searchView);


        //sorting data
        sharedpreferences1 = getSharedPreferences(mypreference1,
                Context.MODE_PRIVATE);
        sortData = sharedpreferences1.getString(Name2,"");
        Log.i(TAG,"MainActivitySP"+sortData);

       //sorting state
        sharedpreferences1 = getSharedPreferences(mypreference1,
                Context.MODE_PRIVATE);
        filterState = sharedpreferences1.getString(Name1,"");

        Log.i(TAG,"FilterState"+filterState);



        // get the reference of RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // set a LinearLayoutManager with default vertical orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        requestOTP();












// Catch event on [x] button inside search view
        int searchCloseButtonId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeButton = (ImageView) this.searchView.findViewById(searchCloseButtonId);
// Set on click listener
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                requestOTP();
                searchView.setQuery("", false);
                searchView.setIconified(true);


            }
        });


    }


    int count=0;

    //otp problem serialnum
    private void requestOTP(){


            try {
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("ios","100");
                jsonBody.put("lan","EN");
                jsonBody.put("ver","100");
                final String mRequestBody = jsonBody.toString();

                      JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                           "https://secure.kwsp.gov.my/m2/postBranchLocation", null,
                            new Response.Listener<JSONObject>() {


                                @Override
                    public void onResponse(JSONObject  response) {
                        Log.e(TAG, "OTP Response: " + response);

                        try {


                            JSONArray arr = response.getJSONArray("lis");
                            JSONObject element;


                                //clear to avoid duplication
                                namePlace.clear();
                                addressPlace.clear();






                                for(int i = 0; i < arr.length(); i++) {
                                element = arr.getJSONObject(i);



                                    if(filterState==null){

                                        namePlace.add("EPF "+element.getString("nam")+" Office");
                                        addressPlace.add(element.getString("ads"));
                                        faxNumberList.add(element.getString("fax"));
                                    }

                                   else if(filterState==element.getString("nam")) {

                                        namePlace.add("EPF " + element.getString("nam") + " Office");
                                        addressPlace.add(element.getString("ads"));
                                        faxNumberList.add(element.getString("fax"));

                                        break;
                                    }
                                    else{
                                       //add into  each list
                                        namePlace.add("EPF "+element.getString("nam")+" Office");
                                        addressPlace.add(element.getString("ads"));
                                        faxNumberList.add(element.getString("fax"));
                                    }









                                if(sortData.contains("Name")){
                                    Collections.sort(namePlace);
                                    Collections.sort(addressPlace);

                                }





                                Log.i(TAG, "PersonName: " + namePlace);



                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }




                                    filterAdapter = new MainAdapter(MainActivity.this, namePlace, addressPlace,faxNumberList);
                                    recyclerView.setAdapter(filterAdapter); // set the Adapter to RecyclerView




                                    // refreshing recycler view
                                    filterAdapter.notifyDataSetChanged();


                                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                        @Override
                                        public boolean onQueryTextSubmit(String query) {

                                            filterAdapter.getFilter().filter(query);


                                            return false;
                                        }

                                        @Override
                                        public boolean onQueryTextChange(String newText) {
                                            filterAdapter.getFilter().filter(newText);

                                            if(newText==null||newText.isEmpty()){
                                                requestOTP();
                                            }

                                            return false;
                                        }


                                    });


                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "OTP Error : " + error.getMessage());
                        Toast.makeText(getApplicationContext()," OTP Error ",
                                Toast.LENGTH_LONG).show();

                    }
                }){

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() {
                        try {
                            return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                            return null;
                        }
                    }

                    };

                //Adding request to request queue

                requestQueue.add(jsonObjReq);

            }catch (JSONException e) {
                e.printStackTrace();
            }




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);


   
      

        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Intent mainIntent = new Intent(MainActivity.this, FilterActivity.class);
        startActivity(mainIntent);

        return super.onOptionsItemSelected(item);
    }


}