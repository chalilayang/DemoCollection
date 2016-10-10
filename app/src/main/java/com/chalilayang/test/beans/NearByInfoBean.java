package com.chalilayang.test.beans;

/**
 * Created by chalilayang on 2016/10/10.
 */

public class NearByInfoBean {
    /*
        province: "天津市",
        cross_list: + [... ],
        code: "1",
        tel: "022",
        cityadcode: "120000",
        areacode: "022",
        timestamp: "1476001413.63",
        sea_area: + {... },
        pos: "在东亚银行(天津滨海支行)附近, 在第一大街旁边, 靠近新城东路--第一大街路口",
        road_list: + [... ],
        result: "true",
        message: "Successful.",
        desc: "天津市,天津市,滨海新区",
        city: "天津市",
        districtadcode: "120116",
        district: "滨海新区",
        country: "中国",
        provinceadcode: "120000",
        version: "2.0-3.0.6270.1443",
        adcode: "120116",
        poi_list: + [... ]
         */
    private String province;
    private String code;
    private String tel;
    private String cityadcode;
    private String areacode;
    private String pos;

    public String getProvince() {
        return province;
    }

    public String getCode() {
        return code;
    }

    public String getTel() {
        return tel;
    }

    public String getCityadcode() {
        return cityadcode;
    }

    public String getAreacode() {
        return areacode;
    }

    public String getPos() {
        return pos;
    }

}
