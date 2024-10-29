package com.example.remotecontrol;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity
        implements BltManager.BluetoothCallback {

    private static final String DEVICE_MAC_ADDRESS = "98:D3:31:F6:F6:AC";

    private BltManager bluetoothManager;
    private FileUtils fileUtils;
    private Button connectButton;
    private Spinner spinnerGender;
    private Spinner spinnerMode;
    private EditText editTextNr;
    private boolean isConnected = false;
    private Uri uri;
    private AtomicInteger round = new AtomicInteger();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button setButton = findViewById(R.id.btn_set);
        Button runButton = findViewById(R.id.btn_run);
        SeekBar seekBarTemp = findViewById(R.id.skBar_temperature);
        SeekBar seekBarFreq = findViewById(R.id.skBar_vibration);
        Trigger trigger = new Trigger();

        round.set(0);

        connectButton = findViewById(R.id.btn_connect);
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

        setSeekBarListener(seekBarTemp, "T");
        setSeekBarListener(seekBarFreq, "V");

        setButton.setOnClickListener(v -> {
            String filename = generateFileName();
            if (filename == null) {
                Toast.makeText(MainActivity.this,
                        "Missing Number", Toast.LENGTH_SHORT).show();
                return;
            }

            uri = fileUtils.getUri(filename);
            trigger.generateTriggerList(spinnerMode.getSelectedItem().toString());
            Gson gson = new Gson();
            String json = gson.toJson(trigger);
            fileUtils.appendToUri(uri, json);
            round.set(0);
        });

        runButton.setOnClickListener(v -> {
            if (uri == null) {
                Toast.makeText(MainActivity.this,
                        "Press Set first", Toast.LENGTH_SHORT).show();
                return;
            }
            if (round.get() < 10) {
                String message = "M/" + trigger.getNodes(round.get());
                bluetoothManager.sendMessageRetry(message);
            } else {
                Toast.makeText(MainActivity.this,
                        "Ten Rounds finished", Toast.LENGTH_SHORT).show();
            }

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
        if (message.equals("ACK")) {
            round.incrementAndGet();
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
            }
        });
    }

    private String generateFileName() {
        String mode = spinnerMode.getSelectedItem().toString();
        if (mode.equals("Test")) {
            return "0_NONE_TEST";
        }

        String gender = spinnerGender.getSelectedItem().toString();
        String number = editTextNr.getText().toString();
        if (number.isEmpty()) {
            return null;
        }

        return number + "_" + gender + "_" + mode;
    }
}
