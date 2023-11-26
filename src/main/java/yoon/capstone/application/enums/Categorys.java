package yoon.capstone.application.enums;

import lombok.Getter;

@Getter
public enum Categorys {

    졸업("졸업", 1),

    생일("생일", 2);

    private String value;

    private int key;

    Categorys(String value, int key){
        this.value = value;
        this.key = key;
    }

    public String getValue(){
        return this.value;
    }
}
