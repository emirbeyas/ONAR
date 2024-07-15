package com.eom.ustabul;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
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

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    ArrayList<User> userList;
    LayoutInflater inflater;

    public UserAdapter(Context context, ArrayList<User> users){
        inflater = LayoutInflater.from(context);
        this.userList = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.activity_users_recyclerview, parent, false);
        UserViewHolder holder = new UserViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User selectedUser = userList.get(position);
        holder.setData(selectedUser, position);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{
        MaterialTextView userName;
        MaterialButton showButton;
        User userInfo;

        public UserViewHolder(View itemView){
            super(itemView);
            userName = (MaterialTextView) itemView.findViewById(R.id.recycler_userName);
            showButton = (MaterialButton) itemView.findViewById(R.id.recycler_ShowButton);
            registerEventHandlers();
        }

        public void setData(User selectedUser, int position){
            userInfo = selectedUser;
            this.userName.setText(selectedUser.getName()+" "+selectedUser.getSurname());
            if(this.userName.getText().equals("Hiçbir sonuç bulunamadı.")){
                this.showButton.setVisibility(View.GONE);
            }else{
                this.showButton.setVisibility(View.VISIBLE);
            }
        }

        public void registerEventHandlers(){
            showButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(showButton.getContext(),UserInfoPageActivity.class);
                    intent.putExtra("name", userInfo.getName());
                    intent.putExtra("surname", userInfo.getSurname());
                    intent.putExtra("phone", userInfo.getPhone());
                    UsersListActivity.resultLauncher.launch(intent);
                }
            });
        }
    }
}
