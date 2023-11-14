package com.yyh.mark_book.notice;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.Notifications;
import com.intellij.openapi.ui.MessageType;

public class Notice {
    public static void success_file_notice(){
        NotificationGroup notificationGroup = new NotificationGroup("markbook_id", NotificationDisplayType.BALLOON,true);
        Notification notification = notificationGroup.createNotification("生成文档成功", MessageType.INFO);
        Notifications.Bus.notify(notification);
    }
}
