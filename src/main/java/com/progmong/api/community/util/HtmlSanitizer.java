package com.progmong.api.community.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class HtmlSanitizer {
    public static String sanitize(String html) {
        if (html == null) {
            return null;
        }
        // 기본적인 텍스트 포맷용 태그들만 허용
        return Jsoup.clean(html, Safelist.basic());
    }
}
