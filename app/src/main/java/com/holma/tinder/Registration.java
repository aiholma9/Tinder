package com.holma.tinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.holma.tinder.Common.Common;

public class Registration extends AppCompatActivity {

    private Button btn_register;
    private EditText edt_username,edt_email, edt_pwd;
    private RadioGroup radioGroup;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null){
                    Intent intent = new Intent(Registration.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        btn_register = (Button)findViewById(R.id.register);
        edt_username = (EditText)findViewById(R.id.username);
        edt_email = (EditText)findViewById(R.id.email);
        edt_pwd = (EditText)findViewById(R.id.password);
        radioGroup = (RadioGroup)findViewById(R.id.radioGender);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selectedId = radioGroup.getCheckedRadioButtonId();

                final RadioButton radioButton = (RadioButton)findViewById(selectedId);
                final String username = edt_username.getText().toString();
                final String email = edt_email.getText().toString();
                final String password = edt_pwd.getText().toString();

                if (radioButton.getText() == null)
                    return;

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()){
                                    Toast.makeText(Registration.this, "sign up error!", Toast.LENGTH_SHORT).show();
                                }

                                else {
                                    String userId = mAuth.getCurrentUser().getUid();
                                    databaseReference.child(Common.USERS)
                                            .child(radioButton.getText().toString())
                                            .child(userId).child(Common.NAME);
                                    databaseReference.setValue(username);
                                }
                            }
                        });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }
}
