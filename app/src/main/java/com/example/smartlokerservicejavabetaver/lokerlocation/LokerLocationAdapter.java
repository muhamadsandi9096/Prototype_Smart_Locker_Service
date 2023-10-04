package com.example.smartlokerservicejavabetaver.lokerlocation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartlokerservicejavabetaver.R;

import java.util.ArrayList;

public class LokerLocationAdapter extends RecyclerView.Adapter<LokerLocationAdapter.ViewHolder> {

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private OnItemClickListener onItemClickListener;


    public interface OnItemClickListener{
        void onItemClicked(String name);
    }

    private static ArrayList<LokerLocationModel> lokasiLoker = new ArrayList<>();

    public LokerLocationAdapter(ArrayList<LokerLocationModel> lokasiLoker){
        this.lokasiLoker.clear();
        this.lokasiLoker.addAll(lokasiLoker);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loker_location, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LokerLocationModel lokasi = lokasiLoker.get(position);

        holder.locationName.setText(lokasi.getName());
        holder.locationstatus.setText(lokasi.getLocationstatus());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClicked(lokasi.getLocationUrl());
            }
        });

    }

    @Override
    public int getItemCount() {
        return lokasiLoker.size();
    }

    public void setFilteredList(ArrayList<LokerLocationModel> filteredList){
        this.lokasiLoker = filteredList;
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView locationName, locationstatus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            locationName = itemView.findViewById(R.id.location_name);
            locationstatus = itemView.findViewById(R.id.location_status);
        }
    }
}
