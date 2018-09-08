package com.apoim.modal;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mindiii on 28/3/18.
 */

public class MyFavoriteListInfo implements Serializable{
    public String status;
    public String message;
    public List<FavoriteDataBean> favoriteList;

    public static class FavoriteDataBean implements Serializable{
        public String favId;
        public String user_id;
        public String favUserId;
        public String crd;
        public String fullName;
        public String profileImage;
        public String workName;
    }
}