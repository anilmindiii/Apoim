package com.apoim.modal;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mindiii on 10/4/18.
 */

public class NotificationInfo implements Serializable {
    
    public String status;
    public String message;
    public List<NotificationListBean> notificationList;
    

    public static class NotificationListBean {
        
        public String notId;
        public String isRead;
        public String notificationBy;
        public String notificationFor;
        public MessageBean message;
        public String notificationType;
        public String crd;
        public String fullName;
        public String image;
        public String timeElapsed;


        public static class MessageBean {

            public String title;
            public String body;
            public String type;
            public String sound;
            public String referenceId;
            public String createrId;
            public String compId;
            public String eventMemId;

        }
    }
}
