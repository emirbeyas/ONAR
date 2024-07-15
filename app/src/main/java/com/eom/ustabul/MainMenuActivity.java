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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainMenuActivity extends AppCompatActivity {

    private MaterialButton btnUstaAra, btnUstaOl;
    ActivityResultLauncher<Intent> resultLauncher;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_UstaBul);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        initComponents();
        registerEventHandlers();
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK){

                        }
                    }
                });
    }

    private void initComponents() {
        btnUstaAra = findViewById(R.id.btnUstaAra);
        btnUstaOl = findViewById(R.id.btnUstaOl);
    }

    private void registerEventHandlers() {
        btnUstaAra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference dataRef = firestore.collection("Categories").document("AllCategories");
                dataRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            selectCategoryDialog((ArrayList<String>) documentSnapshot.get("CategoryList"));
                        }
                    }
                });
            }
        });

        btnUstaOl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenuActivity.this,EditCategoriesActivity.class);
                resultLauncher.launch(intent);
            }
        });
    }

    private void selectCategoryDialog(ArrayList<String> categoryList){
        String[] categoryList1 = categoryList.toArray(new String[categoryList.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Aramak istediğiniz kategoriyi seçiniz.");
        builder.setItems(categoryList1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DocumentReference dataRef = firestore.collection("Users").document(mAuth.getCurrentUser().getUid());
                dataRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            String city = documentSnapshot.getString("city");
                            DocumentReference dataRef1 = firestore.collection("RegisteredUsers").document(city);
                            dataRef1.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()){
                                        ArrayList<String> users = (ArrayList<String>) documentSnapshot.get(categoryList1[i]);
                                        Intent intent = new Intent(MainMenuActivity.this, UsersListActivity.class);
                                        intent.putExtra("users",users);
                                        resultLauncher.launch(intent);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        builder.setNegativeButton("Vazgeç", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(btnUstaAra.getContext(), "İşlem İptal Edildi", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_actionbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.menuProfile:
                Intent intent = new Intent(MainMenuActivity.this,ProfilePage.class);
                resultLauncher.launch(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}