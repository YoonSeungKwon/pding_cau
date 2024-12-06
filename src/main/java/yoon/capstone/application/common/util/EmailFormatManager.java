package yoon.capstone.application.common.util;

import yoon.capstone.application.common.enums.Provider;

public final class EmailFormatManager {

    public static String toPersist(String email, Provider provider){
        StringBuilder sb = new StringBuilder();
        return email = sb.append(email).append("?").append(Provider.DEFAULT.getProvider()).toString();
    }

    public static String toEmail(String email){
        return email.substring(0, email.indexOf("?"));
    }

}
