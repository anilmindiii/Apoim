package com.apoim.modal;

/**
 * Created by mindiii on 22/8/18.
 */

public class BussinessInfo {

    public String status;
    public String message;
    public BusinessDetailBean businessDetail;

    public static class BusinessDetailBean {
       
        public String businessId;
        public String businessName;
        public String businessAddress;
        public String businesslat;
        public String businesslong;
        public String businessImage;
        public String userId;
        public String bizSubscriptionId;
        public String bizSubscriptionStatus;
        
    }
}
