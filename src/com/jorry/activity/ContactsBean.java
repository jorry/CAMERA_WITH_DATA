package com.jorry.activity;

import java.util.Comparator;

import android.text.TextUtils;

public class ContactsBean implements Comparator<ContactsBean>{
    
    public int type;
    
    private int id ;
    private String name ;
    private String number ;
    private float matcher ;
    
    public ContactsBean() {
    }
    
    public ContactsBean(int id ,String name, String number, float matcher) {
        this.id = id ;
        this.name = name;
        this.number = number;
        this.matcher = matcher;
    }

    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        if(TextUtils.isEmpty(number)){
            return "";
        }
        if (number.startsWith("+86")) {
            number = number.replace("+86", "");
        }
        number = number.replace(" ", "").replace("-", "");

        return number.replaceAll(" ", "").replaceAll("-", "");
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public float getMatcher() {
        return matcher;
    }

    public void setMatcher(float matcher) {
        this.matcher = matcher;
    }

    @Override
    public int compare(ContactsBean lhs, ContactsBean rhs) {
        
        if(lhs.getMatcher() > rhs.getMatcher()){
            return -1 ;
        }else if(lhs.getMatcher() < rhs.getMatcher()){
            return 1 ;
        }else {
            return 0 ;
        }
    }
    
    @Override
    public String toString() {
        
        return "{"+this.matcher+", "+this.name+"("+ "" +")"+", "+this.number+"}";
    }
    
    @Override
    public boolean equals(Object o) {
        
        return this.name.equalsIgnoreCase(((ContactsBean)o).name);
    }
}
