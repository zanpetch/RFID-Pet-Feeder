package com.example.rfid;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

class ListHistoryDataAdapter extends BaseAdapter {
    private List<History> listHistorys = new ArrayList<>();
    private Context context;
    private Calendar calendar;
    private SimpleDateFormat mdformat;

    public ListHistoryDataAdapter(Context context,List<History> historys){
        this.listHistorys = historys;
        this.context = context;
        calendar = Calendar.getInstance();
        mdformat = new SimpleDateFormat("d-M-yyyy");
    }
    @Override
    public int getCount() {
        if (listHistorys == null){
            return 0;
        }
        return listHistorys.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

//    @Override
//    public boolean isEnabled(int position) {
//        return false;
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.list_historys_data_item,parent,false);

        TextView textDate = (TextView) view.findViewById(R.id.dateHistoryData);
        TextView textTime = (TextView) view.findViewById(R.id.timHistorydata) ;
        TextView textType = (TextView) view.findViewById(R.id.typeHistoryData);
        TextView textEat = (TextView) view.findViewById(R.id.eatHistoryData);

        if(listHistorys.get(position).getDate().equals(mdformat.format(calendar.getTime()))){
            if(listHistorys.get(position).getType().equals("eat")){
                textDate.setTextColor(view.getResources().getColor(R.color.eatColor));
                textTime.setTextColor(view.getResources().getColor(R.color.eatColor));
                textType.setTextColor(view.getResources().getColor(R.color.eatColor));
                textEat.setTextColor(view.getResources().getColor(R.color.eatColor));
            }else if(listHistorys.get(position).getType().equals("different pet")){
                textDate.setTextColor(view.getResources().getColor(R.color.differentColor));
                textTime.setTextColor(view.getResources().getColor(R.color.differentColor));
                textType.setTextColor(view.getResources().getColor(R.color.differentColor));
                textEat.setTextColor(view.getResources().getColor(R.color.differentColor));
            }else{
                textDate.setTextColor(view.getResources().getColor(R.color.emptyColor));
                textTime.setTextColor(view.getResources().getColor(R.color.emptyColor));
                textType.setTextColor(view.getResources().getColor(R.color.emptyColor));
                textEat.setTextColor(view.getResources().getColor(R.color.emptyColor));
            }
        }
        textDate.setText(listHistorys.get(position).getDate());
        textTime.setText(listHistorys.get(position).getTime());
        if (listHistorys.get(position).getType().equals("eat")){
            textType.setText("กินอาหาร");
            textEat.setText(listHistorys.get(position).getEatWeight()+" g.");
        }else if(listHistorys.get(position).getType().equals("different pet")){
            textType.setText("ตัวอื่นแทรกระหว่างกิน");
            textEat.setText(listHistorys.get(position).getEatWeight()+" g.");
        }else{
            textType.setText("อาหารหมด");
            textEat.setText("");
        }

        return view;
    }
}
