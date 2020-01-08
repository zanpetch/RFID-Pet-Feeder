package com.example.rfid;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference dataHistorys;
    private String key;
    private String petName;

    private TextView textPetName;

    private ActionBar actionBar;
    private ListView listHistorys;
    private List<History> historys = new ArrayList<>();
    private ListHistoryDataAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance();
        dataHistorys = database.getReference().child("historys");
        adapter = new ListHistoryDataAdapter(this,historys);
        listHistorys = (ListView) findViewById(R.id.listView_historys_pet);
        textPetName = (TextView) findViewById(R.id.nameHistorysData);

        key = getIntent().getStringExtra("key");
        Log.i("HistoryAct-key",key);
        petName = getIntent().getStringExtra("name");
        Log.i("HistoryAct-petName",petName);

        textPetName.setText(petName);

        dataHistorys.child(key).orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                historys.clear();
                for (DataSnapshot date : dataSnapshot.getChildren()){
                    Log.i("HistoryAct-dateKey",date.getKey());
                    for (DataSnapshot data : date.getChildren()){
                        Log.i("HistoryAct-dataKey",data.getKey());
                        History history = data.getValue(History.class);
                        history.setDate(date.getKey());
                        historys.add(history);
                    }
                }
                Collections.reverse(historys);
                listHistorys.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HistoryActivity.this,"Failed to read data",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
