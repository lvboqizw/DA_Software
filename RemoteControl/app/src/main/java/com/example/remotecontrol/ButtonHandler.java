package com.example.remotecontrol;

import android.os.Handler;
import java.util.concurrent.atomic.AtomicInteger;

public class ButtonHandler {

    private final BltManager bluetoothManager;
    private final AtomicInteger round = new AtomicInteger();
    private boolean heat = false;
    private final Handler handler = new Handler();

    public ButtonHandler(BltManager.BluetoothCallback bluetoothCallback) {
        bluetoothManager = new BltManager(bluetoothCallback);
        round.set(0);
    }

    public void btnConnection(boolean isConnected) {
        if (!isConnected) {
            bluetoothManager.connect(Constants.DEVICE_MAC_ADDRESS);
        } else {
            bluetoothManager.disconnect();
        }
    }

    public void btnVib(int vibTime, String nodes, boolean isNode, boolean isLeft) {
        if (isNode) {
            vibNode(vibTime, nodes);
        } else {
            vibFlow(vibTime, isLeft);
        }
    }

    public boolean btnHeat(String extraTemperature) {
        if (!heat) {
            bluetoothManager.addToMessageQueue("T/N/" + extraTemperature);
            heat = true;
            return true;
        } else {
            bluetoothManager.addToMessageQueue("T/F/0");
            heat = false;
            return false;
        }
    }

    public void btnRun(int vibTime, String nodes, boolean isNode, boolean isLeft,
                       String extraTemperature) {
        bluetoothManager.addToMessageQueue("T/N/" + extraTemperature);
        heat = true;

        if (isNode) {
            vibNode(vibTime, nodes);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bluetoothManager.addToMessageQueue("T/F/0");
                    heat = false;
                }
            }, vibTime * 1000L);
        } else {
            vibFlow(vibTime, isLeft);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bluetoothManager.addToMessageQueue("T/F/0");
                    heat = false;
                }
            }, vibTime * 6 * 1000L);
        }
    }

    private void vibNode(int vibTime, String nodes) {
        String message = "M/1/" + nodes;
        bluetoothManager.addToMessageQueue(message);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bluetoothManager.addToMessageQueue("M/0/0");
            }
        }, vibTime * 1000L);
    }

    private void vibFlow(int vibTime, boolean isLeft) {
        final int[] node = new int[1];
        if (isLeft) node[0] = 1;
        else node[0] = 32;
        final int[] r = {0};
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (r[0]++ < 6) {
                    String message = "M/1/" + node[0];
                    bluetoothManager.addToMessageQueue(message);
                    if (isLeft) {
                        node[0] = node[0] << 1;
                    } else {
                        node[0] = node[0] >> 1;
                    }
                    handler.postDelayed(this, vibTime * 1000L);
                } else if (r[0] >= 6) {
                    bluetoothManager.addToMessageQueue("M/0/0");
                }
            }
        };
        handler.post(runnable);
    }
}
