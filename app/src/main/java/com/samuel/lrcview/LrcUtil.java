package com.samuel.lrcview;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sunyao
 * @Description:
 * @date 2018/3/5 上午10:51
 */
public class LrcUtil {

    public static int parseTime(String lrcText) {
        Pattern pattern = Pattern.compile("^\\[([0-9]{2,}):([0-5][0-9]\\.[0-9]{2})](.*)");
        Matcher matcher = pattern.matcher(lrcText);
        if (matcher.find()) {
            int cont = matcher.groupCount();
            String[] s = new String[3];
            for (int i = 1; i <= cont; i++) {
                String matcherStr = matcher.group(i);
                s[i - 1] = matcherStr;

            }
            int millis = (int) (Integer.parseInt(s[0]) * 60 * 1000
                    + Float.parseFloat(s[1]) * 1000);
//            String text = s[2];

            return millis;
        }

        return -1;

    }

    public static String parseContent(String lrcText) {
        Pattern pattern = Pattern.compile("^\\[([0-9]{2,}):([0-5][0-9]\\.[0-9]{2})](.*)");

        Matcher matcher = pattern.matcher(lrcText);
        if (matcher.find()) {
            int cont = matcher.groupCount();
            String[] s = new String[3];
            for (int i = 1; i <= cont; i++) {
                String matcherStr = matcher.group(i);
                s[i - 1] = matcherStr;

            }

//            int millis = (int) (Integer.parseInt(s[0]) * 60 * 1000 + Float.parseFloat(s[1]) * 1000);

            String text = s[2];

            return text;
        }

        return "";

    }

}
