package com.example.flamiotefirealarmapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BluetoothDetailsActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    private TextView connectionStatusText;
    private TextView deviceNameText;
    private TextView deviceAddressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_details);
        connectionStatusText = findViewById(R.id.connectionStatusText);
        deviceNameText = findViewById(R.id.deviceNameText);
        deviceAddressText = findViewById(R.id.deviceAddressText);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            return;
        }
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothReceiver, filter);

        updateConnectionDetails();
    }

    private void updateConnectionDetails() {
        if (bluetoothAdapter.isEnabled()) {
            connectionStatusText.setText("Bluetooth is Enabled");
            BluetoothDevice pairedDevice = bluetoothAdapter.getRemoteDevice("00:23:10:A0:5E:57");
            deviceNameText.setText("Device Name: " + pairedDevice.getName());
            deviceAddressText.setText("Device Address: " + pairedDevice.getAddress());
        } else {
            connectionStatusText.setText("Bluetooth is Disabled");
        }
    }

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_ON) {
                    connectionStatusText.setText("Bluetooth is Enabled");
                    updateConnectionDetails();
                } else if (state == BluetoothAdapter.STATE_OFF) {
                    connectionStatusText.setText("Bluetooth is Disabled");
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothReceiver);
    }
}
