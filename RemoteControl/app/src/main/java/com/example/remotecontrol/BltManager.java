package com.example.remotecontrol;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

public class BltManager {

    public interface BluetoothCallback {
        void onConnectionStatusChanged(boolean isConnected);
        void onMessageReceived(String message);
    }

    private static final UUID MY_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;

    private BluetoothCallback callback;
    private boolean isConnected = false;

    private Handler handler = new Handler(Looper.getMainLooper());
    private final BlockingDeque<String> sendQueue = new LinkedBlockingDeque<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Object lock = new Object();
    private boolean ackReceivedFlag = false;

    public BltManager(BluetoothCallback callback) {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.callback = callback;
    }

    @SuppressLint("MissingPermission")
    public void connect(String macAddress) {
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(macAddress);

        new Thread(() -> {
            try {
                bluetoothSocket = bluetoothDevice
                        .createRfcommSocketToServiceRecord(MY_UUID);
                bluetoothSocket.connect();

                isConnected = true;
                notifyConnectionStatus(true);
                checkConnectionStatus();
                sendingMessage();
                receivingMessage();
            } catch (IOException e) {
                Log.e("BluetoothError", "Connection Failed", e);
                notifyConnectionStatus(false);
            }
        }).start();
    }

    public void disconnect() {
        try {
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
                isConnected = false;
                notifyConnectionStatus(false);
            }
        } catch (IOException e) {
            Log.e("BluetoothError","Disconnection Failed", e);
        }
    }

    private void checkConnectionStatus() {
        new Thread(() -> {
            while (isConnected) {
                try {
                    if (bluetoothSocket.getInputStream() != null) {
                        bluetoothSocket.getInputStream().available();
                    }
                    Thread.sleep(1000);
                } catch (IOException e) {
                    Log.e("BluetoothError",
                            "Bluetooth Connection disconnected passively",
                            e);
                    handleDisconnection();
                    break;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    public void addToMessageQueue(String message) {
        int checkSum = getCheckSum(message);
        String bag = String.format("%02X", checkSum) + message;
        sendQueue.offer(bag);
    }

    private void sendingMessage() {
        executor.execute(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String message = sendQueue.take();
                    Logger.d("Take message from queue: "+message);
                    sendWithAck(message);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    Logger.e("Send worker interrupted " + e.getMessage());
                }
            }
        });
    }

    private void sendWithAck(String message) {
        synchronized (lock) {
            try {
                boolean ackReceived = false;
                int retries = 0;
                int maxRetries = 3;

                while (!ackReceived && retries < maxRetries) {
                    bluetoothSocket.getOutputStream().write((message + "\n").getBytes());
                    ackReceived = waitAck(1000);
                    if (!ackReceived) {
                        retries ++;
                        Logger.w("ACK not received, retrying... ("+retries + ")");
                    }
                }

                if (!ackReceived) {
                    Logger.e("Failed to received ACK of message: " + message);
                } else {
                    Logger.d("ACK received: " + message);
                }
            } catch (Exception e) {
                Logger.e("Error during sendWithAck " + e.getMessage());
            }
        }
    }

    private void receivingMessage() {
        new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()){
                    if (bluetoothSocket.getInputStream().available() > 0) {
                        byte[] buffer = new byte[1024];
                        int bytesRead = bluetoothSocket.getInputStream().read(buffer);
                        String response = new String(buffer, 0, bytesRead).trim();

                        synchronized (lock) {
                            switch (response) {
                                case "A":
                                    ackReceivedFlag = true;
                                    lock.notifyAll();
                                    break;
                                case "R":
                                    ackReceivedFlag = false;
                                    lock.notifyAll();;
                                    break;
                                default:
                                    handler.post(() -> callback.onMessageReceived(response));
                                    break;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                Logger.e("Error receiving data: " + e.getMessage());
            }
        }).start();
    }

    private boolean waitAck(long timeout) {
        synchronized (lock) {
            try {
                ackReceivedFlag = false;
                lock.wait(timeout);
                return ackReceivedFlag;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
    }

    private int getCheckSum(String message) {
        int checksum = 0;
        for (int i = 0; i < message.length(); i ++) {
            checksum += message.charAt(i);
        }
        return checksum & 0xFF;
    }

    private void handleDisconnection() {
        isConnected = false;
        notifyConnectionStatus(false);
        try{
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
            }
        }catch (IOException e) {
            Log.e("BluetoothError", "Socked close Failed", e);
        }
    }

    private void notifyConnectionStatus(boolean isConnected) {
        handler.post(() -> callback.onConnectionStatusChanged(isConnected));
    }
}
