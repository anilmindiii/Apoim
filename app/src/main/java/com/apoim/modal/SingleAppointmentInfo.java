package com.apoim.modal;

import java.io.Serializable;

/**
 * Created by mindiii on 30/3/18.
 */

public class SingleAppointmentInfo  implements Serializable{
    
    public String status;
    public String message;
    public String date;
    public AppoimDataBean appoimData;

    public static class AppoimDataBean implements Serializable {
        
        public String appId;
        public String appointById;
        public String appointForId;
        public String business_id;
        public String appointDate;
        public String appointTime;
        public String appointAddress;
        public String appointLatitude;
        public String appointLongitude;
        public String appointmentStatus;
        public String offerPrice;
        public String counterPrice;
        public String counterStatus;
        public String isCounterApply;
        public String offerType;
        public String appointByStatus;
        public String appointForStatus;
        public String status;
        public String isFinish;
        public String isDelete;
        public String crd;
        public String upd;
        public String ByName;
        public String ForName;
        public String ByGender;
        public String ForGender;
        public String ByAddress;
        public String ForAddress;
        public String ByLatitude;
        public String ForLatitude;
        public String ByLongitude;
        public String ForLongitude;
        public String byImage;
        public String forImage;
        public String businessId;
        public String businessName;
        public String businessAddress;
        public String businesslat;
        public String businesslong;
        public String businessImage;
        public Object reviewByUserId;
        public Object reviewByRating;
        public Object reviewByComment;
        public Object reviewByCreatedDate;
        public Object reviewForUserId;
        public Object reviewForRating;
        public Object reviewForComment;
        public Object reviewForCreatedDate;

       
         
    }
}
