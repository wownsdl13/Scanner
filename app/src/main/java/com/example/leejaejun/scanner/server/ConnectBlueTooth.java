package com.example.leejaejun.scanner.server;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.leejaejun.scanner.PlaceVO;
import com.example.leejaejun.scanner.Table;
import com.example.leejaejun.scanner.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

import static com.example.leejaejun.scanner.PlaceVO.EAST;
import static com.example.leejaejun.scanner.PlaceVO.EMPTY;
import static com.example.leejaejun.scanner.PlaceVO.NORTH;
import static com.example.leejaejun.scanner.PlaceVO.SOUTH;
import static com.example.leejaejun.scanner.PlaceVO.WALL;
import static com.example.leejaejun.scanner.PlaceVO.WEST;

/**
 * Created by LeeJaeJun on 2017-05-22.
 */

public class ConnectBlueTooth {
    BluetoothSocket mSocket;
    InputStream mInputStream;
    OutputStream mOutputStream;
    Thread mWorkerThread;
    Handler handler;
    Queue<PlaceVO> queue;
    PlaceVO prePlaceVo;
    final char mDelimiter = '\0';

    Stack<PlaceVO> stack;

    Util util;
    Activity activity;
    Table table;

    final public static int SEARCHING_START = 1;
    final public static int SEARCHING = 2;
    final public static int NONE = 3;

    int nowType;

    public ConnectBlueTooth(Activity activity, Table table, Util util) {
        handler = new Handler();
        queue = new LinkedList<PlaceVO>();
        stack = new Stack<PlaceVO>();
        this.util = util;
        this.table = table;
        this.activity = activity;
    }

