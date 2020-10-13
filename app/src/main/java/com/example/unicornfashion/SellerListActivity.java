package com.example.unicornfashion;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unicornfashion.Admin.AdminCategoryActivity;
import com.example.unicornfashion.Model.Seller;
import com.example.unicornfashion.Prevalent.Prevalent;
import com.example.unicornfashion.ViewHolder.SellerViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SellerListActivity extends AppCompatActivity {

    private DatabaseReference sellerRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_list);

        sellerRef = FirebaseDatabase.getInstance().getReference().child("Sellers");


        recyclerView = findViewById(R.id.seller_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Seller> options = new FirebaseRecyclerOptions.Builder<Seller>()
                .setQuery(sellerRef, Seller.class)
                .build();


        FirebaseRecyclerAdapter<Seller, SellerViewHolder> adapter =
                new FirebaseRecyclerAdapter<Seller, SellerViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull SellerViewHolder holder, int position, @NonNull final Seller model) {
                        holder.txtSellerName.setText(model.getSeller_name());
                        holder.txtCompany.setText(model.getCompany());
//                        holder.txtType.setText(model.getType());
                        Picasso.get().load(model.getImage()).into(holder.imageView);



                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CharSequence optionds[] = new CharSequence[]{
                                        "Edit",
                                        "Remove"
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(SellerListActivity.this);
                                builder.setTitle("Cart Activity");


                                builder.setItems(optionds, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (i == 0){
                                            Intent intent = new Intent(SellerListActivity.this, AdminCategoryActivity.class);
                                            intent.putExtra("pid", model.getPid());
                                            startActivity(intent);
                                        }
                                        if (i == 1){
                                            sellerRef
                                                    .child(Prevalent.currentOnlineUser.getPhone())
                                                    .child("Sellers")
                                                    .child(model.getPid())
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                Toast.makeText(SellerListActivity.this, "Item Removed", Toast.LENGTH_SHORT).show();

                                                                Intent intent = new Intent(SellerListActivity.this,HomeActivity.class);

                                                                startActivity(intent);

                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });


                    }







                    @NonNull
                    @Override
                    public SellerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_layout,parent,false);
                        SellerViewHolder holder = new SellerViewHolder(view);
                        return holder;
                    }
                };


            recyclerView.setAdapter(adapter);
            adapter.startListening();


    }
}