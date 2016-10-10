package com.chalilayang.test.constants;

/**
 * Created by chalilayang on 2016/10/10.
 */

public class URLConstants {

    private static final String HTTP = "http://";
    private static final String URL_base = "ditu.amap.com/service";
    public static String getURL_NearByInfo(String latitude, String longitude) {
        StringBuffer url = new StringBuffer(HTTP);
        url.append(URL_base).append(
                String.format("/regeo?longitude=%s&latitude=%s", longitude, latitude)
        );
        return url.toString();
    }
}