    public void selectDevice(Activity activity, BluetoothAdapter bluetoothAdapter) {
        final Set<BluetoothDevice> mDevices = bluetoothAdapter.getBondedDevices();
        final int count = mDevices.size();

        if (count == 0) {
            Toast.makeText(activity, "페어링된 장치가 존재하지 않음", Toast.LENGTH_SHORT).show();
            return;
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("블루투스 장치 선택");
        List<String> listItems = new ArrayList<String>();
        for (BluetoothDevice device : mDevices) {
            listItems.add(device.getName());
        }
        AlertDialog alert = null;
        final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                connectToSelectdDevice(mDevices, items[item].toString());
            }
        });
        alert = builder.create();
        alert.show();
    }

    void connectToSelectdDevice(Set<BluetoothDevice> mDevices, String selectedDeviceName) {
        BluetoothDevice mRemoteDevice = getDeviceFromBondedList(mDevices, selectedDeviceName);
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        try {
            // 소켓 생성
            mSocket = mRemoteDevice.createRfcommSocketToServiceRecord(uuid);
            // RFCOMM 채널을 통한 연결
            mSocket.connect();

            // 데이터 송수신을 위한 스트림 열기
            mOutputStream = mSocket.getOutputStream();
            mInputStream = mSocket.getInputStream();

            // 데이터 수신 준비
            beginListenForData();
            Log.d("준비", "성공!!");
        } catch (Exception e) {
            e.printStackTrace();
            // 블루투스 연결 중 오류 발생
        }
    }

    BluetoothDevice getDeviceFromBondedList(Set<BluetoothDevice> mDevices, String name) {
        BluetoothDevice selectedDevice = null;

        for (BluetoothDevice device : mDevices) {
            if (name.equals(device.getName())) {
                selectedDevice = device;
                break;
            }
        }
        return selectedDevice;
    }

    void beginListenForData() {

        final byte[] readBuffer = new byte[1024];  //  수신 버퍼
        //   버퍼 내 수신 문자 저장 위치

        // 문자열 수신 쓰레드
        mWorkerThread = new Thread(new Runnable() {
            public void run() {
                final byte[] reaultByte = new byte[5];
                int offset = 0;
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        int bytesAvailable = mInputStream.available();    // 수신 데이터 확인
                        if (bytesAvailable > 0) {
                            Log.d("로그", "들어옴!");// 데이터가 수신된 경우
                            byte[] packetBytes = new byte[bytesAvailable];
                            mInputStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                Log.d("로그", i + "->" + (char) b);
                                switch (b) {
                                    case 'F':
                                    case 'S':
                                    case 'N':
                                    case 'W':
                                    case 'E':
                                        reaultByte[offset++] = b;
                                        break;
                                }
                                if(offset==5)
                                    break;
                            }
                            Log.d("여기까지옴", offset+"<--");
                            if(offset==5)
                                offset = 0;
                            else
                                continue;
                            handler.post(new Runnable() {
                                public void run() {
                                    Log.d("런내로들", "어왔다!!");
                                    switch (nowType) {
                                        case SEARCHING_START:
                                        case SEARCHING:
                                            Log.d("현재!!", prePlaceVo.getX()+", "+prePlaceVo.getY());
                                            final char[] types = new char[4];
                                            for (int i = 0; i < 4; i++)
                                                types[i] = (char) reaultByte[i + 1];
                                            putNearBy(prePlaceVo, types);
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    util.displayNow(table, prePlaceVo);
                                                }
                                            });
                                            while(queue.isEmpty()) {
                                                if (stack.isEmpty() && queue.isEmpty()) {
                                                    Log.d("요기", "--");
                                                    prePlaceVo = null;
                                                    return;
                                                } else if (queue.isEmpty()) {
                                                    Log.d("요기2222", "---");
                                                    PlaceVO vo = stack.pop();
                                                    if(util.areThereNonSearchPlaces(vo))
                                                        queue.addAll(util.getPath(prePlaceVo, vo));
                                                    Log.d("요기333 : ", queue.size() + "<--");
                                                }
                                            }
                                            sendData(SEARCHING);
                                            break;
                                        case NONE:
                                            if (!queue.isEmpty()) {
                                                sendData(nowType);
                                            } else
                                                prePlaceVo = null;
                                            break;
                                    }

                                }
                            });
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        mWorkerThread.start();
    }

    public void getToThere(Queue<PlaceVO> queue, int type) {
        this.queue.addAll(queue);
        sendData(type);
    }

    public void search(PlaceVO firstVO) {
        prePlaceVo = firstVO;
        sendData(SEARCHING_START);
    }

    public void moveOnce(String direction) {
        nowType = NONE;
        try {
            mOutputStream.write(direction.getBytes());    // 문자열 전송
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendData(int moveType) {
        nowType = moveType;
        if (moveType == SEARCHING_START) {
            try {
                byte[] start = new byte[1];
                start[0] = 'F';
                mOutputStream.write(start);    // 문자열 전송
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (!queue.isEmpty()) {
            if (prePlaceVo == null)
                prePlaceVo = queue.poll();
            Log.d("!!!!!!", "!!");
            if(prePlaceVo == queue.peek())
                queue.poll();
            PlaceVO nowPlaceVO = queue.poll();
            int direction = direction(nowPlaceVO, prePlaceVo);
            Log.d("!!위치!!", nowPlaceVO.getX()+", "+nowPlaceVO.getY());
            prePlaceVo = nowPlaceVO;
            byte[] msg = new byte[1];
            switch (direction) {
                case EAST:
                    msg[0] = 'E';
                    break;
                case WEST:
                    msg[0] = 'W';
                    break;
                case SOUTH:
                    msg[0] = 'S';
                    break;
                case NORTH:
                    msg[0] = 'N';
                    break;
            }

            try {
                mOutputStream.write(msg);    // 문자열 전송
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            return;
    }

    public void finish() throws IOException {
        mWorkerThread.interrupt();
        mInputStream.close();
        mOutputStream.close();
        mSocket.close();
    }

    private int direction(PlaceVO now, PlaceVO pre) {
        int x = now.getX() - pre.getX();
        int y = now.getY() - pre.getY();
        if ((x == 0 && y == 0) || (x != 0 && y != 0)) {
            Log.d("direction : ", "에러 발생! now : "+now.getX()+", "+now.getY()+", pre :"+pre.getX()+", "+pre.getY());
        }
        switch (x) {
            case 1:
                return EAST;
            case -1:
                return WEST;
            case 0:
                switch (y) {
                    case 1:
                        return NORTH;
                    case -1:
                        return SOUTH;
                }
        }
        Log.d("direction : ", "에러 발생 2 !");
        return -1;
    }

    private void putNearBy(PlaceVO core, char[] types) {
        for (int i = 3; i >= 0; i--) {
            PlaceVO placeVO = null;
            int direction = -1;
            switch (i) {
                case 0:
                    direction = NORTH;
                    placeVO = PlaceVO.getNext(direction, core);
                    break;
                case 1:
                    direction = EAST;
                    placeVO = PlaceVO.getNext(direction, core);
                    break;
                case 2:
                    direction = SOUTH;
                    placeVO = PlaceVO.getNext(direction, core);
                    break;
                case 3:
                    direction = WEST;
                    placeVO = PlaceVO.getNext(direction, core);
                    break;
            }
            if (placeVO == null) {
                Log.d("널이다!", "--");
                int type = -1;
                switch (types[i]) {
                    case 'E':
                        type = EMPTY;
                        break;
                    case 'W':
                        type = WALL;
                        break;
                }
                placeVO = new PlaceVO(type, core, direction);
                PlaceVO.putOneVO(placeVO);
                if(placeVO.getType()==EMPTY)
                    stack.add(placeVO);
            } else {
                if (!stack.contains(placeVO))
                    if(placeVO.getType()==EMPTY)
                        stack.add(placeVO);
            }

        }

    }
}