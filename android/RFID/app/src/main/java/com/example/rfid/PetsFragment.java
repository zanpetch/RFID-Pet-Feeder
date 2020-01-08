package com.example.rfid;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class PetsFragment extends Fragment {
    private FirebaseDatabase database;
    private DatabaseReference dataPets;
    private ListView listPets;
    private ArrayList<Pet> pets = new ArrayList<>();
    private FloatingActionButton floatButton;
    private ListPetAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pets, container, false);
        database = FirebaseDatabase.getInstance();
        dataPets = database.getReference().child("pets");
        adapter = new ListPetAdapter(getActivity().getApplicationContext(),pets);
        listPets = (ListView) view.findViewById(R.id.listView_pets);
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
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(),"Failed to read data",Toast.LENGTH_SHORT).show();
            }
        });

        listPets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailPetActivity.class);
                intent.putExtra("primaryKey",pets.get(position).getPrimaryKey());
                startActivity(intent);
            }
        });

        floatButton = (FloatingActionButton) view.findViewById(R.id.floatButton);
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddPetActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }


}
