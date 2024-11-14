package com.example.remotecontrol;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity
        implements BltManager.BluetoothCallback {

    private BltManager bluetoothManager;
    private Button connectButton;
    private Spinner spinnerGender;
    private Spinner spinnerMode;
    private EditText editTextNr;
    private boolean isConnected = false;
    private ProgressBar timerBar;

    private ButtonHandler buttonHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button setButton = findViewById(R.id.btn_set);
        Button runButton = findViewById(R.id.btn_run);
        SeekBar seekBarTemp = findViewById(R.id.skBar_temperature);

        connectButton = findViewById(R.id.btn_connect);
        bluetoothManager = new BltManager(this);
        spinnerGender = findViewById(R.id.gender);
        spinnerMode = findViewById(R.id.mode);
        editTextNr = findViewById(R.id.number);
        timerBar = findViewById(R.id.timer);
        timerBar.setProgress(0);

        buttonHandler = new ButtonHandler(this, this);


        connectButton.setOnClickListener(v -> {
            buttonHandler.btnConnection(isConnected);
        });

        setSeekBarListener(seekBarTemp, "T");

        setButton.setOnClickListener(v -> {
            String mode = spinnerMode.getSelectedItem().toString();
            String gender = spinnerGender.getSelectedItem().toString();
            String number = editTextNr.getText().toString();
            buttonHandler.btnSet(mode, gender, number);
        });

        runButton.setOnClickListener(v -> {
            String mode = spinnerMode.getSelectedItem().toString();
            TextView roundText = findViewById(R.id.monitor_round);
            TextView nodeText = findViewById(R.id.monitor_node);
            TextView ringText = findViewById(R.id.monitor_ring);
            buttonHandler.btnRun(mode, roundText, nodeText, ringText, timerBar);
        });
    }

    public void onConnectionStatusChanged(boolean isConnected) {
        this.isConnected = isConnected;
        runOnUiThread(() -> {
            if (isConnected) {
                connectButton.setBackgroundColor(getResources()
                        .getColor(R.color.green));
                connectButton.setText(getResources()
                        .getText(R.string.connected));
                Toast.makeText(MainActivity.this,
                        "Connected",
                        Toast.LENGTH_SHORT).show();
            } else {
                connectButton.setBackgroundColor(getResources()
                        .getColor(R.color.warning_red));
                connectButton.setText(getResources()
                        .getText(R.string.disconnected));
                Toast.makeText(MainActivity.this,
                        "Disconnected",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onMessageReceived(String message) {
        if (message.equals("ACK")) {
            buttonHandler.roundIncrement();
        }
        if (message.contains("T")) {
            updateTemperature(message);
        }
    }

    private void setSeekBarListener(SeekBar seekBar, String type) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                String message = type + "/" + progress;
                bluetoothManager.sendMessageRetry(message);
                if (type.equals("T")) {
                    String temperature = Integer.toString(Constants.DEFAULT_TEMPERATURE + progress);
                    updateMonitor(R.id.monitor_temperature,
                            "Temperature: "
                                    + temperature);
                }
            }
        });
    }

    private void updateMonitor(int id, String message) {
        TextView textView = findViewById(id);
        textView.setText(message);
    }

    private void updateTemperature(String message) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            String number = matcher.group();
            updateMonitor(R.id.monitor_temperature,
                    "Temperature: " + number);
        }
    }
}
