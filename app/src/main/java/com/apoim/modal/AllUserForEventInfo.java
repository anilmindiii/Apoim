package com.apoim.modal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mindiii on 18/10/18.
 */

public class AllUserForEventInfo {

    public String status;
    public DataBean data;

    public static class DataBean {
        public ArrayList<UserBean> user;

        public static class UserBean {

            public String userId;
            public String fullName;
            public String age;
            public String gender;
            public String address;
            public String latitude;
            public String longitude;
            public String eventInvitation;
            public String total_rating;
            public String profileImage;
            public boolean isSelected;

        }
    }
}
