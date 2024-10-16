package com.example.remotecontrol;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.lang.reflect.Array;

public class MainActivity extends AppCompatActivity
        implements BltManager.BluetoothCallback {

    private static final String DEVICE_MAC_ADDRESS = "98:D3:31:F6:F6:AC";

    private BltManager bluetoothManager;
    private FileUtils fileUtils;
    private Button connectButton;
    private Button startButton;
    private Spinner spinnerGender;
    private Spinner spinnerMode;
    private EditText editTextNr;
    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        connectButton = findViewById(R.id.btn_connect);
        startButton = findViewById(R.id.btn_start);
        bluetoothManager = new BltManager(this);
        spinnerGender = findViewById(R.id.gender);
        spinnerMode = findViewById(R.id.mode);
        editTextNr = findViewById(R.id.number);

        fileUtils = new FileUtils(this);

        connectButton.setOnClickListener(v -> {
            if (!isConnected) {
                bluetoothManager.connect(DEVICE_MAC_ADDRESS);
            } else {
                bluetoothManager.disconnect();
            }
        });

        SeekBar seekBarTemp = findViewById(R.id.skBar_temperature);
        setSeekBarListener(seekBarTemp, "T");

        SeekBar seekBarFreq = findViewById(R.id.skBar_vibration);
        setSeekBarListener(seekBarFreq, "F");

        startButton.setOnClickListener(v -> {
            String filename = generateFileName();
            if (filename == null) {
                Toast.makeText(MainActivity.this,
                        "Missing Number", Toast.LENGTH_SHORT).show();
                return;
            }

            Uri uri = fileUtils.getUri(filename);
            fileUtils.appendToUri(uri, "Hello World");
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
        runOnUiThread(() -> Toast.makeText(
                this,
                "Message: " + message,
                Toast.LENGTH_SHORT).show());
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
            }
        });
    }

    private String generateFileName() {
        String gender = spinnerGender.getSelectedItem().toString();
        String number = editTextNr.getText().toString();
        if (number.isEmpty()) {
            return null;
        }
        String mode = spinnerMode.getSelectedItem().toString();

        return number + "_" + gender + "_" + mode;
    }
}
