package com.example.leejaejun.scanner;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.example.leejaejun.scanner.server.ConnectBlueTooth;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    Table table;
    Util util;
    BluetoothAdapter bluetoothAdapter;
    ConnectBlueTooth connectBlueTooth;
    Button button1;
    Button button2;
    Button Nbutton;
    Button Ebutton;
    Button Sbutton;
    Button Wbutton;
    final int BLUETOOTH_CODE = 10;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        table = new Table(this);
        util = new Util();
        connectBlueTooth = new ConnectBlueTooth(this, table, util);
        if(bluetoothAdapter!=null){
            if(!bluetoothAdapter.isEnabled()){
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, BLUETOOTH_CODE);
            }
        }
        button1 = (Button)findViewById(R.id.button2);
        button2 = (Button)findViewById(R.id.button);
        Nbutton = (Button)findViewById(R.id.NORTH);
        Ebutton = (Button)findViewById(R.id.EAST);
        Sbutton = (Button)findViewById(R.id.SOUTH);
        Wbutton = (Button)findViewById(R.id.WEST);

        final PlaceVO placeVO = new PlaceVO();

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectBlueTooth.selectDevice(MainActivity.this, bluetoothAdapter);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectBlueTooth.search(placeVO);
            }
        });
        Nbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                util.positionY++;
                util.displayMap(table);
            }
        });
        Ebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                util.positionX++;
                util.displayMap(table);
            }
        });
        Sbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                util.positionY--;
                util.displayMap(table);
            }
        });
        Wbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                util.positionX--;
                util.displayMap(table);
            }
        });
    }


    @Override
    protected void onDestroy() {
        try {
            connectBlueTooth.finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}