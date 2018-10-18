package com.apoim.modal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anil on 24/4/18.
 */

public class EventRequestInfo {

    public String status;
    public String message;
    public String currentDate;
    public ArrayList<ListBean> List;

    public static class ListBean {

        public String compId;
        public String eventMemId;
        public String event_id;
        public String memberId;
        public String memberType;
        public String memberStatus;
        public String invitedBy;
        public String eventId;
        public String eventName;
        public String eventStartDate;
        public String eventEndDate;
        public String eventPlace;
        public String privacy;
        public String payment;
        public String eventAmount;
        public String currencySymbol;
        public String status;
        public String fullName;
        public String profileImage;
        public String ownerType;
        public String eventImage;
    }
}
