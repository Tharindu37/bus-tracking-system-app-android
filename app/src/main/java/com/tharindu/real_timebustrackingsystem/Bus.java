package com.tharindu.real_timebustrackingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tharindu.real_timebustrackingsystem.adapter.BusAdapter;
import com.tharindu.real_timebustrackingsystem.adapter.SelectListener;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class Bus extends AppCompatActivity implements SelectListener, PopupMenu.OnMenuItemClickListener {

    private TextView signOut;
    private RecyclerView busLocations;
    private List<com.tharindu.real_timebustrackingsystem.model.Bus> buses;

    private ImageView menuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_bus);

//        signOut=findViewById(R.id.btnSignOut);

        busLocations=findViewById(R.id.bus_location);

//        menuButton=findViewById(R.id.btnMenu);
//        registerForContextMenu(menuButton);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("");

        buses=new ArrayList<com.tharindu.real_timebustrackingsystem.model.Bus>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                buses.clear();
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
//                com.tharindu.real_timebustrackingsystem.model.Bus bus = dataSnapshot.getValue(com.tharindu.real_timebustrackingsystem.model.Bus.class);
//                System.out.println("fffffffffffdddddddddddddddd"+bus.getName());

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                    String  id = childSnapshot.getKey();
                    String busName = childSnapshot.child("busName").getValue(String.class);
                    String route = childSnapshot.child("busRoute").getValue(String.class);
                    String no = childSnapshot.child("busNo").getValue(String.class);
                    buses.add(new com.tharindu.real_timebustrackingsystem.model.Bus(id,busName,route,no));
                }
                BusAdapter busAdapter = new BusAdapter(Bus.this, buses, Bus.this);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(Bus.this, 1, GridLayoutManager.VERTICAL, false);
                busLocations.setLayoutManager(gridLayoutManager);
                busLocations.setAdapter(busAdapter);

            }


            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });
//        BusAdapter busAdapter=new BusAdapter(this,buses,this);
//        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,1,GridLayoutManager.VERTICAL,false);
//        busLocations.setLayoutManager(gridLayoutManager);
//        busLocations.setAdapter(busAdapter);



//        buses.add(new com.tharindu.real_timebustrackingsystem.model.Bus("1","Bus 1","route","102"));
//        buses.add(new com.tharindu.real_timebustrackingsystem.model.Bus("2","Bus 2","route","102"));
//        buses.add(new com.tharindu.real_timebustrackingsystem.model.Bus("2","Bus 3","route","102"));




//        signOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseAuth.getInstance().signOut();
//                Intent intent=new Intent(getApplicationContext(),Login.class);
//                startActivity(intent);
//            }
//        });
    }

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        menu.setHeaderTitle("Choose");
//        getMenuInflater().inflate(R.menu.menu, menu);
//    }
//
//    @Override
//    public boolean onContextItemSelected(@NonNull MenuItem item) {
////        switch (item.getItemId()) {
////            case R.id.add:
////                Toast.makeText(this, "add", Toast.LENGTH_SHORT).show();
////            case R.id.logout:
////                Toast.makeText(this, "logout", Toast.LENGTH_SHORT).show();
////            default:
////                return super.onContextItemSelected(item);
////        }
//        return super.onContextItemSelected(item);
//    }

    @Override
    public void onBusClicked(com.tharindu.real_timebustrackingsystem.model.Bus bus) {
        Intent intent=new Intent(getApplicationContext(), Map.class);
        intent.putExtra("ID",bus.getId());
        startActivity(intent);
    }

    public void showPopup(View v){
        PopupMenu popupMenu=new PopupMenu(this,v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.menu);
        popupMenu.show();
    }


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.toString().equalsIgnoreCase("Logout")){
            FirebaseAuth.getInstance().signOut();
            Intent intent=new Intent(getApplicationContext(),Login.class);
            startActivity(intent);
            return true;
        }
        if (menuItem.toString().equalsIgnoreCase("Register Bus")){
            Intent intent=new Intent(getApplicationContext(),AddBus.class);
            startActivity(intent);
            return true;
        }
        return false;
    }


}