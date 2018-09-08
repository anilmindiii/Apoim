package com.apoim.modal;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mindiii on 30/7/18.
 */

public class BusinessListInfo implements Serializable{

    public String status;
    public String message;
    public List<BusinessListBean> businessList;

    public static class BusinessListBean {

        public String businessId;
        public String businessName;
        public String businessAddress;
        public String businesslat;
        public String businesslong;
        public String businessImage;
        public String userId;
        public String bizSubscriptionId;
        public String bizSubscriptionStatus;
        public String distance;
    }
}
