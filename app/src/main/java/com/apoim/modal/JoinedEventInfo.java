package com.apoim.modal;

import java.util.List;

/**
 * Created by Anil on 2/5/18.
 **/

public class JoinedEventInfo {

    public String status;
    public String message;
    public java.util.List<ListBean> List;

    public static class ListBean {
       
        public String eventId;
        public String eventMemId;
        public String memberId;
        public String memberName;
        public String memberImage;
        public String memberStatus;
        public String companionEventMemberId;
        public String companionName;
        public String companionId;
        public String companionInvitedBy;
        public String companionMemberStatus;
        public String companionImage;
        public String workName;

    }
}
