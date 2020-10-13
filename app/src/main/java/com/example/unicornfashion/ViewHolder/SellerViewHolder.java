package com.example.unicornfashion.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unicornfashion.ItemClickListner;
import com.example.unicornfashion.R;

public class SellerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtSellerName, txtCompany, txtType;
    public ImageView imageView;
    public ItemClickListner listner;



    public SellerViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.seller_image);
        txtCompany = (TextView) itemView.findViewById(R.id.company);
        txtSellerName = (TextView) itemView.findViewById(R.id.seller_name);
        txtSellerName = (TextView) itemView.findViewById(R.id.type);


    }
     public void setItemClickListner(ItemClickListner listner){
        this.listner = listner;
     }



    @Override
    public void onClick(View view) {
        listner.OnClick(view, getAdapterPosition(),false);
    }
}
