package com.pawsql.client.util;

import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.pawsql.client.PawWindowFactory;

public class ConsoleLogUtils {
    public static void showLog(Project project) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("PawSQL Client");
        if (PawWindowFactory.getLogConsole() != null) {
            PawWindowFactory.getLogConsole().clear();
            PawWindowFactory.printHeader(PawWindowFactory.getLogConsole());
        }
        //Show tool window
        showToolWindow(project, toolWindow);

    }

    static private void showToolWindow(Project project, ToolWindow toolWindow) {
        if (project != null) {
            if (toolWindow != null) {
                toolWindow.show(() -> {
                    TextConsoleBuilder consoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(project);
                    consoleBuilder.getConsole().clear();

                    ContentManager contentManager = toolWindow.getContentManager();
                    // 通过displayName选择
                    Content targetContent = contentManager.findContent("PawSQL Jetbrains");
                    if (targetContent != null) {
                        contentManager.setSelectedContent(targetContent);
                    }
                });
            }
        }
    }
}
