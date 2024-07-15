package com.eom.ustabul;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    ArrayList<Category> categoryList;
    LayoutInflater inflater;

    public CategoryAdapter(Context context, ArrayList<Category> categories){
        inflater = LayoutInflater.from(context);
        this.categoryList = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.activity_category_recyclerview, parent, false);
        CategoryViewHolder holder = new CategoryViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category selectedCategory = categoryList.get(position);
        holder.setData(selectedCategory, position);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder{
        MaterialTextView categoryName;
        MaterialButton removeButton;

        public CategoryViewHolder(View itemView){
            super(itemView);
            categoryName = (MaterialTextView) itemView.findViewById(R.id.categoryName);
            removeButton = (MaterialButton) itemView.findViewById(R.id.recyclerRemoveButton);
            registerEventHandlers();
        }

        public void setData(Category selectedCategory, int position){
            this.categoryName.setText(selectedCategory.getCategoryName());
            if(this.categoryName.getText().equals("Hiçbir kategoriye dahil değilsiniz.")){
                this.removeButton.setVisibility(View.GONE);
            }else{
                this.removeButton.setVisibility(View.VISIBLE);
            }
        }

        public void registerEventHandlers(){
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!categoryName.getText().equals("Hiçbir kategoriye dahil değilsiniz.")){
                        AlertDialog.Builder builder = new AlertDialog.Builder(categoryName.getContext());
                        builder.setTitle("Kategoriyi Listenizden Kaldırmak İstiyor Musunuz?");
                        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                DocumentReference dataRef = firestore.collection("Users").document(mAuth.getCurrentUser().getUid());
                                dataRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if(documentSnapshot.exists()){
                                            ArrayList<String> activeCategories = (ArrayList<String>) documentSnapshot.get("activeCategories");
                                            if(activeCategories != null && activeCategories.contains(categoryName.getText())){
                                                activeCategories.remove(categoryName.getText());
                                                Task<Void> dataRef = firestore.collection("Users").document(mAuth.getCurrentUser().getUid()).update("activeCategories",activeCategories).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        String city = documentSnapshot.getString("city");
                                                        DocumentReference dataRef1 = firestore.collection("RegisteredUsers").document(city);
                                                        dataRef1.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                ArrayList<String> category = (ArrayList<String>) documentSnapshot.get(categoryName.getText().toString());
                                                                for(int i = 0;i<category.size();i++){
                                                                    if(category.get(i).equals(mAuth.getCurrentUser().getUid())){
                                                                        category.remove(i);
                                                                        break;
                                                                    }
                                                                }
                                                                Task<Void> dataRef1 = firestore.collection("RegisteredUsers").document(city).update(categoryName.getText().toString(),category).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        updateUI();
                                                                        Toast.makeText(view.getContext(),"Silme İşlemi Başarılı", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
                            }
                        });
                        builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(view.getContext(),"İptal Edildi", Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.show();
                    }
                }
            });
        }

        public void updateUI(){
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            DocumentReference dataRef = firestore.collection("Users").document(mAuth.getCurrentUser().getUid());
            dataRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        ArrayList<String> activeCategories = (ArrayList<String>) documentSnapshot.get("activeCategories");
                        EditCategoriesActivity.categories.clear();
                        if(activeCategories == null || activeCategories.size() == 0){
                            EditCategoriesActivity.categories.add(new Category("Hiçbir kategoriye dahil değilsiniz."));
                        }else{
                            for(int i=0;i<activeCategories.size();i++){
                                EditCategoriesActivity.categories.add(new Category(activeCategories.get(i)));
                            }
                        }
                    }
                    EditCategoriesActivity.categoryAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
