package com.apoim.modal;

import java.util.List;

/**
 * Created by mindiii on 17/5/18.
 */

public class CompanionInfo {
    
    public String status;
    public String message;
    public String currentDate;
    public List<ListBean> List;
    
    public static class ListBean {

        public String compId;
        public String eventMem_Id;
        public String companionMemId;
        public String companionMemberStatus;
        public String upd;
        public String eventId;
        public String eventEndDate;
        public String fullName;
        public String userImg;
        
    }
}
