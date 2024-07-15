package com.eom.ustabul;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    MaterialButton btnLogin, btnLogout, btnRegister;
    MaterialTextView txtUserStatus, txtUserDetail;
    TextInputLayout usernameWrapper, passwordWrapper;
    private FirebaseAuth mAuth;
    ActivityResultLauncher<Intent> resultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initComponents();
        registerEventHandlers();
        mAuth = FirebaseAuth.getInstance();
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            usernameWrapper.getEditText().setText(data.getStringExtra("username"));
                            passwordWrapper.getEditText().setText(data.getStringExtra("password"));
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            txtUserStatus.setText(getString(R.string.google_status_fmt, user.getEmail()));
            txtUserDetail.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            btnLogin.setVisibility(View.GONE);
            btnRegister.setVisibility(View.GONE);
            btnLogout.setVisibility(View.VISIBLE);
            MainActivityIntentLauncher(); //Otomatik Ana Ekrana Geçiş
        } else {
            txtUserStatus.setText("Giriş yapılmadı.");
            txtUserDetail.setText(null);

            btnLogin.setVisibility(View.VISIBLE);
            btnRegister.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.GONE);
        }
    }

    private void initComponents() {
        btnLogin = findViewById(R.id.btnLogin);
        btnLogout = findViewById(R.id.btnLogout);
        btnRegister = findViewById(R.id.btnRegister);
        txtUserDetail = findViewById(R.id.txtUserDetail);
        txtUserStatus = findViewById(R.id.txtUserStatus);
        usernameWrapper = findViewById(R.id.usernameWrapper);
        passwordWrapper = findViewById(R.id.passwordWrapper);
    }

    private void registerEventHandlers() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isUsernameValid = validateEditText(usernameWrapper);
                boolean isPasswordValid = validateEditText(passwordWrapper);

                if(!isUsernameValid)
                    usernameWrapper.setError("Hatalı E-Posta Adresi!");
                else
                    usernameWrapper.setError(null);

                if(!isPasswordValid)
                    passwordWrapper.setError("Hatalı Şifre");
                else
                    passwordWrapper.setError(null);

                if(isUsernameValid && isPasswordValid){
                    String username = usernameWrapper.getEditText().getText().toString();
                    String password = passwordWrapper.getEditText().getText().toString();
                    mAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(LoginActivity.this,
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(LoginActivity.this, "Giriş başarılı", Toast.LENGTH_SHORT).show();
                                        MainActivityIntentLauncher();
                                    }else{
                                        Toast.makeText(LoginActivity.this, "Giriş Hatası", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                resultLauncher.launch(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(LoginActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean validateEditText(TextInputLayout wrapper) {
        String input = wrapper.getEditText().getText().toString();
        return input.length() > 3;
    }

    private void MainActivityIntentLauncher(){
        Intent intent = new Intent(LoginActivity.this,MainMenuActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}