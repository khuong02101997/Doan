package com.example.myshop.Sellers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.myshop.R;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SellerRegistrationActivity extends AppCompatActivity {

    private Button sellerLoginBeginBtn, registerBtn;
    private EditText nameInput, phoneInput, emailInput, passwordInput, addressInput;

    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_registration);

        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        nameInput = findViewById(R.id.seller_name);
        phoneInput = findViewById(R.id.seller_phone);
        emailInput = findViewById(R.id.seller_email);
        passwordInput = findViewById(R.id.seller_password);
        addressInput = (EditText) findViewById(R.id.seller_address);

        registerBtn = findViewById(R.id.seller_register_btn);
        sellerLoginBeginBtn = findViewById(R.id.seller_already_have_account_btn);

        sellerLoginBeginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(SellerRegistrationActivity.this, SellerLoginActivity.class);
                startActivity(intent);
            }
        });


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerSeller();
            }
        });
    }

    private void registerSeller() {
        final String name = nameInput.getText().toString();
        final String phone = phoneInput.getText().toString();
        final String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        final String address = addressInput.getText().toString();

        Pattern patternPassword = Pattern.compile("[a-zA-Z0-9]");
        Matcher matcherPassword = patternPassword.matcher(password);



        if (name.isEmpty() && phone.isEmpty() && email.isEmpty() && password.isEmpty() && address.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        }
        else if (password.length() < 6){
            Toast.makeText(this, "Mật khẩu phải có nhiều hơn 6 ký tự", Toast.LENGTH_SHORT).show();
        }
        else if (matcherPassword.find() == true) {
            loadingBar.setTitle("Đang đăng ký.");
            loadingBar.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                final DatabaseReference RootRef;
                                RootRef = FirebaseDatabase.getInstance().getReference();

                                String sid = mAuth.getCurrentUser().getUid();

                                HashMap<String, Object> sellerMap = new HashMap<>();
                                sellerMap.put("sid", sid);
                                sellerMap.put("phone", phone);
                                sellerMap.put("email", email);
                                sellerMap.put("name", name);
                                sellerMap.put("address", address);

                                RootRef.child("Sellers").child(sid).updateChildren(sellerMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                loadingBar.dismiss();
                                                Toast.makeText(SellerRegistrationActivity.this, "Đăng ký bán hàng thành công.", Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(SellerRegistrationActivity.this, SellerLoginActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                            }
                        }
                    });
        }
        else {
            Toast.makeText(this, "Mật khẩu phải có chữ thường, chữ hoa và sô", Toast.LENGTH_SHORT).show();

        }
    }
}
