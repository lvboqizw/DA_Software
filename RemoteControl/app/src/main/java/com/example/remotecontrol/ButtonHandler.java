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

//    private boolean isConnected = false;

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
    public void btnRun(String mode, TextView roundText, TextView nodeText,
                       TextView ringText, ProgressBar timerBar) {
        if (mode.equals("FLOW")) {
            Handler handler = new Handler();
            final int[] node = {1};
            final int[] timer = {2};
            long startTime = System.currentTimeMillis();

            Runnable runnable = new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    long elapsedTime = System.currentTimeMillis() - startTime;
                    if (elapsedTime < 8000) {
                        bluetoothManager.sendMessageRetry("M/1/" + node[0]);
                        nodeText.setText("Node: " +
                                toBinary(String.valueOf(node[0]), 6));
                        ringText.setText("Ring: 001");

                        timerBar.setProgress(timer[0]);

                        node[0] <<= 1;
                        if (node[0] > 32) {
                            node[0] = 1;
                        }
                        timer[0] += 2;

                        handler.postDelayed(this, 2000);
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
            int curRound = round.get();
            if (curRound < 10) {
                String ring = trigger.getRing(curRound);
                String node = trigger.getNode(curRound);
                String message = "M/" + ring + "/" + node;
                bluetoothManager.sendMessageRetry(message);

                String process = Integer.toString(curRound + 1) + "/"
                        + Integer.toString(trigger.getTotalRound());
                roundText.setText("Round: " + process);
                ringText.setText("Ring: " + toBinary(ring, 3));
                nodeText.setText("Node: " + toBinary(node, 6));

            } else {
                Toast.makeText(context,
                        "Ten Rounds finished", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void roundIncrement() { round.incrementAndGet();}


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
