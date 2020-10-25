package com.example.myshop.Buyers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.myshop.Admin.AdminUserProductsActivity;
import com.example.myshop.Model.Orders;
import com.example.myshop.Prevalent.Prevalent;
import com.example.myshop.R;

public class OrdersActivity extends AppCompatActivity {

    private RecyclerView ordersList;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");

        ordersList = findViewById(R.id.order_user_list);
        ordersList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Orders> options =
                new FirebaseRecyclerOptions.Builder<Orders>()
                        .setQuery(ordersRef.orderByChild("phone").equalTo(Prevalent.currentOnlineUser.getPhone()), Orders.class)
                        .build();
        FirebaseRecyclerAdapter<Orders, OrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<Orders, OrdersViewHolder>(options) {
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

                                Intent intent = new Intent(OrdersActivity.this, AdminUserProductsActivity.class);
                                intent.putExtra("id", id);
                                startActivity(intent);
                            }
                        });
                            ordersViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    if (orders.getStatus().equals("Đã vận chuyển")) {
                                        final String phone = orders.getPhone();
                                        final String status = orders.getStatus();
                                        CharSequence options[] = new CharSequence[]
                                                {
                                                        "Đúng",
                                                        "Sai"
                                                };
                                        AlertDialog.Builder builder = new AlertDialog.Builder(OrdersActivity.this);
                                        builder.setTitle("Đã nhận được hàng?");

                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                if (which == 0) {
                                                    ChangeOrderState(phone, status);
                                                } else {
                                                    Toast.makeText(OrdersActivity.this, "Đơn hàng của bạn đang được vận chuyển.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                        builder.show();
                                    }
//                                    else {
//                                        Toast.makeText(OrdersActivity.this, "Đơn hàng của bạn chưa được vận chuyển.", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
                            });
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

    private void ChangeOrderState(String phone, String status) {
        if ( status.equals("Đã nhân đươc hàng")) {
            Toast.makeText(OrdersActivity.this, "Ban đã nhận đơn hàng này trước đó.", Toast.LENGTH_SHORT).show();
        }
        else if (status.equals("Đã vận chuyển")){
            ordersRef
                    .child(phone).child("status")
                    .setValue("Đã nhân đươc hàng").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(OrdersActivity.this, "Đã nhận được hàng.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(this, "Đơn hàng này của bạn chưa được vận chuyển.", Toast.LENGTH_SHORT).show();
        }

    }

    public static class OrdersViewHolder extends RecyclerView.ViewHolder{

        public TextView userTotalPrice, userName, userPhoneNumber, userDateTime, userShippingAddress, orderStatus;
        public Button showOrders;

        public OrdersViewHolder(View view){
            super(view);

            userName = view.findViewById(R.id.order_user_name);
            userPhoneNumber = view.findViewById(R.id.order_phone_number);
            userDateTime = view.findViewById(R.id.order_date_time);
            userShippingAddress = view.findViewById(R.id.order_address_city);
            userTotalPrice = view.findViewById(R.id.order_total_price);
            showOrders = view.findViewById(R.id.show_all_products_btn);
            orderStatus = view.findViewById(R.id.order_status);
        }
    }
}
