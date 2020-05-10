package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.myapplication.FilterActivity.mypreference1;
import static com.example.myapplication.FilterAdapter.Name;
import static com.example.myapplication.FilterAdapter.Name1;
import static com.example.myapplication.FilterAdapter.mypreference;
import static com.example.myapplication.MainActivity.TAG;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder>    implements Filterable {
    ArrayList<String> namePlaceFilter;
    ArrayList<String> namePlace;
    ArrayList<String> addressPlace;
    ArrayList<String> faxNumberList;
    ArrayList<Double> latList = new ArrayList<>();
    ArrayList<Double> lonList = new ArrayList<>();
    Double latitude, longitude;
    Context context;
    private ContactsAdapterListener listener;
    SharedPreferences  sharedpreferences1;
    Double tDouble;
    ArrayList<Double> distance= new ArrayList<>();
    public MainAdapter(Context context, ArrayList<String> namePlace, ArrayList<String> addressPlace, ArrayList<String> faxNumberList, ArrayList<Double> latList, ArrayList<Double> lonList, double lattitude, double longitude) {
        this.context = context;
        this.namePlace = namePlace;
        this.addressPlace = addressPlace;
        this.faxNumberList = faxNumberList;
        this.latList = latList;
        this.lonList = lonList;
        this.latitude = lattitude;
        this.longitude= longitude;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mainrecycleview, parent, false);
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        // set the data in items
        holder.name.setText(namePlace.get(position));
        holder.address.setText(addressPlace.get(position));

        if(latitude!=null&longitude!=null){

            //formula
            double converlatGPS=latitude;
            double converlonGPS=longitude;
            Log.i(TAG,"LocationUpdateAdapter"+latitude+longitude);

            double converlat=Double.parseDouble(String.valueOf(latList.get(position)));
            double converlon=Double.parseDouble(String.valueOf(lonList.get(position)));

            Double value =distance(converlatGPS,converlonGPS,converlat,converlon,"K");
            tDouble =
                    new BigDecimal(value).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();


            distance.add(tDouble);
            Log.i(TAG,"ValueDistance"+value);

            if(tDouble>1){
                holder.distance.setText(tDouble+" km");

            }else{
                holder.distance.setText(tDouble+" m");

            }
        }else{
            holder.distance.setText("NoData");
        }





        // implement setOnClickListener event on item view.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // display a toast with person name on item click
                Toast.makeText(context, namePlace.get(position), Toast.LENGTH_SHORT).show();



                Intent detailIntent = new Intent(context, MapDetailActivity.class);
                detailIntent.putExtra("name", namePlace.get(position));
                detailIntent.putExtra("address", addressPlace.get(position));
                detailIntent.putExtra("fax", faxNumberList.get(position));
                detailIntent.putExtra("lat", latList.get(position));
                detailIntent.putExtra("lon", lonList.get(position));
                detailIntent.putExtra("distance", distance.get(position));
                context.startActivity(detailIntent);


                Log.i(TAG,"DistanceKM"+distance.get(position));

            }
        });



        sharedpreferences1 = context.getSharedPreferences(mypreference1, Context.MODE_PRIVATE);
        String score1 = sharedpreferences1.getString(Name1,"");

        Log.i(TAG,"MainAdapterSP"+score1);


    }


    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        }
        else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit.equals("K")) {
                dist = dist * 1.609344;
            } else if (unit.equals("N")) {
                dist = dist * 0.8684;
            }
            return (dist);
        }
    }




    @Override
    public int getItemCount() {
        return namePlace.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, address,distance;// init the item view's

        public MyViewHolder(View itemView) {
            super(itemView);

            // get the reference of item view's
            name= (TextView) itemView.findViewById(R.id.name);
            address = (TextView) itemView.findViewById(R.id.email);
            distance = (TextView) itemView.findViewById(R.id.distance);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onContactSelected(namePlaceFilter.get(getAdapterPosition()));
                }
            });

        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                Log.i(TAG,"CHAR"+charString);

                if (charString.isEmpty()) {
                    namePlaceFilter = namePlace;
                } else {
                    List<String> filteredList = new ArrayList<>();
                    for (String row : namePlace) {
                        Log.i(TAG,"NamePlace"+namePlace);
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.toLowerCase().contains(charString.toLowerCase()) || row.contains(charSequence)) {


                            filteredList.add(row);

                            Log.i(TAG,"FilterList"+filteredList);
                        }
                    }

                    namePlaceFilter= (ArrayList<String>) filteredList;
                    namePlace=namePlaceFilter;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = namePlace;

                return filterResults;


            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                namePlace= (ArrayList<String>) filterResults.values;
                Log.i(TAG,"Result"+namePlace);
                notifyDataSetChanged();
            }
        };
    }

    public interface ContactsAdapterListener {
        void onContactSelected(String contact);
    }


}