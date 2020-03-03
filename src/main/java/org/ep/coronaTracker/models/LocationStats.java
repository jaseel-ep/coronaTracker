package org.ep.coronaTracker.models;

public class LocationStats {
    private String province;
    private String country;
    private String latestConfirmedStat;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLatestConfirmedStat() {
        return latestConfirmedStat;
    }

    public void setLatestConfirmedStat(String latestConfirmedStat) {
        this.latestConfirmedStat = latestConfirmedStat;
    }

    @Override
    public String toString() {
        return "LocationStats{" +
                "province='" + province + '\'' +
                ", country='" + country + '\'' +
                ", latestConfirmedStat='" + latestConfirmedStat + '\'' +
                '}';
    }
}
