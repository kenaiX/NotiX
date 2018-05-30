package com.flyme.systemuitools;

import android.os.UserHandle;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class UserHandleUtils {
    private static UserHandleUtils sMyUser;

    public static UserHandleUtils myUserHandle() {
        if (sMyUser == null) {
            sMyUser = new UserHandleUtils();
        }
        return sMyUser;
    }

    private static int muUserId = Integer.MIN_VALUE;

    public static int myUserId() {
        if (muUserId == Integer.MIN_VALUE) {
            try {
                Method method = UserHandle.class.getMethod("myUserId");
                muUserId = (int) method.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return muUserId;
    }

    public static int getUserId(UserHandle user) {
        try {
            Field handle = UserHandle.class.getDeclaredField("mHandle");
            handle.setAccessible(true);
            return handle.getInt(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return myUserId();
    }

    private static boolean sMultiOpenUserIdFlag = true;

    public static boolean isMultiOpenUserId(int userId) {
        if (sMultiOpenUserIdFlag) {
            try {
                Method isMultiOpenUserId = UserHandle.class.getMethod("isMultiOpenUserId", Integer.TYPE);
                return (boolean) isMultiOpenUserId.invoke(null, userId);
            } catch (Exception e) {
                e.printStackTrace();
                sMultiOpenUserIdFlag = false;
            }
        }
        return false;
    }
}