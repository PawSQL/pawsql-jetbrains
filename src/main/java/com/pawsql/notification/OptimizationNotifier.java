package com.pawsql.notification;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.pawsql.model.StatementDetailInfoRead;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OptimizationNotifier {
    private static final String GROUP_ID = "PawSQL Notification Group";

    @NotNull
    public static Notification notifySuccess(@Nullable Project project, StatementDetailInfoRead result) {
        String content = String.format(
                "Optimization completed\n" +
                        "Performance improved by: %.2f%%\n" +
                        "Query suggestions: %d\n" +
                        "Indexes recommended: %d",
                result.getValidationDetails().getPerformance(),
                result.getRewrittenQuery() != null ? result.getRewrittenQuery().size() : 0,
                result.getIndexRecommended() != null ? result.getIndexRecommended().size() : 0
        );

        Notification notification = new Notification(
                GROUP_ID,
                "Optimization completed",
                content,
                NotificationType.INFORMATION
        );

        notification.addAction(new NotificationAction("查看详情") {
            @Override
            public void actionPerformed(AnActionEvent e, Notification notification) {
                // TODO: 打开优化结果详情页面
            }
        });

        Notifications.Bus.notify(notification, project);
        return notification;
    }

    public static void notifyError(@Nullable Project project, @NotNull String message) {
        Notification notification = new Notification(
                GROUP_ID,
                "PawSQL error",
                message,
                NotificationType.ERROR
        );
        Notifications.Bus.notify(notification, project);
    }

    @NotNull
    public static Notification notifyInfo(@Nullable Project project, @NotNull String title, @NotNull String content) {
        Notification notification = new Notification(
                GROUP_ID,
                title,
                content,
                NotificationType.INFORMATION
        );

        Notifications.Bus.notify(notification, project);
        return notification;
    }
}
