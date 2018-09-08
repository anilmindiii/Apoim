package com.apoim.modal;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mindiii on 8/3/18.
 */

public class UserListInfo {

    public String status;
    public String message;
    public List<NearByUsersBean> nearByUsers;

    public static class NearByUsersBean implements Serializable{

        public String isAppointment;
        public String userId;
        public String fullName;
        public String address;
        public String age;
        public String gender;
        public String latitude;
        public String longitude;
        public String showOnMap;
        public double distance;
        public String profileImage;
        public String mapPayment;
        public String showTopPayment;
        public String status = "";

    }
}
