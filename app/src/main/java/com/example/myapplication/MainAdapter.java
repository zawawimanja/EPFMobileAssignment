package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.myapplication.MainActivity.TAG;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder>    implements Filterable {
    ArrayList<String> namePlaceFilter;
    ArrayList<String> namePlace;
    ArrayList<String> addressPlace;
    ArrayList<String> mobileNumbers;
    Context context;
    private ContactsAdapterListener listener;

    public MainAdapter(Context context, ArrayList<String> namePlace, ArrayList<String> addressPlace) {
        this.context = context;
        this.namePlace = namePlace;
        this.addressPlace = addressPlace;
        //    this.mobileNumbers = mobileNumbers;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout, parent, false);
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        // set the data in items
        holder.name.setText(namePlace.get(position));
        holder.email.setText(addressPlace.get(position));

        // implement setOnClickListener event on item view.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // display a toast with person name on item click
                Toast.makeText(context, namePlace.get(position), Toast.LENGTH_SHORT).show();



                Intent detailIntent = new Intent(context, MapDetailActivity.class);
                detailIntent.putExtra("name", namePlace.get(position));
                detailIntent.putExtra("address", addressPlace.get(position));
                context.startActivity(detailIntent);

            }
        });

    }




    @Override
    public int getItemCount() {
        return namePlace.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, mobileNo;// init the item view's

        public MyViewHolder(View itemView) {
            super(itemView);

            // get the reference of item view's
            name = (TextView) itemView.findViewById(R.id.name);
            email = (TextView) itemView.findViewById(R.id.email);


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