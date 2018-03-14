package com.jerry_mar.mvc.utils;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static final String NULL = "";
    public static final String RMB = "￥";
    public static final String mobileRegex = "^0?(13[0-9]|15[012356789]|18[0-9]|14[57]|17[03678])[0-9]{8}";
    public static final String emailRegex = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
    public static final String carIDRegex = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1}";

    private static final int[] WI = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1 };
    private static final int[] VI = { 1, 0, 'X', 9, 8, 7, 6, 5, 4, 3, 2 };
    private static int[] AI = new int[18];

    /**
     * @since 1.0
     * @param value 目标字符串
     * @return 是否为空
     */
    public static boolean isEmpty(String value) {
        boolean empty = false;
        if(value == null || value.trim().length() == 0) {
            empty = true;
        }
        return empty;
    }

    /**
     * @since 1.0
     * @param val 目标字符串
     * @return 字符串长度
     */
    public static int length(String val) {
        int len = 0;
        if (!isEmpty(val)) {
            len = val.length();
        }
        return len;
    }

    /**
     * @since 1.0
     * @param value 目标字符串
     * @param min 最大长度
     * @param max 最小长度
     * @return 字符串长度是否满足区间
     */
    public static boolean between(String value, int min, int max) {
        boolean result = false;
        if(!isEmpty(value)) {
            int len = value.length();
            if (len >= min && len <= max) {
                result = true;
            }
        }
        return result;
    }

    /**
     * @since 1.0
     * @param phone 手机号
     * @return 交验是否为手机号
     */
    public static boolean isMobile(String phone) {
        boolean result = false;
        if(!isEmpty(phone)) {
            result = matcher(phone, mobileRegex);
        }
        return result;
    }

    /**
     * @since 1.0
     * @param email 邮箱
     * @return 交验是否为邮箱
     */
    public static boolean isEmail(String email){
        boolean result = false;
        if(!isEmpty(email)) {
            result = matcher(email, emailRegex);
        }
        return result;
    }

    /**
     * @since 1.0
     * @param id 车牌号码
     * @return 交验是否为车牌号码
     */
    public static boolean isCarID(String id) {
        boolean result = false;
        if(!isEmpty(id)) {
            result = matcher(id, carIDRegex);
        }
        return result;
    }

    private static boolean matcher(String val, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(val);
        return matcher.matches();
    }

    /**
     * @since 1.0
     * @param cardId 银行卡号
     * @return 交验是否为银行卡号
     */
    public static boolean checkBankCard(String cardId) {
        boolean result = false;
        if(!isEmpty(cardId)) {
            char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
            if (bit == 'N') {
                return false;
            }
            result = cardId.charAt(cardId.length() - 1) == bit;
        }
        return result;
    }

    private static char getBankCardCheckCode(String nonCheckCodeCardId) {
        if(nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for(int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if(j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char)((10 - luhmSum % 10) + '0');
    }

    /**
     * @since 1.0
     * @param id 身份证号
     * @return 交验是否为身份证号
     */
    public static boolean verify(String id) {
        if(!isEmpty(id)) {
            if (id.length() == 15) {
                id = uptoeighteen(id);
            }
            if (id.length() != 18) {
                return false;
            }
            String verify = id.substring(17, 18);
            if (verify.equals(getVerify(id))) {
                return true;
            }
        }
        return false;
    }

    private static String uptoeighteen(String id) {
        StringBuffer eighteen = new StringBuffer(id);
        eighteen = eighteen.insert(6, "19");
        return eighteen.toString();
    }

    private static String getVerify(String id) {
        int remain = 0;
        if (id.length() == 18) {
            id = id.substring(0, 17);
        }
        if (id.length() == 17) {
            int sum = 0;
            for (int i = 0; i < 17; i++) {
                String k = id.substring(i, i + 1);
                AI[i] = Integer.valueOf(k);
            }
            for (int i = 0; i < 17; i++) {
                sum += WI[i] * AI[i];
            }
            remain = sum % 11;
        }
        return remain == 2 ? "X" : String.valueOf(VI[remain]);
    }

    /**
     * @since 1.0
     * @param obj 目标对象
     * @return 目标对象的字符串形式
     */
    public static String toString(Object obj) {
        String result = NULL;
        if (obj != null) {
            result = obj.toString();
        }
        return result;
    }

    /**
     * @since 1.0
     * @param val 目标字符串
     * @return 目标字符串首字母大写新字符串
     */
    public static String standInitial(String val) {
        String result = val;
        if (!isEmpty(val)) {
            char initial = val.charAt(0);
            if (Character.isLetter(initial) && !Character.isUpperCase(initial)) {
                result = Character.toUpperCase(initial) + val.substring(1);
            }
        }
        return result;
    }

    /**
     * @since 1.0
     * @param money 人民币值
     * @return 带有人民币符号的字符串
     */
    public static String revalueInitial(String money) {
        return RMB + " " + money;
    }

    /**
     * @since 1.0
     * @param val 目标字符串
     * @param format 转码格式
     * @return 转码后的字符串
     */
    public static String encode(String val, String format) {
        if (!isEmpty(val) && val.getBytes().length != val.length()) {
            try {
                val = URLEncoder.encode(val, format);
            } catch (UnsupportedEncodingException e) {
                val = NULL;
            }
        }
        return val;
    }

    /**
     * @since 1.0
     * @param binary 二进制数组
     * @return base64字符串
     */
    public static String toString(byte[] binary) {
        String result;
        try {
            result = Base64.encodeToString(binary, Base64.DEFAULT);
        } catch (AssertionError e) {
            result = NULL;
        }
        return result;
    }

    /**
     * @since 1.0
     * @param val 目标字符串
     * @return 半角字符串
     */
    public static String toHalf(String val) {
        if (!isEmpty(val)) {
            char[] source = val.toCharArray();
            for (int i = 0; i < source.length; i++) {
                if (source[i] == 12288) {
                    source[i] = ' ';
                    continue;
                }
                if (source[i] >= 65281 && source[i] <= 65374) {
                    source[i] = (char)(source[i] - 65248);
                    continue;
                }
                source[i] = source[i];
            }
            val = new String(source);
        }
        return val;
    }

    /**
     * @since 1.0
     * @param val 目标字符串
     * @return 全角字符串
     */
    public static String toFull(String val) {
        if (!isEmpty(val)) {
            char[] source = val.toCharArray();
            for (int i = 0; i < source.length; i++) {
                if (source[i] == ' ') {
                    source[i] = (char) 12288;
                    continue;
                }
                if (source[i] >= 33 && source[i] <= 126) {
                    source[i] = (char) (source[i] + 65248);
                    continue;
                }
                source[i] = source[i];
            }
            val = new String(source);
        }
        return val;
    }

    public static SpannableString getString(String msg, int size,
                        Resources res, String format, String pgk) {
        SpannableString spannableString = new SpannableString(msg);
        Pattern pattern = Pattern.compile(format, Pattern.CASE_INSENSITIVE);
        try {
            dealExpression(spannableString, pattern, 0, size, res, pgk);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return spannableString;
    }

    private static void dealExpression(SpannableString ss, Pattern pattern,
                   int index, int size, Resources res, String pgk) {
        Matcher matcher = pattern.matcher(ss);
        while (matcher.find()) {
            String key = matcher.group();
            if (matcher.start() < index) {
                continue;
            }
            key = key.substring(1, key.length() - 1);
            int resid = res.getIdentifier(key, "drawable", pgk);
            if (resid > 0) {
                Drawable drawable = res.getDrawable(resid);
                drawable.setBounds(0, 0, size, size);
                ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
                ss.setSpan(span, matcher.start(), matcher.start()
                        + key.length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }
}
