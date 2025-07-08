package com.progmong.api.community.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class HtmlSanitizer {
    public static String sanitize(String html) {
        if (html == null) {
            return null;
        }
//        // 기본적인 텍스트 포맷용 태그들만 허용
//        return Jsoup.clean(html, Safelist.basic());

        return Jsoup.clean(html, Safelist.relaxed()
            .addTags("figure") // 추가로 figure 허용
            .addAttributes("figure", "class", "style") // figure의 스타일과 클래스 허용
            .addAttributes("img", "style")); // img에 style 허용
    }
}
