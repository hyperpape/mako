package com.justinblank.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class NoOpPrintStream extends PrintStream {

    public NoOpPrintStream() {
        super(new OutputStream() {
            @Override
            public void write(int b) throws IOException {

            }
        });
    }
}


