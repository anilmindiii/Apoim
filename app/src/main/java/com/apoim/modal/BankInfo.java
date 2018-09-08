package com.apoim.modal;

import java.io.Serializable;

/**
 * Created by mindiii on face_verification/face_verification/18.
 */

public class BankInfo implements Serializable {


    public String status;
    public String message;
    public AccountDetailBean accountDetail;
    
    public static class AccountDetailBean {
        
        public String bankAccId;
        public String user_id;
        public String firstName;
        public String lastName;
        public String userName;
        public String dob;
        public String routingNumber;
        public String accountNumber;
        public String postalCode;
        public String ssnLast;
        public String accountId;
        public String status;
        public String crd;
        public String upd;
        
    }
}
