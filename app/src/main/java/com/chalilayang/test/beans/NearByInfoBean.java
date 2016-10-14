package com.chalilayang.test.beans;

import java.util.List;

/**
 * Created by chalilayang on 2016/10/10.
 */

public class NearByInfoBean {


    /**
     * province : 江苏省
     * code : 1
     * tel : 025
     * cityadcode : 320100
     * areacode : 025
     * timestamp : 1476094619.99
     * pos : 在紫云楼附近, 靠近陵西路--陵前路路口
     * result : true
     * message : Successful.
     * desc : 江苏省,南京市,玄武区
     * city : 南京市
     * districtadcode : 320102
     * district : 玄武区
     * country : 中国
     * provinceadcode : 320000
     * version : 2.0-3.0.6270.1443
     * adcode : 320102
     */

    private String province;
    private String code;
    private String tel;
    private String cityadcode;
    private String areacode;
    private String timestamp;
    private String pos;
    private List<RoadBean> road_list;
    private String result;
    private String message;
    private String desc;
    private String city;
    private String districtadcode;
    private String district;
    private String country;
    private String provinceadcode;
    private String version;
    private String adcode;
    private List<PosBean> poi_list;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getCityadcode() {
        return cityadcode;
    }

    public void setCityadcode(String cityadcode) {
        this.cityadcode = cityadcode;
    }

    public String getAreacode() {
        return areacode;
    }

    public void setAreacode(String areacode) {
        this.areacode = areacode;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public List<RoadBean> getRoad_list() {
        return road_list;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrictadcode() {
        return districtadcode;
    }

    public void setDistrictadcode(String districtadcode) {
        this.districtadcode = districtadcode;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvinceadcode() {
        return provinceadcode;
    }

    public void setProvinceadcode(String provinceadcode) {
        this.provinceadcode = provinceadcode;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    public List<PosBean> getPoi_list() {
        return poi_list;
    }

    public String getStringInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("国家：").append(this.country).append("\n");
        sb.append("省份：").append(this.province).append("\n");
        sb.append("城市：").append(this.city).append("\n");
        sb.append("地区：").append(this.district).append("\n");
        sb.append("大概位置描述：").append(this.pos).append("\n");
        if (poi_list != null && poi_list.size() > 0) {
            sb.append("附近：\n");
            int size = poi_list.size();
            int index = 0;
            for (PosBean poss:
                    poi_list) {
                index ++;
                sb.append(poss.getStringInfo());
                if (index < size) {
                    sb.append("\n");
                } else {
                    sb.append("\n");
                }
            }
        }
        return sb.toString();
    }

}
