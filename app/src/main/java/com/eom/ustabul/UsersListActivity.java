package com.eom.ustabul;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class UsersListActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    RecyclerView recyclerView;
    static ArrayList<User> users;
    static UserAdapter userAdapter;
    static ActivityResultLauncher<Intent> resultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_UstaBul);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponents();
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        users = new ArrayList<>();
        userAdapter = new UserAdapter(this,users);
        recyclerView.setAdapter(userAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK){

                        }
                    }
                });
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        ArrayList<String> userList = bundle.getStringArrayList("users");
        updateUI(userList);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initComponents(){
        recyclerView = findViewById(R.id.categoriesRecyclerView);
    }

    public void updateUI(ArrayList<String> userList){
        if(userList == null || userList.size() == 0){
            users.add(new User("Hiçbir sonuç", "bulunamadı.",null,null,null,null));
            userAdapter.notifyDataSetChanged();
        }
        else if(userList.size() > 0){
            for(int i = 0;i<userList.size();i++){
                DocumentReference dataRef = firestore.collection("Users").document(userList.get(i));
                dataRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            users.add(new User(documentSnapshot.getString("name"),
                                    documentSnapshot.getString("surname"),
                                    documentSnapshot.getString("mail"),
                                    documentSnapshot.getString("phone"),
                                    (ArrayList<String>) documentSnapshot.get("activeCategories"),documentSnapshot.getString("city")));
                            userAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}