package zhimingzhang.deakin.timer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    TextView previousEntryText, taskText;
    EditText taskEditText;
    ImageButton startButton, pauseButton, stopButton;
    Chronometer chronometer;

    SharedPreferences SharedPreferences;
    SharedPreferences.Editor Editor;

    boolean timerRunning;
    long time;
    long seconds;
    long minutes;
    long editorTime;
    CharSequence finalTime;
    long offset = 0;
    String enteredWork, enteredWork2, timeSpent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previousEntryText = findViewById(R.id.previousEntryText);
        taskText = findViewById(R.id.taskText);
        taskEditText = findViewById(R.id.taskEditText);
        startButton = findViewById(R.id.startButton);
        pauseButton = findViewById(R.id.pauseButton);
        stopButton = findViewById(R.id.stopButton);
        chronometer = findViewById(R.id.chronometer);

        SharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        Editor = SharedPreferences.edit();
        offset = 0;

        enteredWork = SharedPreferences.getString("enteredWork", "");
        time = SharedPreferences.getLong("time", 0);

        seconds = (time % (1000 * 60)) / 1000;
        minutes = (time % (1000 * 60 * 60)) / (1000 * 60);
        timeSpent = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        previousEntryText.setText("You spent " + timeSpent + " on " + enteredWork + " last time.");

        //to keep timer going on rotation
        if (savedInstanceState != null) {
            offset = savedInstanceState.getLong("offset",0);
            timerRunning = savedInstanceState.getBoolean("timerRunning", true);
            long getBaseTime = savedInstanceState.getLong("getBaseTime", 0);

            if (timerRunning == true) {
                chronometer.start();
                chronometer.setBase(SystemClock.elapsedRealtime() - (SystemClock.elapsedRealtime() - getBaseTime));
            }
            else {
                chronometer.setBase(SystemClock.elapsedRealtime() - offset);
                chronometer.stop();
            }
        }


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!taskEditText.getText().toString().isEmpty()) {
                    timerRunning = true;
                    taskEditText.setEnabled(false);
                    chronometer.setBase(SystemClock.elapsedRealtime() - offset);
                    chronometer.start();

                } else {
                    Toast.makeText(MainActivity.this, "Please enter your task first!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timerRunning = true) {
                    timerRunning = false;
                    chronometer.stop();
                    offset = SystemClock.elapsedRealtime() - chronometer.getBase();
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timerRunning = true) {
                    timerRunning = false;
                    editorTime = offset;
                    finalTime = chronometer.getText();

                    Editor.putLong("time", editorTime);
                    Editor.putString("enteredWork", taskEditText.getText().toString());
                    Editor.apply();

                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.stop();

                    enteredWork2 = taskEditText.getText().toString();
                    previousEntryText.setText("You spent " + finalTime + " on " + enteredWork2 + " last time.");

                    taskEditText.setEnabled(true);
                    taskEditText.setText("");
                    offset = 0;
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("getBaseTime", chronometer.getBase());
        outState.putLong("offset", offset);
        outState.putBoolean("timerRunning", timerRunning);
    }
}