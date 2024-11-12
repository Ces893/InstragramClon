package com.example.instragramclone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.instragramclone.HomeFragment;
import com.example.instragramclone.MainActivity;
import com.example.instragramclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    EditText etPassLog, etCorreoLog;
    Button btnCreateLog, btnInicioS;
    ImageView passIcon;
    FirebaseAuth firebaseAuth;

    private boolean passShow = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etCorreoLog = findViewById(R.id.etNameLog);
        etPassLog = findViewById(R.id.etPassLog);

        btnCreateLog = findViewById(R.id.btnCrearCuentaAct);
        btnInicioS = findViewById(R.id.btnInicioS);
        passIcon = findViewById(R.id.passIcon);

        firebaseAuth = FirebaseAuth.getInstance();

        btnInicioS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etCorreoLog.getText().toString();
                String pass = etPassLog.getText().toString();

                if (email.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            assert firebaseUser != null;
                            if(firebaseUser.isEmailVerified()){
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(LoginActivity.this,"Verifique su Correo",Toast.LENGTH_SHORT).show();
                            }

                        }else {
                            Toast.makeText(LoginActivity.this,"Error"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        passIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(passShow){
                    passShow = false;
                    etPassLog.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passIcon.setImageResource(R.drawable.outline_remove_red_eye_24);
                }
                else {
                    passShow = true;
                    etPassLog.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passIcon.setImageResource(R.drawable.outline_visibility_off_24);
                }

                etPassLog.setSelection(etPassLog.length());
            }
        });

        btnCreateLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}