package com.khoa.tudien.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.khoa.tudien.Model.IrregularVerbs;
import com.khoa.tudien.R;

import java.util.ArrayList;

public class ListViewIrregularAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<IrregularVerbs> arrIv;

    public ListViewIrregularAdapter(Context context, ArrayList<IrregularVerbs> arr) {
        this.context = context;
        this.arrIv = arr;
    }


    @Override
    public int getCount() {
        return arrIv.size();
    }

    @Override
    public Object getItem(int position) {
        return arrIv.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_irregular_verbs, null);
            viewHolder = new ViewHolder();
            viewHolder.txtInfinitive = convertView.findViewById(R.id.txt_infinitive);
            viewHolder.txtSimple = convertView.findViewById(R.id.txt_simple);
            viewHolder.txtParticciple = convertView.findViewById(R.id.txt_particile);
            viewHolder.txtMeaning = convertView.findViewById(R.id.txt_meaning);
            convertView.setTag(viewHolder);
        } else {
           viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtInfinitive.setText(arrIv.get(position).getInfinitive());
        viewHolder.txtSimple.setText(arrIv.get(position).getSimple());
        viewHolder.txtParticciple.setText(arrIv.get(position).getParticiple());
        viewHolder.txtMeaning.setText(arrIv.get(position).getMeaning());

        return convertView;
    }

    class ViewHolder {
        private TextView txtInfinitive;
        private TextView txtSimple;
        private TextView txtParticciple;
        private TextView txtMeaning;
    }
}
