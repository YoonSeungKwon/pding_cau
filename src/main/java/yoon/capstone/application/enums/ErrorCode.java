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
    MEMBER_USERNAME_NOTFOUND("MEMBER_USERNAME_NOTFOUND", "존재하지 않는 회원입니다."),

    //Authorization Error

    ACCESS_TOKEN_EXPIRED("ACCESS_TOKEN_EXPIRED", "토큰이 만료되었습니다."),
    REFRESH_TOKEN_EXPIRED("REFRESH_TOKEN_EXPIRED", "토큰이 만료되었습니다."),


    //Friends Error

    ALREADY_FRIENDS("ALREADY_FRIENDS", "이미 친구로 등록된 회원입니다."),

    NOT_FRIENDS("NOT_FRIENDS", "친구로 등록되지 않은 회원입니다."),

    //Server Error

    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버가 동작하지 않습니다.");

    private final String status;
    private final String message;

    ErrorCode(String status, String message){
        this.status = status;
        this.message = message;
    }

    public String getStatus(){
        return this.status;
    }

    public String getMessage(){
        return this.message;
    }


}
