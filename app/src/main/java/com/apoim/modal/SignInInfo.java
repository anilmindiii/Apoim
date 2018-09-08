package com.apoim.modal;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mindiii on 19/2/18.
 */

public class SignInInfo implements Serializable {

    public String status;
    public String message;
    public UserDetailBean userDetail;

    public static class UserDetailBean {

        public String userId;
        public String fullName;
        public String birthday;
        public String gender;
        public String email;
        public String countryCode;
        public String contactNo;
        public String emailVerified;
        public String socialId;
        public String socialType;
        public String authToken;
        public String address;
        public String latitude;
        public String longitude;
        public String isProfileUpdate;
        public String mapPayment;
        public String showTopPayment;
        public String isNotification;

        public String stripeCustomerId;
        public String subscriptionId;
        public String subscriptionStatus;

        public String bizSubscriptionId;
        public String bizSubscriptionStatus;

        public String isBusinessAdded;


        public String totalFriends;
        public String showOnMap;
        public String bankAccountStatus;
        public List<ProfileImageBean> profileImage;
        
        public static class ProfileImageBean {
           
            public String image;
            public String userImgId;
            
        }
    }
}
