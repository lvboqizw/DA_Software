package com.example.remotecontrol;

import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
        implements BltManager.BluetoothCallback {

    private static final String DEVICE_MAC_ADDRESS = "98:D3:31:F6:F6:AC";

    private BltManager bluetoothManager;
    private Button connectButton;
    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        connectButton = findViewById(R.id.btn_connect);
        bluetoothManager = new BltManager(this);

        connectButton.setOnClickListener(v -> {
            if (!isConnected) {
                bluetoothManager.connect(DEVICE_MAC_ADDRESS);
            } else {
                bluetoothManager.disconnect();
            }
        });

        SeekBar seekBar = findViewById(R.id.skBar_temperature);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                bluetoothManager.sendMessageRetry(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                bluetoothManager.sendMessageRetry(String.valueOf(progress));
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
    }
}
