package com.eom.ustabul;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    MaterialButton btnNext, btnCancel;
    TextInputLayout usernameWrapper, passwordWrapper, secondPasswordWrapper, nameWrapper, surnameWrapper, phoneWrapper;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_UstaBul);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initComponents();
        registerEventHandlers();
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void initComponents() {
        btnNext = findViewById(R.id.register_btnNext);
        btnCancel = findViewById(R.id.register_btnCancel);
        usernameWrapper = findViewById(R.id.register_UsernameWrapper);
        passwordWrapper = findViewById(R.id.register_PasswordWrapper);
        secondPasswordWrapper = findViewById(R.id.register_SecondPasswordWrapper);
        nameWrapper = findViewById(R.id.register_NameWrapper);
        surnameWrapper = findViewById(R.id.register_SurnameWrapper);
        phoneWrapper = findViewById(R.id.register_PhoneWrapper);
    }

    private void registerEventHandlers() {
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isUsernameValid = validateEditText(usernameWrapper);
                boolean isPasswordValid = validateEditText(passwordWrapper);
                boolean isNameValid = validateEditText(nameWrapper);
                boolean isSurnameValid = validateEditText(surnameWrapper);
                boolean isPhoneValid = validateEditText(phoneWrapper);
                boolean isSecondPasswordValid = validateSecondPassword(passwordWrapper,secondPasswordWrapper);
                if(!isUsernameValid)
                    usernameWrapper.setError("Hatalı E-Posta Adresi!");
                else
                    usernameWrapper.setError(null);

                if(!isPasswordValid)
                    passwordWrapper.setError("Hatalı Şifre");
                else
                    passwordWrapper.setError(null);

                if(!isNameValid)
                    passwordWrapper.setError("Hatalı Ad");
                else
                    passwordWrapper.setError(null);

                if(!isSurnameValid)
                    passwordWrapper.setError("Hatalı Soyad");
                else
                    passwordWrapper.setError(null);

                if(!isPhoneValid)
                    passwordWrapper.setError("Hatalı Telefon Numarası");
                else
                    passwordWrapper.setError(null);

                if(!isSecondPasswordValid)
                    secondPasswordWrapper.setError("İki Şifre Eşleşmiyor");
                else
                    secondPasswordWrapper.setError(null);

                if(isUsernameValid && isPasswordValid && isSecondPasswordValid && isNameValid && isSurnameValid && isPhoneValid){
                    String username = usernameWrapper.getEditText().getText().toString();
                    String password = passwordWrapper.getEditText().getText().toString();
                    String name = nameWrapper.getEditText().getText().toString();
                    String surname = surnameWrapper.getEditText().getText().toString();
                    String phone = phoneWrapper.getEditText().getText().toString();
                    ArrayList<String> categoryList = new ArrayList<String>();

                    DocumentReference dataRef = firestore.collection("Cities").document("AllCities");
                    dataRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                ArrayList<String> cityList = (ArrayList<String>) documentSnapshot.get("CityList");
                                if(cityList.size() > 0) {
                                    String[] cityList1 = cityList.toArray(new String[cityList.size()]);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(btnNext.getContext());
                                    builder.setTitle("Bir şehir seçiniz");
                                    builder.setItems(cityList1, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            mAuth.createUserWithEmailAndPassword(username,password).addOnSuccessListener(RegisterActivity.this,
                                                    new OnSuccessListener<AuthResult>() {
                                                        @Override
                                                        public void onSuccess(AuthResult authResult) {
                                                            User newUser = new User(name, surname, username, phone, null, cityList1[i]);
                                                            String userId = mAuth.getCurrentUser().getUid();
                                                            firestore.collection("Users").document(userId).set(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    Toast.makeText(RegisterActivity.this, "Kayıt Tamam", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent();
                                                                    intent.putExtra("username",username);
                                                                    intent.putExtra("password",password);
                                                                    setResult(Activity.RESULT_OK,intent);
                                                                    finish();
                                                                }
                                                            });
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(btnNext.getContext(), "Kayıt Hatası!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    });
                                    builder.setNegativeButton("Vazgeç", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Toast.makeText(view.getContext(), "İşlem İptal Edildi", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    builder.show();
                                }
                                }
                            }
                        });
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(Activity.RESULT_CANCELED,intent);
                finish();
            }
        });
    }

    private boolean validateEditText(TextInputLayout wrapper) {
        String input = wrapper.getEditText().getText().toString();
        return input.length() > 3;
    }

    private boolean validateSecondPassword(TextInputLayout wrapper, TextInputLayout secondWrapper) {
        String input = wrapper.getEditText().getText().toString();
        String input2 = secondWrapper.getEditText().getText().toString();
        return input.equals(input2);
    }
}