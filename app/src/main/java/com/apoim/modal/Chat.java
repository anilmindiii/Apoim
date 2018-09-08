package com.apoim.modal;

import java.io.Serializable;

/**
 * Created by Anil on 13/6/18.
 */

public class Chat implements Serializable {

    public String deleteby;
    public String firebaseId;
    public String firebaseToken;
    public int    image;
    public String imageUrl;
    public String message;
    public String name;
    public String profilePic;
    public Object timestamp;
    public String uid;
    public String lastMsg;

    public String readBy;
}
