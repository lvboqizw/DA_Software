package com.example.remotecontrol;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity
        implements BltManager.BluetoothCallback {

    private Button connectButton;
    private boolean isConnected = false;

    private ButtonHandler buttonHandler;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Logger logger = Logger.getInstance(this);
        Logger.d("MainActivity started");
        Button btnVib = findViewById(R.id.btn_vib);
        Button btnHeat = findViewById(R.id.btn_heat);
        Button btnRun = findViewById(R.id.btn_run);
        SeekBar seekBarTemp = findViewById(R.id.skBar_temperature);

        EditText vibTimeText = findViewById(R.id.vib_time);
        TextView extraTemperature = findViewById(R.id.extra_Temperature);
        vibTimeText.setText(String.valueOf(Constants.DEFAULT_VIBE_TIME));

        RadioGroup modes = findViewById(R.id.mode);
        LinearLayout layoutFlow = findViewById(R.id.layoutFlow);
        LinearLayout layoutNode = findViewById(R.id.layoutNode);
        modes.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.modeNode) {
                layoutNode.setVisibility(View.VISIBLE);
                layoutFlow.setVisibility(View.GONE);
            } else if (checkedId == R.id.modeFlow) {
                layoutNode.setVisibility(View.GONE);
                layoutFlow.setVisibility(View.VISIBLE);
            }
        });

        connectButton = findViewById(R.id.btn_connect);
        buttonHandler = new ButtonHandler(this, this);

        seekBarTemp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String extra = String.valueOf(seekBarTemp.getProgress());
                extraTemperature.setText("Temperature: +" + extra);
            }
        });

        connectButton.setOnClickListener(v -> {
            buttonHandler.btnConnection(isConnected);
        });

        btnVib.setOnClickListener(v -> {
            int vibTime = Integer.parseInt(vibTimeText.getText().toString());
            RadioGroup flow = findViewById(R.id.flow);
            String nodes = getNodes();

            buttonHandler.btnVib(vibTime, nodes,
                    modes.getCheckedRadioButtonId() == R.id.modeNode,
                    flow.getCheckedRadioButtonId() == R.id.flowLeft);
        });

        btnHeat.setOnClickListener(v -> {
            int extraTemp = seekBarTemp.getProgress();
            boolean heat = buttonHandler.btnHeat(String.valueOf(extraTemp));
            if (heat) {
                btnHeat.setBackgroundColor(getResources()
                        .getColor(R.color.green));
            } else {
                btnHeat.setBackgroundColor(getResources()
                        .getColor(R.color.warning_red));
            }
        });

        btnRun.setOnClickListener(v -> {
            int extraTemp = seekBarTemp.getProgress();
            int vibTime = Integer.parseInt(vibTimeText.getText().toString());
            RadioGroup flow = findViewById(R.id.flow);
            String nodes = getNodes();

            buttonHandler.btnRun(vibTime, nodes,
                    modes.getCheckedRadioButtonId() == R.id.modeNode,
                    flow.getCheckedRadioButtonId() == R.id.flowLeft,
                    String.valueOf(extraTemp));
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
        if (message.contains("T")) {
            updateTemperature(message);
        }
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

    private String getNodes() {
        int node = 0;
        CheckBox nodeI = findViewById(R.id.nodeOne);
        if (nodeI.isChecked()) node += 1;
        CheckBox nodeII = findViewById(R.id.nodeTwo);
        if (nodeII.isChecked()) node += (1 << 1);
        CheckBox nodeIII = findViewById(R.id.nodeThree);
        if (nodeIII.isChecked()) node += (1 << 2);
        CheckBox nodeIV = findViewById(R.id.nodeFour);
        if (nodeIV.isChecked()) node += (1 << 3);
        CheckBox nodeV = findViewById(R.id.nodeFive);
        if (nodeV.isChecked()) node += (1 << 4);
        CheckBox nodeVI = findViewById(R.id.nodeSix);
        if (nodeVI.isChecked()) node += (1 << 5);

        return String.valueOf(node);
    }
}
