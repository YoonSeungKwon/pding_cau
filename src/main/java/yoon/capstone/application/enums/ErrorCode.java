package yoon.capstone.application.enums;

public enum ErrorCode {


    //Register, Login Error (400)
    MEMBER_EMAIL_BLANK("MEMBER_EMAIL_BLANK", "이메일을 입력해 주세요."),
    MEMBER_EMAIL_FORMAT("MEMBER_EMAIL_FORMAT", "이메일 형식이 아닙니다."),
    MEMBER_EMAIL_DUPLICATED("MEMBER_EMAIL_DUPLICATED", "이미 존재하는 이메일 주소입니다."),
    MEMBER_PASSWORD_BLANK("MEMBER_PASSWORD_BLANK", "비밀번호를 입력해 주세요."),
    MEMBER_USERNAME_BLANK("MEMBER_USERNAME_BLANK", "이름을 입력해 주세요."),

    MEMBER_EMAIL_NOTFOUND("MEMBER_EMAIL_NOTFOUND", "존재하지 않는 이메일 주소입니다."),
    MEMBER_PASSWORD_NOTFOUND("MEMBER_PASSWORD_NOTFOUND", "이메일 또는 비밀번호가 일치하지 않습니다."),
    MEMBER_USERNAME_NOTFOUND("MEMBER_USERNAME_NOTFOUND", "존재하지 않는 회원입니다.");

    //Authorization Error


    //Friends Error


    private final String code;
    private final String message;

    ErrorCode(String code, String message){
        this.code = code;
        this.message = message;
    }

    public String getCode(){
        return this.code;
    }

    public String getMessage(){
        return this.message;
    }


}
