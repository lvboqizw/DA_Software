package com.example.remotecontrol;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

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
    private OutputStream outputStream;
    private InputStream inputStream;

    private BluetoothCallback callback;
    private boolean isConnected = false;

    private Handler handler = new Handler(Looper.getMainLooper());

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
                outputStream = bluetoothSocket.getOutputStream();
                inputStream = bluetoothSocket.getInputStream();

                isConnected = true;
                notifyConnectionStatus(true);
                listeningMessage();
                monitorConnectionStatus();
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

    public void sendMessage(String message) {
        new Thread(() -> {
            try {
                if (outputStream != null && isConnected) {
                    outputStream.write(message.getBytes());
                    outputStream.flush();
                }
            } catch (IOException e) {
                Log.e("BluetoothError", "Send Message Failed", e);
            }
        }).start();
    }

    private void listeningMessage() {
        new Thread(() -> {
            byte[] buffer = new byte[1024];
            int bytes;

            while (isConnected) {
                try {
                    bytes = inputStream.read(buffer);
                    String receivedMessage = new String(buffer, 0, bytes);
                    //notifyMessageReceived(receivedMessage);

//                    if (receivedMessage.equals("ACK")) {
                        //handler.post(() -> callback.onMessageReceived("ACK received"));
//                    }
                } catch (IOException e) {
                    Log.e("BluetoothError", "Listen Message Failed", e);
                    handleDisconnection();
                    break;
                }
            }
        }).start();
    }

    private void monitorConnectionStatus() {
        new Thread(() -> {
            while (isConnected) {
                try {
                    if (inputStream != null) {
                        inputStream.available();
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

    public void sendMessageRetry(String message) {
        new Thread(() -> {
            boolean ackReceived = false;
            while (!ackReceived && isConnected) {
                try {
                    sendMessage(message);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.e("BluetoothError", "sendMessageRetry: Thread sleep Failed", e);
                }
                //ackReceived = checkForAck();
            }
        }).start();
    }

    private boolean checkForAck() {
        // TODO: Check ACK
        return false;
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

    private void notifyMessageReceived(String message) {
        //handler.post(() -> callback.onMessageReceived(message));
    }

}
