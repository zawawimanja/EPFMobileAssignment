package com.example.myapplication;

import android.content.Context;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.MyViewHolder> {

    public static final String TAG="FilterAdapter";
    ArrayList<String> namePlaceFilter;
    ArrayList<String> namePlace;
    ArrayList<String> addressPlace;
    ArrayList<String> mobileNumbers;
    Context context;
   SharedPreferences  sharedpreferences;

   boolean display= false;

    public static final String mypreference = "mypref";
    public static final String Name = "nameKey";
    public static final String Name1 = "nameKey1";
    public static final String Name2 = "sortdata";
    int count=0;

    public FilterAdapter(Context context, ArrayList<String> namePlace, ArrayList<String> addressPlace) {
        this.context = context;
        this.namePlace = namePlace;
        this.addressPlace = addressPlace;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout, parent, false);
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }
    private int currentPosition = -1;

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {



        // set the data in items
        holder.name.setText(namePlace.get(position));
  //     holder.email.setText(addressPlace.get(position));
     //   holder.mobileNo.setText(mobileNumbers.get(position));
        // implement setOnClickListener event on item view.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // display a toast with person name on item click
                Toast.makeText(context, namePlace.get(position), Toast.LENGTH_SHORT).show();



                if (currentPosition == position) {
                    holder.imageView.setVisibility(View.INVISIBLE);
                } else {
                    holder.imageView.setVisibility(View.VISIBLE);
                }





                //share prefence send data
                sharedpreferences = context.getSharedPreferences(mypreference,  Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(Name, namePlace.get(position));
                editor.commit();

                Log.i(TAG,"DataSent"+namePlace.get(position));


            }
        });



    }





    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }




    @Override
    public int getItemCount() {
        return namePlace.size();
    }




    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;// init the item view's
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);

            // get the reference of item view's
            name = (TextView) itemView.findViewById(R.id.name);
            imageView = (ImageView) itemView.findViewById(R.id.imageIcon);
       //    email = (TextView) itemView.findViewById(R.id.email);
         //   mobileNo = (TextView) itemView.findViewById(R.id.mobileNo);

        }
    }
}
