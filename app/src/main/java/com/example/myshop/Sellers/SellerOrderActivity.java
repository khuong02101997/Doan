package com.example.myshop.Sellers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.myshop.Admin.AdminUserProductsActivity;
import com.example.myshop.Buyers.OrdersActivity;
import com.example.myshop.Model.Orders;
import com.example.myshop.R;

public class SellerOrderActivity extends AppCompatActivity {

    private RecyclerView ordersSellerList;
    private DatabaseReference ordersSellerRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_order);

        ordersSellerRef = FirebaseDatabase.getInstance().getReference().child("Orders");

        ordersSellerList = findViewById(R.id.order_seller_list);
        ordersSellerList.setLayoutManager(new LinearLayoutManager(this));
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Orders> options =
                new FirebaseRecyclerOptions.Builder<Orders>()
                        .setQuery(ordersSellerRef.orderByChild("sid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()), Orders.class)
                        .build();
        FirebaseRecyclerAdapter<Orders, OrdersActivity.OrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<Orders, OrdersActivity.OrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull OrdersActivity.OrdersViewHolder ordersViewHolder, final int i, @NonNull final Orders orders) {

                        ordersViewHolder.userName.setText("Tên: " + orders.getName());
                        ordersViewHolder.userPhoneNumber.setText("Số điên thoại : " + orders.getPhone());
                        ordersViewHolder.userTotalPrice.setText("Tổng tiền: " + orders.getTotalAmount() + " VNĐ");
                        ordersViewHolder.userDateTime.setText("Thới gian đặt hàng: " + orders.getDate() + " " + orders.getTime());
                        ordersViewHolder.userShippingAddress.setText("Địa chỉ giao hàng: " + orders.getAddress() + ", " + orders.getCity());
                        ordersViewHolder.orderStatus.setText("Trạng thái đơn hàng: " + orders.getStatus());

                        ordersViewHolder.showOrders.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String id = getRef(i).getKey();

                                Intent intent = new Intent(SellerOrderActivity.this, AdminUserProductsActivity.class);
                                intent.putExtra("id", id);
                                startActivity(intent);
                            }
                        });

                        ordersViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String phone = orders.getPhone();
                                final String status = orders.getStatus();


                                final CharSequence options[] = new CharSequence[]
                                        {
                                                "Đúng",
                                                "Sai"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(SellerOrderActivity.this);
                                builder.setTitle("Xác nhân gửi hàng?");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (which == 0) {
                                            ChangeOrderState(phone, status);
                                        } else {
                                            finish();
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public OrdersActivity.OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout, parent, false);
                        return new OrdersActivity.OrdersViewHolder(view);
                    }
                };

        ordersSellerList.setAdapter(adapter);
        adapter.startListening();
    }

    private void ChangeOrderState(String phone, String status) {

        if (status.equals("Đã đặt hàng")) {
            ordersSellerRef
                    .child(phone).child("status")
                    .setValue("Đã vận chuyển").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(SellerOrderActivity.this, "Xác nhận vận chuyển đơn hàng thành công.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(SellerOrderActivity.this, "Đơn hàng này đã được vận chuyển trước đó.", Toast.LENGTH_SHORT).show();
        }
    }
}
