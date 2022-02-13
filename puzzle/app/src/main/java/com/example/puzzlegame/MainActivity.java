package com.example.puzzlegame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView easy  ,med, hard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        easy = findViewById(R.id.easy);
        med = findViewById(R.id.med);
        hard = findViewById(R.id.hard);


        easy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,PlayActivity.class);
                intent.putExtra(Constants.MSG,Constants.EASY);
                startActivity(intent);
            }
        });


        med.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,PlayActivity.class);
                intent.putExtra(Constants.MSG,Constants.MED);
                startActivity(intent);

            }
        });

        hard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,PlayActivity.class);
                intent.putExtra(Constants.MSG,Constants.HARD);
                startActivity(intent);

            }
        });
        TextView btn=findViewById(R.id.highscores);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,HighscoreActivity.class);
                startActivity(intent);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);

        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.highscores) {
            startActivity(new Intent(this, HighscoreActivity.class));
        }
        return true;
    }
}