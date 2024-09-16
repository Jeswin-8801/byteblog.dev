package com.jeswin8801.byteBlog.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class MailUtil {

    public int generateCode() {
        return new Random().nextInt(100000, 1000000);
    }


    @Getter
    @AllArgsConstructor
    public enum MailTemplatePathEnums {
        EMAIL_VERIFICATION_MAIL("/verify-email-template.ftl"),
        RESET_PASSWORD_MAIL("/reset-password-template.ftl");

        private final String templatePath;
    }

    public static class TemplateKeys {

        // Verification code template Keys
        public static final String linkEmailVerification = "linkEmailVerification";

        // Password reset template keys
        public static final String linkPasswordReset = "linkPasswordReset";

    }
}
