package com.apoim.modal;

import java.io.Serializable;

/**
 * Created by mindiii on 28/3/18.
 */

public class MyFriendListInfo implements Serializable{

    public String status;
    public String message;
    public int requestCount;
    public java.util.List<ListBean> List;

    public static class ListBean {

        public String eventInvitation;
        public String memberStatus;
        public String privacy;
        public String owner;
        public String friendId;
        public String work;
        public String userId;
        public String fullName;
        public String profileImage;
        public String gender;
        public boolean isSelected;

    }
}
