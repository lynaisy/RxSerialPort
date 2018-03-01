package com.lyn.serialport.control;

import com.lyn.serialport.android_serialport_api.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * @author liuyn
 */

public class SerialPortListener {
    private static Map<String, Boolean> mapIsListening = new HashMap<>();
    private static Map<String, SerialPort> mapSerialPort = new HashMap<>();

    /**
     * 打开串口并开始监听
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

    public void stop(String serialPortFileName) {
        mapIsListening.remove(serialPortFileName);
        mapSerialPort.remove(serialPortFileName);
    }

    public void stopAll() {
        mapIsListening.clear();
        mapSerialPort.clear();
    }
}
