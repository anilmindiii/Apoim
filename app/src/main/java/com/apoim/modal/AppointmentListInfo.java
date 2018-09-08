package com.apoim.modal;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mindiii on 15/3/18.
 */

public class AppointmentListInfo implements Serializable{

    public String status;
    public String message;
    public List<AppoimDataBean> appoimData;

    public static class AppoimDataBean {
        
        public String appId;
        public String appointById;
        public String appointForId;
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
        public String appointDateTime;
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

    }
}
