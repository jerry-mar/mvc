package com.jerry_mar.mvc;

import com.jerry_mar.mvc.utils.DateUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;

public class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler defaultHandler;
    private String path;
    private String url;

    public CustomExceptionHandler(String path, String url) {
        this.path = path;
        this.url = url;
        this.defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stacktrace = result.toString();
        printWriter.close();
        String filename = "stacktrace_" + DateUtils.parse(new Date()) + ".txt";

        if (path != null) {
            writeToFile(stacktrace, filename);
        }
        if (url != null) {
            sendToServer(stacktrace, filename);
        }

        defaultHandler.uncaughtException(t, e);
    }

    protected void writeToFile(String stacktrace, String filename) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(
                    path + "/" + filename));
            writer.write(stacktrace);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void sendToServer(String stacktrace, String filename) {
    }
}

