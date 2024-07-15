package com.eom.ustabul;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EditUserInfoActivity extends AppCompatActivity {

    MaterialButton btnUpdate, btnCancel;
    TextInputLayout nameWrapper, surnameWrapper, phoneWrapper;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private User updateUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_UstaBul);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);
        initComponents();
        registerEventHandlers();
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        updateUI(user);
    }

    private void updateUI(FirebaseUser user) {
        if(user!=null){
            DocumentReference dataRef = firestore.collection("Users").document(user.getUid());
            dataRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        updateUser = new User(documentSnapshot.getString("name"),
                                documentSnapshot.getString("surname"),
                                documentSnapshot.getString("mail"),
                                documentSnapshot.getString("phone"),
                                (ArrayList<String>) documentSnapshot.get("activeCategories"),documentSnapshot.getString("city"));
                        nameWrapper.getEditText().setText(updateUser.getName());
                        surnameWrapper.getEditText().setText(updateUser.getSurname());
                        phoneWrapper.getEditText().setText(updateUser.getPhone());
                    }
                }
            });
        }
    }

    private void initComponents() {
        btnUpdate = findViewById(R.id.editUserInfo_btnUpdate);
        btnCancel = findViewById(R.id.editUserInfo_btnCancel);
        nameWrapper = findViewById(R.id.editUserInfo_NameWrapper);
        surnameWrapper = findViewById(R.id.editUserInfo_SurnameWrapper);
        phoneWrapper = findViewById(R.id.editUserInfo_PhoneWrapper);
    }

    private void registerEventHandlers() {
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isNameValid = validateEditText(nameWrapper);
                boolean isSurnameValid = validateEditText(surnameWrapper);
                boolean isPhoneValid = validateEditText(phoneWrapper);

                if(!isNameValid)
                    nameWrapper.setError("Hatalı Ad");
                else
                    nameWrapper.setError(null);

                if(!isSurnameValid)
                    surnameWrapper.setError("Hatalı Soyad");
                else
                    surnameWrapper.setError(null);

                if(!isPhoneValid)
                    phoneWrapper.setError("Hatalı Telefon Numarası");
                else
                    phoneWrapper.setError(null);

                if(isNameValid && isSurnameValid && isPhoneValid){
                    String name = nameWrapper.getEditText().getText().toString();
                    String surname = surnameWrapper.getEditText().getText().toString();
                    String phone = phoneWrapper.getEditText().getText().toString();
                    updateUser.setName(name);
                    updateUser.setSurname(surname);
                    updateUser.setPhone(phone);
                    firestore.collection("Users").document(mAuth.getCurrentUser().getUid()).set(updateUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(EditUserInfoActivity.this, "Güncelleme Tamam", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            setResult(Activity.RESULT_OK,intent);
                            finish();
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
}