package com.khoa.tudien.Activity;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.khoa.tudien.Model.Idioms;
import com.khoa.tudien.Controller.MyDataBase;
import com.khoa.tudien.R;
import com.khoa.tudien.Adapter.ViewPagerAdapter;

import java.util.ArrayList;

public class IdiomsActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ArrayList<Idioms> arrIdioms;
    private TextView txtCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idioms);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.idioms_English));
        getSupportActionBar().setElevation(0);
        arrIdioms = new MyDataBase(this).getAllIdioms();

        txtCount = findViewById(R.id.txt_count);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new ViewPagerAdapter(this, arrIdioms));

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                txtCount.setText(String.valueOf(i + 1) + "/100");
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(isFinishing()){
            overridePendingTransition(R.anim.popup_right,R.anim.popup_zoom_out);
        }
    }
}
