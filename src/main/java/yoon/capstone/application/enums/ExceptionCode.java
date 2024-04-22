package yoon.capstone.application.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionCode {


    //Register, Login Exception

    MEMBER_EMAIL_BLANK( "이메일을 입력해 주세요.", HttpStatus.BAD_REQUEST),
    MEMBER_EMAIL_FORMAT("이메일 형식이 아닙니다.", HttpStatus.BAD_REQUEST),
    MEMBER_EMAIL_DUPLICATED( "이미 존재하는 이메일 주소입니다.", HttpStatus.BAD_REQUEST),
    MEMBER_PASSWORD_BLANK( "비밀번호를 입력해 주세요.", HttpStatus.BAD_REQUEST),
    MEMBER_USERNAME_BLANK( "이름을 입력해 주세요.", HttpStatus.BAD_REQUEST),

    MEMBER_EMAIL_NOTFOUND("존재하지 않는 이메일 주소입니다.", HttpStatus.UNAUTHORIZED),
    MEMBER_PASSWORD_INVALID( "이메일 또는 비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    MEMBER_USERNAME_NOTFOUND( "존재하지 않는 회원입니다.", HttpStatus.UNAUTHORIZED),

    //Authorization Exception

    UNAUTHORIZED_ACCESS("인증이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    ACCESS_TOKEN_EXPIRED("토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED("토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),


    //Friends Exception

    ALREADY_FRIENDS("이미 친구로 등록된 회원입니다.", HttpStatus.FORBIDDEN),
    NOT_FRIENDS( "친구로 등록되지 않은 회원입니다.", HttpStatus.FORBIDDEN),
    SELF_FRIENDS( "본인은 등록할 수 없습니다.", HttpStatus.FORBIDDEN),


    //Project Exception

    PROJECT_OWNER("해당 기능을 이용할 권한이 없습니다.", HttpStatus.FORBIDDEN),

    TITLE_NOT_BLANK( "제목을 작성해주세요.", HttpStatus.BAD_REQUEST),

    LINK_NOT_BLANK( "상품 링크를 첨부해주세요.", HttpStatus.BAD_REQUEST),

    GOAL_NOT_BLANK( "목표 금액을 설정해주세요.", HttpStatus.BAD_REQUEST),

    DATE_NOT_BLANK( "마감 기한을 설정해주세요.", HttpStatus.BAD_REQUEST),

    DATE_NOT_FUTURE( "마감 기한을 확인해주세요.", HttpStatus.BAD_REQUEST),

    //Util Exception
    NOT_IMAGE_FORMAT("이미지 파일만 업로드 할 수 있습니다.", HttpStatus.BAD_REQUEST),

    FILE_SIZE_EXCEEDED( "10MB 이하의 파일만 업로드 할 수 있습니다.", HttpStatus.BAD_REQUEST),

    //Order Exception

    ORDER_NOT_FOUND("주문정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    //Server Exception

    INTERNAL_SERVER_ERROR("서버에서 에러가 발생하였습니다. 문의해주세요.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final HttpStatus status;


    ExceptionCode(String message, HttpStatus status){
        this.message = message;
        this.status = status;
    }


}
