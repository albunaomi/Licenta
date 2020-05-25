package com.example.diana.dreamcakes.ViewHolder;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.diana.dreamcakes.Interface.ItemClickListener;
import com.example.diana.dreamcakes.Model.Favorite;
import com.example.diana.dreamcakes.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView name,price;
    public ImageView image;
    private ItemClickListener itemClickListener;

    public FavoriteViewHolder(View itemView) {
        super(itemView);
        name=(TextView)itemView.findViewById(R.id.item_name);
        price=(TextView)itemView.findViewById(R.id.item_price);
        image=(ImageView)itemView.findViewById(R.id.item_img);
    }

    public void setName(TextView name) {
        this.name = name;
    }

    @Override
    public void onClick(View v) {

    }
}
public class FavoriteAdapter extends  RecyclerView.Adapter<FavoriteViewHolder> {

    private List<Favorite> favoriteList=new ArrayList<>();
    private Context context;

    public FavoriteAdapter(List<Favorite> favoriteList, Context context) {
        this.favoriteList = favoriteList;
        this.context = context;
    }
    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.fav_item_layout,parent,false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Picasso.with(context).load(favoriteList.get(position).getCakeImage())
                .into(holder.image);

        holder.price.setText(new StringBuilder("RON ").append(favoriteList.get(position).getPrice()));
        holder.name.setText(favoriteList.get(position).getCakeName());

    }

    @Override
    public int getItemCount() {
       return favoriteList.size();
    }
}
