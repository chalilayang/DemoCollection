package com.chalilayang.test.beans;

/**
 * Created by chalilayang on 2016/10/10.
 */

public class PosBean {

    /**
     * distance : 158
     * direction : South
     * tel :
     * name : 紫云楼
     * weight : 0.0
     * typecode : 100102
     * longitude : 118.848189
     * address : 中山陵5号
     * latitude : 32.061787
     * type : 住宿服务;宾馆酒店;五星级宾馆
     * poiid : B0FFGWU9VN
     */

    private String distance;
    private String direction;
    private String tel;
    private String name;
    private String weight;
    private String typecode;
    private String longitude;
    private String address;
    private String latitude;
    private String type;
    private String poiid;

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getTypecode() {
        return typecode;
    }

    public void setTypecode(String typecode) {
        this.typecode = typecode;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPoiid() {
        return poiid;
    }

    public void setPoiid(String poiid) {
        this.poiid = poiid;
    }

    public String getStringInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.name).append("（");
        sb.append(this.address).append("、");
        sb.append(this.type).append("）");
        return sb.toString();
    }
}
