package com.apoim.modal;

import java.util.List;

/**
 * Created by mindiii on 26/4/18.
 */

public class InvitedListInfo {
    
    public String status;
    public String message;
    public java.util.List<ListBean> List;
    
    public static class ListBean {
       
        public String eventMemId;
        public String memberId;
        public String eventId;
        public String fullName;
        public String userImg;
        public String workName;
        
    }
}
