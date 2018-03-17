package com.lyn.serialport.control;

import com.lyn.serialport.android_serialport_api.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * @author liuyn
 */

public class SerialPortListener {
    private static Map<String, Boolean> mapIsListening = new ConcurrentHashMap<>();
    private static Map<String, SerialPort> mapSerialPort = new ConcurrentHashMap<>();

    /**
     * 打开串口并开始监听
     *
     * @param serialPortFileName 串口名
     * @param baudrate
     * @param flags
     * @return
     * @throws Exception
     */
    public static Observable<byte[]> statListen(final String serialPortFileName, int baudrate, int flags) throws Exception {
        if (mapSerialPort.get(serialPortFileName) != null) {
            throw new Exception(serialPortFileName + "串口已经在监听了");
        }
        SerialPort serialPort = null;
        serialPort = new SerialPort(serialPortFileName, baudrate, flags);
        final InputStream inputStream = serialPort.getInputStream();
        if (inputStream == null) {
            throw new Exception(serialPortFileName + "串口输入流有问题");
        }
        mapSerialPort.put(serialPortFileName, serialPort);
        mapIsListening.put(serialPortFileName, true);
        return Observable.create(new ObservableOnSubscribe<byte[]>() {
            @Override
            public void subscribe(ObservableEmitter<byte[]> emitter) throws Exception {
                while (mapIsListening.get(serialPortFileName)) {
                    try {
                        byte[] buffer = new byte[64];
                        int size = inputStream.read(buffer);
                        if (size > 0) {
                            emitter.onNext(buffer);
                        }
                    } catch (IOException e) {
                        emitter.onError(e);
                        e.printStackTrace();
                        return;
                    }
                }
            }
        });
    }

    /**
     * 关闭一个串口的读写
     *
     * @param serialPortFileName
     */
    public static void stop(String serialPortFileName) {
        mapSerialPort.get(serialPortFileName).close();
        mapIsListening.remove(serialPortFileName);
        mapSerialPort.remove(serialPortFileName);
    }

    /**
     * 关闭所有串口的读写
     */
    public static void stopAll() {
        for (String serialPortFileName : mapSerialPort.keySet()) {
            mapSerialPort.get(serialPortFileName).close();
        }
        mapIsListening.clear();
        mapSerialPort.clear();
    }
}
