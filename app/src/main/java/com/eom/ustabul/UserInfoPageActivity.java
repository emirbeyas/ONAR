package com.eom.ustabul;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

public class UserInfoPageActivity extends AppCompatActivity {
    MaterialTextView txtNameSurname, txtPhone;
    MaterialButton btnCall, btnMessage;
    private final int REQUEST_PERMISSION_CALL_PHONE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_UstaBul);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_page);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initComponents();
        registerEventHandlers();
    }

    void initComponents(){
        txtNameSurname = findViewById(R.id.userInfoPage_txtNameSurnameText);
        txtPhone = findViewById(R.id.userInfoPage_txtPhoneText);
        btnCall = findViewById(R.id.userInfoPage_Arama);
        btnMessage = findViewById(R.id.userInfoPage_MesajAt);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String name = bundle.getString("name");
        String surname = bundle.getString("surname");
        String phone = bundle.getString("phone");
        txtNameSurname.setText(name+" "+surname);
        txtPhone.setText(phone);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    void registerEventHandlers(){
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCallPhonePermission();
                String phone = txtPhone.getText().toString();
                String uri = "tel:+90" + phone ;
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });

        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = txtPhone.getText().toString();
                String uri = "smsto:+90" + phone ;
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void showCallPhonePermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                btnCall.getContext(), Manifest.permission.CALL_PHONE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(UserInfoPageActivity.this,
                    Manifest.permission.CALL_PHONE)) {
                showExplanation("Permission Needed", "Rationale", Manifest.permission.CALL_PHONE, REQUEST_PERMISSION_CALL_PHONE);
            } else {
                requestPermission(Manifest.permission.CALL_PHONE, REQUEST_PERMISSION_CALL_PHONE);
            }
        } else {
            Toast.makeText(UserInfoPageActivity.this, "Permission (already) Granted!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_CALL_PHONE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(UserInfoPageActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UserInfoPageActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }
}