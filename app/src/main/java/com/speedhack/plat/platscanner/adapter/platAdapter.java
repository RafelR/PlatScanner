package com.speedhack.plat.platscanner.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.speedhack.plat.platscanner.DriverActivity;
import com.speedhack.plat.platscanner.R;

import java.util.ArrayList;

public class platAdapter extends RecyclerView.Adapter<platAdapter.CardViewViewHolder>{

    private ArrayList<String> listPlat;
    private Context context;

    public platAdapter(ArrayList<String> listPlat, Context context) {
        this.listPlat = listPlat;
        this.context = context;
    }
    public ArrayList<String> getListRute() {
        return listPlat;
    }

    public void setListRute(ArrayList<String> listPlat) {
        this.listPlat = listPlat;
    }

    @NonNull
    @Override
    public CardViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plat, parent, false);
        CardViewViewHolder viewHolder = new CardViewViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewViewHolder holder, int position) {
        String plat = getListRute().get(position);
        holder.tvPlat.setText(plat);
//
//        Intent intent = new Intent(context,DriverActivity.class);
//        intent.putExtra("nomor",plat);
//        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return getListRute().size();
    }

    public class CardViewViewHolder extends RecyclerView.ViewHolder{
        TextView tvPlat;
        public CardViewViewHolder(View itemView) {
            super(itemView);
            tvPlat = itemView.findViewById(R.id.tv_plat);
        }
    }
}
