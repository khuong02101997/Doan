package com.example.myshop.Admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.myshop.Buyers.OrdersActivity;
import com.example.myshop.Model.Orders;
import com.example.myshop.R;

public class AdminNewOrderActivity extends AppCompatActivity {

    private RecyclerView ordersList;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_order);

        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");

        ordersList = findViewById(R.id.order_list);
        ordersList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Orders> options =
                new FirebaseRecyclerOptions.Builder<Orders>()
                .setQuery(ordersRef, Orders.class)
                .build();
        FirebaseRecyclerAdapter<Orders, OrdersActivity.OrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<Orders, OrdersActivity.OrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull OrdersActivity.OrdersViewHolder adminOrdersViewHolder, final int i, @NonNull final Orders adminOrders) {


                        adminOrdersViewHolder.userName.setText("Tên: " + adminOrders.getName());
                        adminOrdersViewHolder.userPhoneNumber.setText("Số điên thoại : " + adminOrders.getPhone());
                        adminOrdersViewHolder.userTotalPrice.setText("Tổng tiền: " + adminOrders.getTotalAmount() + " VNĐ");
                        adminOrdersViewHolder.userDateTime.setText("Thới gian đặt hàng: " + adminOrders.getDate() + " " + adminOrders.getTime());
                        adminOrdersViewHolder.userShippingAddress.setText("Địa chỉ giao hàng: " + adminOrders.getAddress() + ", " + adminOrders.getCity());
                        adminOrdersViewHolder.orderStatus.setText("Trạng thái đơn hàng: " + adminOrders.getStatus());

                        adminOrdersViewHolder.showOrders.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String id = getRef(i).getKey();

                                Intent intent = new Intent(AdminNewOrderActivity.this, AdminUserProductsActivity.class);
                                intent.putExtra("id", id);
                                startActivity(intent);
                            }
                        });

                        if (adminOrders.getStatus().equals("Đã nhận được hàng")){
                            adminOrdersViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CharSequence options[] = new CharSequence[]
                                            {
                                                    "Có",
                                                    "Không"
                                            };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrderActivity.this);
                                    builder.setTitle("Bạn có muốn xoa đơn hàng?");

                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            if (which == 0) {
                                                String id = getRef(i).getKey();

                                                RemoveOrder(id);

                                            }
                                            else {
                                                finish();
                                            }
                                        }
                                    });
                                    builder.show();
                                }
                            });
                        }
                    }

                    @NonNull
                    @Override
                    public OrdersActivity.OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout, parent, false);
                        return new OrdersActivity.OrdersViewHolder(view);
                    }
                };

        ordersList.setAdapter(adapter);
        adapter.startListening();
    }

    private void RemoveOrder(String id) {
        ordersRef.child(id).removeValue();

    }

}
