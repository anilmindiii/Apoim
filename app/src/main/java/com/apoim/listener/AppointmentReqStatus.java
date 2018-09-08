package com.apoim.listener;

/**
 * Created by mindiii on 14/3/18.
 */

public interface AppointmentReqStatus {
    void acceptRequest(String AppointmentId,int position,String isCounterApply,String appointForId);
    void rejectRequest(String AppointmentId,int position,String isCounterApply,String appointForId);
    void directionRequest(int position);

    void applyCounterTask(String appointId,String counterPrice,String appointById,int position);

}
