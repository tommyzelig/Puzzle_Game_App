package com.example.puzzlegame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class HighscoreAdapter extends ArrayAdapter<HighScore> {

    private Context context;
    private List<HighScore> highScores;

    public HighscoreAdapter(@NonNull Context context, List<HighScore> highScores) {
        super(context, 0 , highScores);
        this.context = context;
        this.highScores = highScores;
    }

    SimpleDateFormat df = new SimpleDateFormat("yy-MM-dd hh:mm:ss");

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);

        HighScore highScore = highScores.get(position);

        TextView name = (TextView) listItem.findViewById(R.id.points);
        String points= context.getString(R.string.points);
        name.setText(points+" " + highScore.getPoints());

        TextView level = (TextView) listItem.findViewById(R.id.level);
        String levels= context.getString(R.string.level);
        level.setText(levels+" " + highScore.getLevel());

        String timeString = df.format(new Date(highScore.getTime()));
        TextView time = (TextView) listItem.findViewById(R.id.time);
        String times= context.getString(R.string.time);
        time.setText(times+" " + timeString);

        return listItem;
    }
}