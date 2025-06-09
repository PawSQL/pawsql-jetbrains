package com.pawsql.client;

import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.filters.UrlFilter;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.pawsql.client.util.MessageConsole;
import com.pawsql.client.util.MessageConsoleStream;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;

public class PawWindowFactory implements ToolWindowFactory {
    private static ConsoleView logConsole;

    public static ConsoleView getLogConsole() {
        return logConsole;
    }

    private static JComponent createConsolePanel(ConsoleView view) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(view.getComponent(), BorderLayout.CENTER);
        return panel;
    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return true;
    }

    public void clean() {
        logConsole.clear();
        printHeader(logConsole);
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        TextConsoleBuilder consoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(project);
        ContentManager contentManager = toolWindow.getContentManager();
        consoleBuilder.addFilter(new UrlFilter());
        //1. log console
        logConsole = consoleBuilder.getConsole();
        logConsole.clear();
        JComponent consolePanel = createConsolePanel(logConsole);
        Content log = contentManager.getFactory().createContent(consolePanel, "Log Console",
                false);
        toolWindow.getContentManager().addContent(log);

        printHeader(logConsole);

        // link to log4j
        MessageConsole msgConsole = new MessageConsole(logConsole, project);
        MessageConsoleStream consoleStream = msgConsole.newMessageStream();
        PrintWriter pr = new PrintWriter(consoleStream);
        try {
            PatternLayout lay = new PatternLayout("%-d{yyyy-MM-dd HH:mm:ss,SSS} [%c{1}]-[%p]%m%n");
            WriterAppender consoleWriter = new WriterAppender();
            consoleWriter.setLayout(lay);
            consoleWriter.setWriter(pr);
            consoleWriter.setName("ConsoleAppender");
            Logger.getRootLogger().addAppender(consoleWriter);
            Logger.getRootLogger().setLevel(Level.INFO);
        } catch (Exception e) {
            logConsole.print("Failed to initialize log appender: " + e.getMessage() + "\n",
                    ConsoleViewContentType.ERROR_OUTPUT);
        }

    }

    public static void printHeader(ConsoleView consoleView) {
        consoleView.print("\n",
                ConsoleViewContentType.LOG_VERBOSE_OUTPUT);
        consoleView.print("    ________                 ____________________                             \n",
                ConsoleViewContentType.LOG_VERBOSE_OUTPUT);
        consoleView.print("    ___  __ \\_____ ___      ___  ___/_  __ \\__  /                             \n",
                ConsoleViewContentType.LOG_VERBOSE_OUTPUT);
        consoleView.print("    __  /_/ /  __ `/_ | /| / /____ \\_  / / /_  / \n",
                ConsoleViewContentType.LOG_VERBOSE_OUTPUT);
        consoleView.print("    _  ____// /_/ /__ |/ |/ /____/ // /_/ /_  /___ \n",
                ConsoleViewContentType.LOG_VERBOSE_OUTPUT);
        consoleView.print("    /_/     \\__,_/ ____/|__/ /____/ \\___\\_\\/_____/\n",
                ConsoleViewContentType.LOG_VERBOSE_OUTPUT);
        consoleView.print("                            https://www.pawsql.com\n",
                ConsoleViewContentType.LOG_VERBOSE_OUTPUT);
    }
}
