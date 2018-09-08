package com.apoim.modal;

import java.util.List;

/**
 * Created by mindiii on 24/4/18.
 */

public class MyEventInfo {


    public String status;
    public String message;
    public String currentDate;
    public java.util.List<MyEventInfo.ListBean> List;


    public static class ListBean {

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


    }

}
