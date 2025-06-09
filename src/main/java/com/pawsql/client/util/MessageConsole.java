package com.pawsql.client.util;

import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageConsole {
    private final List<Closeable> openStreams = Collections.synchronizedList(new ArrayList<>());
    public boolean isShow;
    private final ConsoleView console;

    public MessageConsole(ConsoleView console, @NotNull Project project) {
        this.console = console;
    }

    public MessageConsoleStream newMessageStream() {
        MessageConsoleStream messageConsoleOutStream = new MessageConsoleStream(this);
        addOpenStream(messageConsoleOutStream);
        return messageConsoleOutStream;
    }

    void streamClosed(MessageConsoleStream stream) {
        synchronized (openStreams) {
            openStreams.remove(stream);
        }
    }

    public void dispose() {
        synchronized (this) {
            List<Closeable> list = new ArrayList<>(openStreams);
            for (Closeable closable : list) {
                try {
                    closable.close();
                } catch (IOException e) {
                }
            }
        }
        console.dispose();
    }

    private void addOpenStream(Closeable stream) {
        synchronized (openStreams) {
            openStreams.add(stream);
        }
    }

    public void write(String str) {
        console.print(str, ConsoleViewContentType.LOG_INFO_OUTPUT);
    }
}
