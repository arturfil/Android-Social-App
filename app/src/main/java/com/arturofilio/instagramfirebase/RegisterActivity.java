package com.arturofilio.instagramfirebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    // Setting all the views for the register Activity
    EditText edt_username, edt_fullname, edt_email, edt_password, edt_password2;
    Button btn_register;
    TextView login_link;

    // Setting the firebase
    FirebaseAuth auth;
    DatabaseReference reference;

    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edt_username = findViewById(R.id.username);
        edt_fullname = findViewById(R.id.fullname);
        edt_email = findViewById(R.id.email);
        edt_password = findViewById(R.id.password);
        edt_password2 = findViewById(R.id.password2);
        login_link = findViewById(R.id.login_link);
        btn_register = findViewById(R.id.btn_register);

        auth = FirebaseAuth.getInstance();

        login_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd = new ProgressDialog(RegisterActivity.this);
                pd.setMessage("Verifying...");
                pd.show();

                String str_username = edt_username.getText().toString();
                String str_fullname = edt_fullname.getText().toString();
                String str_email = edt_email.getText().toString();
                String str_password = edt_password.getText().toString();
                String str_password2 = edt_password2.getText().toString();

                if(TextUtils.isEmpty(str_username) ||
                        TextUtils.isEmpty(str_fullname) ||
                        TextUtils.isEmpty(str_email) ||
                        TextUtils.isEmpty(str_password) ||
                        TextUtils.isEmpty(str_password2)) {
                    Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();

                } else if (str_password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password has to be 6 chars. long",
                            Toast.LENGTH_SHORT).show();

                } else if ( !str_password.equals(str_password2 )) {
                    Toast.makeText(RegisterActivity.this, "Passwords must be equal",
                            Toast.LENGTH_SHORT).show();

                } else {
                    register(str_username, str_fullname, str_email, str_password);
                }
            }
        });

    }

    private void register(final String username, final String fullname, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference().child("Users")
                                    .child(userid);

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username", username.toLowerCase());
                            hashMap.put("fullname", fullname);
                            hashMap.put("bio", "");
                            hashMap.put("imageURL", R.drawable.default_profile);

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        pd.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Successful Login!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                    }
                                }
                            });

                        } else {
                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this, "That email already exists", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }
}
