package com.khoa.tudien.Adapter;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.khoa.tudien.Model.DialogWord;
import com.khoa.tudien.R;
import com.khoa.tudien.Model.Word;

import java.util.ArrayList;
import java.util.Locale;

public class RecycleViewWordAdapter extends RecyclerView.Adapter<RecycleViewWordAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Word> arrWord;
    private TextToSpeech textToSpeech;
    private String tableName;

    public RecycleViewWordAdapter(Context context, String tableName, ArrayList<Word> arr){
        this.arrWord = arr;
        this.context = context;
        this.tableName = tableName;
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setSpeechRate((float) 0.8);
                }
            }
        });
    }

    @NonNull
    @Override
    public RecycleViewWordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_word_history, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Word word = arrWord.get(i);
        final int position = i;
        viewHolder.txtWord.setText(word.getWord());
        viewHolder.txtWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogWord(context, tableName, arrWord).show(position);
            }
        });
        viewHolder.ibtnSpell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech.speak(word.getWord(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrWord.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtWord;
        private ImageButton ibtnSpell;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtWord = itemView.findViewById(R.id.txt_word);
            ibtnSpell = itemView.findViewById(R.id.action_spell);
        }
    }
}
