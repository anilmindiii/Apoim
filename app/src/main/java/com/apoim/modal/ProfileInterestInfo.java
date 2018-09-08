package com.apoim.modal;

/**
 * Created by mindiii on 26/2/18.
 */

public class ProfileInterestInfo {
    public String interestId;
    public String interest;
    public boolean isChecked = false;


    public ProfileInterestInfo(String interestId, String interest) {
        this.interestId = interestId;
        this.interest = interest;
    }

    public ProfileInterestInfo() {
    }
}
