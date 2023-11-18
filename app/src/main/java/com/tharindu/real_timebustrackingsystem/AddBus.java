package com.tharindu.real_timebustrackingsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddBus extends AppCompatActivity {

    private EditText busName;
    private EditText busRoute;
    private EditText busNo;
    private Button busRegister;
    private EditText busId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bus);

        busName=findViewById(R.id.edtBusName);
        busNo=findViewById(R.id.edtBusNo);
        busRoute=findViewById(R.id.edtBusRoute);
        busRegister=findViewById(R.id.btnBusRegister);
        busId=findViewById(R.id.edtBusId);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();
        busId.setText(userId);


        busRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(userId);

                String busNameValue=busName.getText().toString();
                String busNoValue=busNo.getText().toString();
                String busRouteValue=busRoute.getText().toString();
                if (busNameValue!="" && busNoValue!="" && busRouteValue!= ""){
                    myRef.child("busName").setValue(busNameValue);
                    myRef.child("busRoute").setValue(busRouteValue);
                    myRef.child("busNo").setValue(busNoValue);
                    myRef.child("currentLatitude").setValue(0);
                    myRef.child("currentLongitude").setValue(0);
                    myRef.child("latitude").setValue(0);
                    myRef.child("longitude").setValue(0);
                    myRef.child("isStop").setValue(false);
                    busName.setText("");
                    busNo.setText("");
                    busRoute.setText("");
                }
            }
        });
    }
}