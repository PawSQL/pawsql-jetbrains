package com.pawsql.client.util;

import com.intellij.openapi.diagnostic.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.stream.IntStream;

public class MessageConsoleStream extends OutputStream {
    private final Logger logger = Logger.getInstance(MessageConsoleStream.class);
    private final MessageConsole console;
    private boolean closed = false;
    private boolean prependCR;

    public MessageConsoleStream(MessageConsole console) {
        this.console = console;
    }

    @Override
    public void write(int b) throws IOException {
        char[] b1 = new char[1];
        b1[0] = (char) b;
        write(b1);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        Objects.checkFromIndexSize(off, len, b.length);
        byte[] b1 = new byte[len];
        IntStream.range(0, len).forEach(i -> b1[i] = b[off + i]);
        write(new String(b1));
    }

    @Override
    public void flush() throws IOException {
        if (closed) {
            throw new IOException("Output Stream is closed");
        }
    }

    @Override
    public synchronized void close() throws IOException {
        if (closed) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        if (prependCR) { // force writing of last /r
            prependCR = false;
            builder.append('\r');
        }
        write(builder.toString());
        console.streamClosed(this);
        closed = true;
    }

    public void print(String message) {
        try {
            write(message);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    public void println() {
        try {
            write("\n");
        } catch (IOException e) {
            logger.error(e);
        }
    }

    public synchronized boolean isClosed() {
        return closed;
    }

    private void write(char[] buffer) throws IOException {
        String str = new String(buffer);
        this.write(str);
    }

    private void write(String encodedString) throws IOException {
        if (closed) {
            throw new IOException("Output Stream is closed");
        }
        String str = encodedString;
        if (prependCR) {
            str = "\r" + str;
            prependCR = false;
        }
        if (str.endsWith("\r")) {
            prependCR = true;
            str = str.substring(0, str.length() - 1);
        }
        console.write(str);
    }
}
