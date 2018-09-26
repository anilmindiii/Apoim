package com.apoim.modal;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mindiii on 26/9/18.
 */

public class ApoimReviewInfo implements Serializable{
    
    public String status;
    public String message;
    public String date;
    public List<ReviewListBean> reviewList;

    public static class ReviewListBean {
        
        public String rating;
        public String comment;
        public String crd;
        public String fullName;
        public String profileImage;
        
    }
}
