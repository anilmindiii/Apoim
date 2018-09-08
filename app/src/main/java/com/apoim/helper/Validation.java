package com.apoim.helper;

import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by abc on 2/13/2018.
 */

public class Validation {
    private final String NAME = "^[a-zA-Z ]+$";

    public boolean isNullValue(String string) {
        if (string.isEmpty() && string.equals("")) {
            return false;
        }
        return true;
    }

    public boolean isNull(TextView editText) {
        String str = editText.getText().toString().trim();
        if (str.isEmpty() && str.equals("")) {
            return false;
        }
        return true;
    }

    public boolean isDoublezero(TextView editText) {
        Double aDouble = Double.valueOf(editText.getText().toString().trim());
        if (aDouble == 0.0) {
            return false;
        }
        return true;
    }


    public boolean isLength3Minimum(String string) {
        if (string.length() == 3) {
            return false;
        }
        return true;
    }

    public boolean isLength25Max(String string) {
        if (string.length() == 25) {
            return false;
        }
        return true;
    }


    public boolean isEmailValid(String string) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(string).matches();
    }

    public boolean isPasswordValid(String string) {
        return string.length() >= 8;
    }

    public boolean isNameValid(String string) {
        return string.length() >= 2;
    }

    public boolean isNameAlphabets(String string) {
        Pattern pattern = Pattern.compile(NAME, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(string);
        return matcher.find();
    }
}
