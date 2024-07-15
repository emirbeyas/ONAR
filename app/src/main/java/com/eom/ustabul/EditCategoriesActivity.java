package com.eom.ustabul;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditCategoriesActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    static ArrayList<Category> categories;
    static CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_UstaBul);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_categories);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponents();
        registerEventHandlers();
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        categories = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(this,categories);
        recyclerView.setAdapter(categoryAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initComponents(){
        recyclerView = findViewById(R.id.categoriesRecyclerView);
        floatingActionButton = findViewById(R.id.addCategoryFloatingActionButton);
    }

    private void registerEventHandlers(){
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference dataRef = firestore.collection("Categories").document("AllCategories");
                dataRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            addCategoryDialog((ArrayList<String>) documentSnapshot.get("CategoryList"));
                        }
                    }
                });

            }
        });
    }

    private void addCategoryDialog(ArrayList<String> categoryList){
        String[] categoryList1 = categoryList.toArray(new String[categoryList.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bir kategori seçiniz");
        builder.setItems(categoryList1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DocumentReference dataRef = firestore.collection("Users").document(mAuth.getCurrentUser().getUid());
                dataRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            ArrayList<String> activeCategories = (ArrayList<String>) documentSnapshot.get("activeCategories");
                            String city = documentSnapshot.getString("city");
                            if(activeCategories == null){
                                activeCategories = new ArrayList<>();
                                activeCategories.add(categoryList1[i]);
                                addCategory(activeCategories,city);
                            }else if(!activeCategories.contains(categoryList1[i])){
                                activeCategories.add(categoryList1[i]);
                                addCategory(activeCategories,city);
                            }
                            else{
                                Toast.makeText(EditCategoriesActivity.this, "Bu kategoriye zaten dahilsiniz.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
        builder.setNegativeButton("Vazgeç", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(EditCategoriesActivity.this, "İşlem İptal Edildi", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    public void updateUI(){
        DocumentReference dataRef = firestore.collection("Users").document(mAuth.getCurrentUser().getUid());
        dataRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    ArrayList<String> activeCategories = (ArrayList<String>) documentSnapshot.get("activeCategories");
                    categories.clear();
                    if(activeCategories == null || activeCategories.size() == 0){
                        categories.add(new Category("Hiçbir kategoriye dahil değilsiniz."));
                    }else{
                            for(int i=0;i<activeCategories.size();i++){
                                categories.add(new Category(activeCategories.get(i)));
                            }
                        }
                    }
                categoryAdapter.notifyDataSetChanged();
                }
            });
    }

    private void addCategory(ArrayList<String> categoryList, String city){
        String userId = mAuth.getCurrentUser().getUid();
        Task<Void> dataRef = firestore.collection("Users").document(userId).update("activeCategories",categoryList).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                ArrayList<String> user = new ArrayList<>();
                user.add(userId);
                Map<String, Object> update = new HashMap<>();
                update.put(categoryList.get(categoryList.size()-1),user);
                Task<Void> dataRef1 = firestore.collection("RegisteredUsers").document(city).set(update, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(EditCategoriesActivity.this, "Kategoriye Dahil Olundu", Toast.LENGTH_SHORT).show();
                        updateUI();
                    }
                });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}