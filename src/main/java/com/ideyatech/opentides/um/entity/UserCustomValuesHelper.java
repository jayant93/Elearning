package com.ideyatech.opentides.um.entity;

import java.util.ArrayList;

/**
 * Created by Gino on 10/18/2016.
 */
public class UserCustomValuesHelper {

    private String customFieldKey;

    private String value;

    public String getCustomFieldKey() {
        return customFieldKey;
    }

    public void setCustomFieldKey(String customFieldKey) {
        this.customFieldKey = customFieldKey;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static class UserCustomValuesHelperList extends ArrayList<UserCustomValuesHelper> {

    }

}
