package duongvanbao.Book.Store.utils;

import java.util.Random;

public final class Util {
    private static final int EXPIRE_TIME_OTP = 10 * 60;
    private static final int EXPIRE_TIME_CHANGE_PASS = 20 * 60;
    public static int getExpireOTPTime() {
        return EXPIRE_TIME_OTP;
    }
    public static int getExpireTimeChangePass() {
        return EXPIRE_TIME_CHANGE_PASS;
    }
    public static String generateOtp() {
        String allowedChar = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        int otpLength = 6;
        Random random = new Random();
        StringBuilder otpCode = new StringBuilder(otpLength);
        for (int i = 0; i < otpLength; i++) {
            otpCode.append(allowedChar.charAt(random.nextInt(allowedChar.length())));
        }
        return otpCode.toString();
    }
}
