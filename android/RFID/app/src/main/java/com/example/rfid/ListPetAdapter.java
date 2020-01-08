package com.example.rfid;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class ListPetAdapter extends BaseAdapter {
    private ArrayList<Pet> listPets = new ArrayList<>();
    private Context context;

    public ListPetAdapter(Context context, ArrayList<Pet> listPets){
        this.context = context;
        this.listPets = listPets;
    }

    @Override
    public int getCount() {
        if (listPets == null){
            return 0;
        }
        return listPets.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.list_pets_item, parent, false);

        ImageView imagePet = (ImageView) view.findViewById(R.id.imagePetsItem);
        TextView namePet = (TextView) view.findViewById(R.id.namePetsItem);
        TextView typePet = (TextView) view.findViewById(R.id.typePetsItem);

        Log.i("listPets-getsize", "" + listPets.get(position).getTagID());
        if (!listPets.get(position).getTagID().equals("")) {
            imagePet.setImageResource(R.drawable.rfid_sensor);
        }

        namePet.setText(listPets.get(position).getName());
        if (listPets.get(position).getType().equals("dog")) {
            typePet.setText("สุนัข");
        } else {
            typePet.setText("แมว");
        }

        return view;
    }
}
