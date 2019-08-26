package com.khoa.tudien.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.khoa.tudien.Model.Idioms;
import com.khoa.tudien.R;

import java.util.ArrayList;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<Idioms> arrIdioms;

    public ViewPagerAdapter(Context context, ArrayList<Idioms> arrIdioms){
        this.context = context;
        this.arrIdioms = arrIdioms;
    }

    @Override
    public int getCount() {
        return arrIdioms.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = View.inflate(context, R.layout.item_viewpager, null);
        TextView txtSentence = view.findViewById(R.id.txt_sentence);
        TextView txtMeaning = view.findViewById(R.id.txt_meaning);

        txtSentence.setText("\"" + arrIdioms.get(position).getSentence() + "\"");
        txtMeaning.setText("- " + arrIdioms.get(position).getMeaning());
        container.addView(view);

        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
