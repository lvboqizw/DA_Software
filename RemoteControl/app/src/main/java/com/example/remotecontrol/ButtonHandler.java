package com.example.remotecontrol;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.concurrent.atomic.AtomicInteger;

public class ButtonHandler {

    private final Context context;
    private final BltManager bluetoothManager;
    private final Trigger trigger;
    private Uri uri;
    private final AtomicInteger round = new AtomicInteger();
    private boolean heat = false;

    public ButtonHandler(Context context, BltManager.BluetoothCallback bluetoothCallback) {
        this.context = context;
        bluetoothManager = new BltManager(bluetoothCallback);
        trigger = new Trigger();
        round.set(0);
    }

    public void btnConnection(boolean isConnected) {
        if (!isConnected) {
            bluetoothManager.connect(Constants.DEVICE_MAC_ADDRESS);
        } else {
            bluetoothManager.disconnect();
        }
    }

    public void btnSet(String mode, String gender, String number) {
        String filename;
        if (number.isEmpty()) {
            Toast.makeText(context,
                    "Missing Number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mode.equals("Test")) {
            filename =  "0_NONE_TEST";
        } else {
            filename = number + "_" + gender + "_" + mode;
        }

        FileUtils fileUtils = new FileUtils(context);
        uri = fileUtils.getUri(filename);
        trigger.generateTriggerList(mode);
        Gson gson = new Gson();
        String json = gson.toJson(trigger);
        fileUtils.appendToUri(uri, json);
        round.set(0);
    }

    @SuppressLint("SetTextI18n")
    public void btnVibrate(String mode, TextView roundText, TextView nodeText,
                           TextView ringText, ProgressBar processBar, int vibTime) {
        Handler handler = new Handler();

        if (mode.equals("FLOW")) {
            final int[] node = {1};
            final int[] progress = {0};
            processBar.setMax(6);

            Runnable runnable = new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    if (node[0] <= 32) {
                        processBar.setProgress(++progress[0]);
                        bluetoothManager.sendMessageRetry("M/1/" + node[0]);
                        node[0] <<= 1;

                        handler.postDelayed(this, vibTime * 1000L);
                    } else {
                        bluetoothManager.sendMessageRetry("M/1/0");
                    }
                }
            };
            handler.post(runnable);
        } else {
            if (uri == null) {
                Toast.makeText(context,
                        "Press Set first", Toast.LENGTH_SHORT).show();
                return;
            }

            int curRound = round.getAndIncrement();
            processBar.setMax(Constants.TEST_ROUNDS);
            if (curRound < Constants.TEST_ROUNDS) {
                String ring = trigger.getRing(curRound);
                String node = trigger.getNode(curRound);

                processBar.setProgress(curRound + 1);
                roundText.setText("Round: " + String.valueOf(curRound + 1) + " Vibrating");
                ringText.setText("Ring: " + toBinary(ring, 3));
                nodeText.setText("Node: " + toBinary(node, 6));
                String message = "M/" + ring + "/" + node;
                bluetoothManager.sendMessageRetry(message);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                    bluetoothManager.sendMessageRetry("M/0/0");
                        roundText.setText("Round: " + String.valueOf(curRound + 1) + " Stopped");
                    }
                }, vibTime * 1000L);
            } else {
                Toast.makeText(context,
                        "Ten Rounds finished", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public boolean btnHeat(String extraTemperature) {
        if (!heat) {
            bluetoothManager.sendMessageRetry("T/N/" + extraTemperature);
            heat = true;
            return true;
        } else {
            bluetoothManager.sendMessageRetry("T/F/" + extraTemperature);
            heat = false;
            return false;
        }
    }

    private String toBinary(String message, int len) {
        int value = Integer.parseInt(message);
        StringBuilder builder = new StringBuilder();

        for (int i = len - 1; i >= 0; i--) {
            int tmp = 1 << i;
            if ((tmp & value) != 0) {
                builder.append("1");
            } else {
                builder.append("0");
            }
        }
        return builder.toString();
    }
}
