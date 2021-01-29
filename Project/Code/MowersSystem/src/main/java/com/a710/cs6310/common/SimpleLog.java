package com.a710.cs6310.common;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class SimpleLog {
    static final boolean FLAG = true;
    FileWriter _logWrite;
    static final SimpleLog _instance = new SimpleLog();

    public void log(String str) {
        System.out.println(str);
        if (FLAG) {
            try {
                _logWrite.write(str + "\n");
                _logWrite.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static final SimpleLog getInstance() {
        return _instance;
    }

    private SimpleLog() {
        if (FLAG) {
            try {
                String fileName = new SimpleDateFormat("yyyyMMddHHmm'.txt'").format(new Date());
                _logWrite = new FileWriter(fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
