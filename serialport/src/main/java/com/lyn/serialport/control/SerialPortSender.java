package com.lyn.serialport.control;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author liuyn
 */

public class SerialPortSender {
    public static int send(String serialPortFileName, byte[] bytes) {
        File file = new File(serialPortFileName);
        try {
            FileOutputStream fileOperation = new FileOutputStream(file);
            fileOperation.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }
}
