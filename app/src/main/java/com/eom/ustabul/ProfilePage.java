package com.eom.ustabul;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ProfilePage extends AppCompatActivity {

    MaterialTextView txtNameSurname, txtMail, txtPhone, txtCity;
    MaterialButton btnLogout, btnEdit, btnCityChange;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    ActivityResultLauncher<Intent> resultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_UstaBul);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponents();
        registerEventHandlers();
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        }
                    }
                });
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
                        txtNameSurname.setText(documentSnapshot.getString("name")+" "+documentSnapshot.getString("surname"));
                        txtMail.setText(documentSnapshot.getString("mail"));
                        txtPhone.setText(documentSnapshot.getString("phone"));
                        txtCity.setText(documentSnapshot.getString("city"));
                    }
                }
            });
        }
    }

    private void registerEventHandlers() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Toast.makeText(ProfilePage.this, "Çıkış Başarılı", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfilePage.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( ProfilePage.this, EditUserInfoActivity.class);
                resultLauncher.launch(intent);
            }
        });

        btnCityChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference dataRef = firestore.collection("Cities").document("AllCities");
                dataRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            ArrayList<String> cityList = (ArrayList<String>) documentSnapshot.get("CityList");
                            if(cityList.size() > 0) {
                                String[] cityList1 = cityList.toArray(new String[cityList.size()]);
                                AlertDialog.Builder builder = new AlertDialog.Builder(btnCityChange.getContext());
                                builder.setTitle("Bir şehir seçiniz");
                                builder.setItems(cityList1, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Task<Void> dataRef = firestore.collection("Users").document(mAuth.getCurrentUser().getUid()).update("city",cityList1[i]).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(btnCityChange.getContext(), "Şehir Değiştirildi.", Toast.LENGTH_SHORT).show();
                                                updateUI(mAuth.getCurrentUser());
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
        });
    }

    private void initComponents(){
        btnLogout = findViewById(R.id.profilePage_Logout);
        txtMail = findViewById(R.id.profilePage_txtMailText);
        txtNameSurname = findViewById(R.id.profilePage_txtNameSurnameText);
        txtPhone = findViewById(R.id.profilePage_txtPhoneText);
        btnEdit = findViewById(R.id.profilePage_Edit);
        txtCity = findViewById(R.id.profilePage_txtCityText);
        btnCityChange = findViewById(R.id.profilePage_CityChange);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}