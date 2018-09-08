package com.apoim.modal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mindiii on 14/3/18.
 */

public class GetOtherProfileInfo implements Serializable {
    
    public String status;
    public String message;
    public UserDetailBean UserDetail= new UserDetailBean();
    public UserDetailBean userDetail= new UserDetailBean();

    public static class UserDetailBean implements Serializable{

        public String userId;
        public String fullName;
        public String email;
        public String password;
        public String countryCode;
        public String contactNo;
        //public String profileImage;
        public String address;
        public String latitude;
        public String longitude;
        public String gender;
        public String birthday;
        public String age;
        public String height;
        public String weight;
        public String relationship;
        public String about;
        public String purpose;
        public String dateWith;
        public String eventInvitation;
        public String showOnMap;
        public String otp;
        public String emailVerified;
        public String language;
        public String socialId;
        public String socialType;
        public String authToken;
        public String deviceToken;
        public String deviceType;
        public String status;
        public String isProfileUpdate;
        public String crd;
        public String upd;
        public String work;
        public String education;
        public String interest;
        public String isFavorite;
        public String isAppointment;
        public String isRequest;
        public String isFriend;
        public String isLike;
        public String workId;
        public String eduId;
        public String totalLikes;
        public String visit;
        public String totalFriends;
        public String mapPayment;
        public String showTopPayment;

        public String stripeCustomerId;
        public String subscriptionId;
        public String subscriptionStatus;
        public String bizSubscriptionId;
        public String bizSubscriptionStatus;

        public String profileUrl;
        public String quickBloxId;
        public String eventType;
        public String appointmentType;
        public String isBusinessAdded;

        public String isVerifiedId;
        public String idWithHand;
        public String otpVerified;

        public ArrayList<ImagesBean> profileImage = new ArrayList<>();
        
        public static class ImagesBean implements Serializable{

            public String image;
            public String userImgId;
            public boolean isSelected = false;
        }
    }
}
