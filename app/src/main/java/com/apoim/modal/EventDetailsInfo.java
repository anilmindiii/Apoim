package com.apoim.modal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mindiii on 25/4/18.
 */

public class EventDetailsInfo  implements Serializable{
    
    public String status;
    public String message;
    public String currentDate;
    public DetailBean Detail = new DetailBean();
    public List<JoinedMemberBean> joinedMember = new ArrayList<>();
    public List<InvitedMember> invitedMember = new ArrayList<>();
    public List<CompanionMember> companionMember = new ArrayList<>();
    
    public static class DetailBean implements Serializable{
        
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
        public String firstImage;
        public String businessId;
        public String memberIds;

        public String businessName;
        public String businessAddress;
        public String businesslat;
        public String businesslong;
        public String businessImage;

        public String ownerName;
        public String ownerImage;

        public List<EventImage> eventImage;

    }

    public static class EventImage implements Serializable{

        public String eventImage;
        public String eventImgId;

    }

    public static class JoinedMemberBean  implements Serializable{
      
        public String eventMemId;
        public String memberId;
        public String eventId;
        public String fullName;
        public String userImg;
    }
    
    public static class InvitedMember  implements Serializable {
      
        public String eventMemId;
        public String memberId;
        public String eventId;
        public String fullName;
        public String userImg;
    } 
    
    public static class CompanionMember  implements Serializable{
      
        public String eventMemId;
        public String memberId;
        public String eventId;
        public String fullName;
        public String userImg;
    }
}
