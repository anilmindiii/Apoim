package com.apoim.modal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mindiii on 25/4/18.
 */

public class EventDetailsInfo {
    
    public String status;
    public String message;
    public String currentDate;
    public DetailBean Detail;
    public List<JoinedMemberBean> joinedMember = new ArrayList<>();
    public List<InvitedMember> invitedMember = new ArrayList<>();
    public List<CompanionMember> companionMember = new ArrayList<>();
    
    public static class DetailBean {
        
        public String eventMemId;
        public String memberId;
        public String eventId;
        public String eventName;
        public String eventOrganizer;
        public String eventStartDate;
        public String eventEndDate;
        public String eventPlace;
        public String eventLatitude;
        public String eventLongitude;
        public String privacy;
        public String payment;
        public String eventAmount;
        public String currencySymbol;
        public String currencyCode;
        public String userLimit;
        public String eventUserType;
        public String memberStatus;
        public String fullName;
        public String profileImage;
        public String ownerType;
        public int joinedMemberCount;
        public String companionMemberCount;
        public String invitedMemberCount ;
        public String confirmedCount ;
        public String groupChat ;

    }

    public static class JoinedMemberBean {
      
        public String eventMemId;
        public String memberId;
        public String eventId;
        public String fullName;
        public String userImg;
    }
    
    public static class InvitedMember {
      
        public String eventMemId;
        public String memberId;
        public String eventId;
        public String fullName;
        public String userImg;
    } 
    
    public static class CompanionMember {
      
        public String eventMemId;
        public String memberId;
        public String eventId;
        public String fullName;
        public String userImg;
    }
}
