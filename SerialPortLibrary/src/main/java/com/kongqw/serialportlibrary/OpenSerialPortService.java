package com.kongqw.serialportlibrary;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;

import java.io.File;
import java.util.Arrays;

/**
 * @author LiChuang
 * @version 1.0
 * @ClassName OpenSerialPortService
 * @description  此方法用来打开SY1串口进行两设备之间的通讯
 * @since 2019/11/29 9:33
 **/
public class OpenSerialPortService implements OnOpenSerialPortListener {

private  final   String  TAG   = "OpenSerialPortService";

private SerialPortManager mSerialPortManager;
//双屏通讯地址
private  static    String  SERIAL_PORT   =  "/dev/ttyS4";
//波特率
private  static    int  BAUD_RATE   =  9600;

    public interface  OnSerialPortReceiveDataListener{
        void onSerialPortReceiveData(String data);
    }

    public void open(final OnSerialPortReceiveDataListener listener){
        open("",0,listener);
    }


    public  void  open (String serialPort ,int baudRate, final OnSerialPortReceiveDataListener listener ){
        mSerialPortManager = new SerialPortManager();
        if(serialPort!=null && !serialPort.equals("") &&serialPort.length()>0){
            SERIAL_PORT = serialPort;
        }
        if (baudRate > 0) {
            BAUD_RATE =baudRate;
        }
        // 打开串口
        boolean openSerialPort = mSerialPortManager.setOnOpenSerialPortListener(this)
                .setOnSerialPortDataListener(new OnSerialPortDataListener() {
                    @Override
                    public void onDataReceived(byte[] bytes) {
                        String data = new String(bytes);
                        listener.onSerialPortReceiveData(data);
                    }
                    @Override
                    public void onDataSent(byte[] bytes) {
                        String data = new String(bytes);
                        Log.i(TAG,  String.format("发送\n\n%s", data));
                    }
                })
                .openSerialPort(new File(SERIAL_PORT), BAUD_RATE);
        Log.i(TAG, "onCreate: openSerialPort = " + openSerialPort);
    }

    @Override
    public void onSuccess(File device) {
        Log.i(TAG,  String.format("串口 [%s] 打开成功", device.getPath()));
    }

    public  void   closeSerialPort(){
        if(mSerialPortManager!=null)
            mSerialPortManager.closeSerialPort();
    }
    /**
     * 串口打开失败
     *
     * @param device 串口
     * @param status status
     */
    @Override
    public void onFail(File device, Status status) {
        switch (status) {
            case NO_READ_WRITE_PERMISSION:
                Log.e(device.getPath(), "没有读写权限");
                break;
            case OPEN_FAIL:
            default:
                Log.e(device.getPath(), "串口打开失败");
                break;
        }
    }


    /**
     * 发送数据
     */
    public void onSend(String sendContent) {
        byte[] sendContentBytes = sendContent.getBytes();
        boolean sendBytes = mSerialPortManager.sendBytes(sendContentBytes);
        Log.i(TAG, "onSend: sendBytes = " + sendBytes + ":" + sendContent);
    }
    /**
     * 发送数据
     */
    public void onSend(byte[] sendContent) {
        boolean sendBytes = mSerialPortManager.sendBytes(sendContent);
        Log.i(TAG, "onSend: sendBytes = " + sendBytes + ":" + Arrays.toString(sendContent));
    }

}
