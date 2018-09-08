package com.apoim.modal;

/**
 * Created by mindiii on 10/4/18.
 */

public class FilterItemInfo {
    public int image ;
    public String filterItemName;
    public boolean isCheck;

    public FilterItemInfo(int image, String filterItemName, boolean isCheck) {
        this.image = image;
        this.filterItemName = filterItemName;
        this.isCheck = isCheck;
    }
}
