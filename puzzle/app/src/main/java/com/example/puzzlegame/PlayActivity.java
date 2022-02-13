package com.example.puzzlegame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlayActivity extends AppCompatActivity {

    //time and level...
    private TextView level , time;
    public static int timer = 50000; // 50 sec
    CountDownTimer countDownTimer;

    boolean isRunning = true;
    boolean isSoundEnabled = false;


    private List<ImageView> buttons = new ArrayList<>();


    final class Card {
        public ImageView first;
        public ImageView second;
        public int drawable;
        public boolean identified;

        public boolean isIdentified(View current) {
            if (current == first) {
                return (boolean)second.getTag() == true;
            }
            if (current == second) {
                return (boolean)first.getTag() == true;
            }
            return false;
        }

        public void setAsIdentified() {
            first.setClickable(false);
            second.setClickable(false);
            identified = true;
        }

        public void hide() {
            setImageToBox(first);
            setImageToBox(second);
        }
    }

    List<Card> cards = new ArrayList<>();

    private boolean areAllIdentified() {
        for(Card card : cards) {
            if (!card.identified) {
                return false;
            }
        }
        return true;
    }

    private void setImageToBox(ImageView view) {
        view.setImageResource(R.drawable.qmarkcard);
        view.setTag(false);
    }



    private int dpToPx(int dp) {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private List<Card> buildCards(int[] resourceIds) {
        ArrayList<Card> cards = new ArrayList<>();
        for(int resourceId : resourceIds) {
            Card card = new Card();
            card.drawable = resourceId;
            cards.add(card);
        }
        return cards;
    }

    Card firstCardOfPair = null;

    private ImageView createImageView(Card card) {
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dpToPx(130));
        params.weight = 1;
        params.setMarginStart(dpToPx(1));
        params.setMarginEnd(dpToPx(1));
        imageView.setLayoutParams(params);
       // imageView.setBackgroundResource(R.drawable.box_border);
        imageView.setImageDrawable(ContextCompat.getDrawable(this, card.drawable));
        imageView.setClickable(false);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isSoundEnabled) {
                    return;
                }

                MediaPlayer mediaPlayer = MediaPlayer.create(PlayActivity.this, R.raw.tapmusic);
                mediaPlayer.start();

                //set real image...
               // imageView.setBackgroundResource(R.drawable.box_border);
                imageView.setImageDrawable(ContextCompat.getDrawable(PlayActivity.this, card.drawable));
                imageView.setTag(true);


                if (firstCardOfPair == null) {
                    firstCardOfPair = card;
                } else {
                    boolean identified = firstCardOfPair.isIdentified(view);
                    if (identified) {
                        card.setAsIdentified();

                        MediaPlayer mpc = MediaPlayer.create(PlayActivity.this, R.raw.success);
                        mpc.start();
                        if (areAllIdentified()) {
                            finishGame();
                        }
                    } else {

                        Card cardToHide = firstCardOfPair;
                        new Handler().postDelayed(new Runnable(){
                            @Override
                            public void run() {
                                cardToHide.hide();
                                setImageToBox(imageView);
                                MediaPlayer mpw = MediaPlayer.create(PlayActivity.this, R.raw.vibrate);
                                mpw.start();
                            }

                        }, 400);
                    }
                    firstCardOfPair = null;
                }


            }
        });


        return imageView;
    }

    // POINTS|LEVEL|TIME\n
    // POINTS|LEVEL|TIME\n
    // POINTS|LEVEL|TIME\n

    private final static String HIGHSCORES_KEY =  "highscores";

    public static ArrayList<HighScore> getHighScores(Context context) {
        SharedPreferences sp = context.getSharedPreferences("highscores", MODE_PRIVATE);
        String highscores = sp.getString(HIGHSCORES_KEY, "");

        String[] scores = highscores.split("\n");

        ArrayList<HighScore> highScores = new ArrayList<>();
        for (String line : scores) {
            if (line.trim().equals("")) {
                continue;
            }
            HighScore highScore = new HighScore(line);
            highScores.add(highScore);
        }
        return highScores;
    }

    private void updateHighScores(HighScore current) {
        ArrayList<HighScore> highScores = getHighScores(this);
        highScores.add(current);
        Collections.sort(highScores, new Comparator<HighScore>() {
            @Override
            public int compare(HighScore h1, HighScore h2) {
                if (h1.getPoints() == h2.getPoints()) return 0;
                return h1.getPoints() > h2.getPoints() ? -1 : 1;
            }
        });

        if (highScores.size() > 5) {
            highScores.remove(highScores.size() - 1);
        }

        StringBuilder sb = new StringBuilder();
        for(HighScore hs : highScores) {
            sb.append(hs).append("\n");
        }

        SharedPreferences sp = getSharedPreferences("highscores", MODE_PRIVATE);
        sp.edit().putString(HIGHSCORES_KEY, sb.toString()).apply();
    }

    private int calculateHighScore(int level, double timeLeft) {
        return level * (int) timeLeft;
    }

    private void finishGame() {
        countDownTimer.cancel();

        // TODO: replace 50 with calculated points
        int score = calculateHighScore(selectedLevel, millisUntilFinished / 1000);
        updateHighScores(new HighScore(score, selectedLevel, System.currentTimeMillis()));
        MediaPlayer mp = MediaPlayer.create(PlayActivity.this, R.raw.win);
        mp.start();
        showDialogueBox(PlayActivity.this, getString(R.string.win), getString(R.string.playagain));

    }

    private int selectedLevel;
    private double millisUntilFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        Intent intent = getIntent();
        level = (TextView)findViewById(R.id.level);
        time = (TextView)findViewById(R.id.time);

        selectedLevel = intent.getIntExtra(Constants.MSG, -1);

        level.setText(getString(R.string.level) + selectedLevel);
        time.setText(getString(R.string.time)+ (timer / 1000));

        int rows;
        int columns;

        switch (selectedLevel) {
            case Constants.EASY:
                rows = 4;
                columns = 3;
                cards = buildCards(new int[]{R.drawable.ic_biswajyotim_bee, R.drawable.ic_flower1svg, R.drawable.ic_hakanl_simple_flower,
                        R.drawable.ic_purple_burst_flower, R.drawable.ic_redflower, R.drawable.ic_yves_guillou_dahlia});

                break;
            case Constants.MED:
                rows = 4;
                columns = 4;
                cards = buildCards(new int[]{R.drawable.ic_food1, R.drawable.ic_food2, R.drawable.ic_food3,
                        R.drawable.ic_food5, R.drawable.ic_food6, R.drawable.ic_food7, R.drawable.ic_food8, R.drawable.ic_food4});
                break;
            case Constants.HARD:
                rows = 5;
                columns = 4;
                // TODO
                cards = buildCards(new int[]{R.drawable.ic_food1, R.drawable.ic_food2, R.drawable.ic_food3,
                        R.drawable.ic_food5, R.drawable.ic_food6,R.drawable.ic_biswajyotim_bee, R.drawable.ic_flower1svg, R.drawable.ic_hakanl_simple_flower,
                        R.drawable.ic_purple_burst_flower, R.drawable.ic_redflower});
                break;

            default:
                throw new RuntimeException();
        }

        LinearLayout grid = findViewById(R.id.grid);

        buttons = new ArrayList<>();
        for (int i = 0; i < cards.size() * 2; i++) {
            Card card = cards.get(i / 2);
            ImageView imageView = createImageView(card);
            if (i % 2 == 0) {
                card.first = imageView;
            } else {
                card.second = imageView;
            }
            buttons.add(imageView);
        }
        Collections.shuffle(buttons);


        for (int i = 0; i < rows; i++) {
            LinearLayout row = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.topMargin = dpToPx(2);
            params.weight = 1;
            row.setOrientation(LinearLayout.HORIZONTAL);

            for (int j = 0; j < columns; j++) {
                row.addView(buttons.get(i * columns + j));
            }
            grid.addView(row);
        }




        countDownTimer = new CountDownTimer(timer, 1000) {

            public void onTick(long millisUntilFinished) {
                time.setText("TIME: " + millisUntilFinished / 1000 + " sec");
                if((millisUntilFinished / 1000) <= 45 && isRunning){
                    isRunning = false;
                    timer = 50000;
                    time.setText("TIME: " + millisUntilFinished / 1000 + " sec");
                    countDownTimer.start();
                } else {
                    PlayActivity.this.millisUntilFinished = millisUntilFinished;
                }
            }

            public void onFinish() {
                time.setText(getString(R.string.time)+ timer / 1000);
//                isUserWins = 0; // resetting...
                showDialogueBox(PlayActivity.this, getString(R.string.lose), getString(R.string.tryagain));

            }

        }.start();

        //to make disappear images
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                for (int i = 0; i < buttons.size(); i++) {
                   // buttons.get(i).setBackgroundResource(R.drawable.qmarkcard);
                    buttons.get(i).setImageDrawable(ContextCompat.getDrawable(PlayActivity.this, R.drawable.qmarkcard));
                }
                isSoundEnabled = true;
            }
        }, 4000);


    }


    //method show box
    public void showDialogueBox(Activity activity, String msg , String button_text){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        pl.droidsonroids.gif.GifImageView gifimage = (pl.droidsonroids.gif.GifImageView) dialog.findViewById(R.id.gif_view);


        if(msg.contains("YOU LOOSE!")){
            MediaPlayer mediaPlayer = MediaPlayer.create(PlayActivity.this, R.raw.baby_laugh);
            mediaPlayer.start();
        }else{
            gifimage.setBackgroundResource(R.drawable.win_gif);
            MediaPlayer mediaPlayer = MediaPlayer.create(PlayActivity.this, R.raw.win);
            mediaPlayer.start();
        }


        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        text.setText(msg);

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        dialogButton.setText(button_text);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();

    }


}