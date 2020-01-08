package com.example.rfid;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListHistoryAdapter extends BaseAdapter {
    private ArrayList<Pet> listPets = new ArrayList<>();
    private Context context;

    public ListHistoryAdapter(Context context, ArrayList<Pet> listPets){
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
        view = inflater.inflate(R.layout.list_historys_item,parent,false);

        TextView textName = (TextView) view.findViewById(R.id.nameHistorysItem);
        TextView textEat = (TextView) view.findViewById(R.id.eatHistorysItem);

        Log.i("eatTotals-init",listPets.get(position).getCurrentEatWeight()+"");
        if (listPets.get(position).getTagID().equals("")) {
            textEat.setTextColor(view.getResources().getColor(R.color.noTagColor));
            textEat.setText("ไม่มี Tag");
        }else {
            if (listPets.get(position).getHistorys().size() == 0){
                textEat.setTextColor(view.getResources().getColor(R.color.eatColor   ));
                if (listPets.get(position).getEatType().equals("noLimit")) {
                    textEat.setText("0" + " / " + "ไม่จำกัด");
                } else {
                    textEat.setText("0"+" / "+listPets.get(position).getEatWeight()+" g.");
                }
            }else {
                if (listPets.get(position).getEatType().equals("noLimit")) {
                    textEat.setTextColor(view.getResources().getColor(R.color.eatColor));
                    textEat.setText(listPets.get(position).getCurrentEatWeight() + " / " + "ไม่จำกัด");
                }else {
                    if (listPets.get(position).getCurrentEatWeight() < listPets.get(position).getEatWeight()){
                        textEat.setTextColor(view.getResources().getColor(R.color.eatColor));
                    }else{
                        textEat.setTextColor(view.getResources().getColor(R.color.emptyColor));
                    }
                    textEat.setText(listPets.get(position).getCurrentEatWeight()+" / "+listPets.get(position).getEatWeight()+" g.");
                }
            }
        }
        textName.setText(listPets.get(position).getName());

        return view;
    }


}
