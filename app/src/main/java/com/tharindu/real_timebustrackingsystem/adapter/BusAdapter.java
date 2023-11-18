package com.tharindu.real_timebustrackingsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.tharindu.real_timebustrackingsystem.R;
import com.tharindu.real_timebustrackingsystem.model.Bus;


import java.util.List;

public class BusAdapter extends RecyclerView.Adapter<BusAdapter.ViewHolder> {

    List<Bus> buses;
    LayoutInflater inflater;
    SelectListener listener;

    public BusAdapter(Context ctx,List<Bus> buses, SelectListener listener) {
        this.buses = buses;
        this.inflater = LayoutInflater.from(ctx);
        this.listener = listener;
    }

    @NonNull
    @Override
    public BusAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.bus_location,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusAdapter.ViewHolder holder, int position) {
        holder.name.setText(buses.get(position).getName());
        holder.route.setText(buses.get(position).getRoute());
        int currentPosition = holder.getAdapterPosition();
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                listener.onBusClicked(buses.get(position));
                listener.onBusClicked(buses.get(currentPosition));
            }
        });
    }

    @Override
    public int getItemCount() {
        return  buses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView route;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.busName);
            route=itemView.findViewById(R.id.busRoute);

        }
    }
}
