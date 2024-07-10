package yoon.capstone.application.enums;

import lombok.Getter;

@Getter
public enum Category {

    졸업("졸업", 1),

    생일("생일", 2);

    private String value;

    private int key;

    Category(String value, int key){
        this.value = value;
        this.key = key;
    }

    public String getValue(){
        return this.value;
    }
}
