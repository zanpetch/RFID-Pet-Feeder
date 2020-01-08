package com.example.rfid;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class HistoryFragment extends Fragment {
    private FirebaseDatabase database;
    private DatabaseReference dataPets;
    private DatabaseReference dataHistorys;
    private float eatTotal;
    private Calendar calendar;
    private SimpleDateFormat mdformat;

    private ListView listPets;
    private ArrayList<Pet> pets = new ArrayList<>();
    //private List<String> keys = new ArrayList<>();
    private List<String> eatTotals = new ArrayList<>();
    private ListHistoryAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_history, container, false);

        calendar = Calendar.getInstance();
        mdformat = new SimpleDateFormat("d-M-yyyy");
        eatTotal = 0;

        database = FirebaseDatabase.getInstance();
        dataPets = database.getReference().child("pets");
        dataHistorys = database.getReference().child("historys");
        adapter = new ListHistoryAdapter(getActivity().getApplicationContext(),pets);
        listPets = (ListView) view.findViewById(R.id.listView_historys);

        dataPets.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pets.clear();
                for (final DataSnapshot ds : dataSnapshot.getChildren()){
                    Pet pet = ds.getValue(Pet.class);
                    pet.setPrimaryKey(ds.getKey());
                    pets.add(pet);
                }
                Collections.reverse(pets);
                listPets.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to read data", Toast.LENGTH_SHORT).show();
            }
        });

        dataHistorys.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot petKey : dataSnapshot.getChildren()){
                    int index = 0;
                    for(int i = 0; i < pets.size(); i++){
                       if (pets.get(i).getPrimaryKey().equals(petKey.getKey())) {
                           index = i;
                           break;
                       }
                    }
                    for(DataSnapshot date : petKey.getChildren()) {
                        eatTotal = 0;
                        if (date.getKey().equals(mdformat.format(calendar.getTime()))) {
                            pets.get(index).setCurrentEatWeight(0);
                        }
                        pets.get(index).getHistorys().put(date.getKey(), new ArrayList<History>());
                        for(DataSnapshot history : date.getChildren()){
                            pets.get(index).getHistorys().get(date.getKey()).add(history.getValue(History.class));
                            if (date.getKey().equals(mdformat.format(calendar.getTime()))){
                                pets.get(index).setCurrentEatWeight(pets.get(index).getCurrentEatWeight()+history.child("eatWeight").getValue(int.class));
                            }
                        }
                    }

                }

                try{
                    listPets.setAdapter(new ListHistoryAdapter(getActivity().getApplicationContext(),pets));
                }catch (NullPointerException e){
                    Log.i("NullExcep-petsHF",""+e);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to read data", Toast.LENGTH_SHORT).show();
            }
        });

        listPets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(),HistoryActivity.class);
                intent.putExtra("key",pets.get(position).getPrimaryKey());
                intent.putExtra("name",pets.get(position).getName());
                startActivity(intent);
            }
        });

        return view;
    }
}
