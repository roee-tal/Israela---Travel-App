package com.example.app;

import android.util.Log;

import java.util.Comparator;


class Site
{
    private String id;
    private String name;
    private String image;
    private double mainRate;
    private int mainRateReviewNum;
    private double shadeRate;
    private int shadeRateReviewNum;
    private String detail;
    private Location location;
    public Site(){};

    public double getRate() {
        return mainRate;
    }

    public int getMainRateReviewNum() {
        return mainRateReviewNum;
    }

    public double getShadeRate() {
        return shadeRate;
    }

    public int getShadeRateReviewNum() {
        return shadeRateReviewNum;
    }

    public Site(String id, String name, String image, double mainRate, String detail, Location location, double shadeRate, int shadeRateReviewNum, int mainRateReviewNum) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.detail = detail;
        this.location = location;
        this.shadeRate = shadeRate;
        this.shadeRateReviewNum = 1;
        this.mainRate = mainRate;
        this.mainRateReviewNum = 1;
    }

    public void updateRate(double rate) {
        this.mainRate = ((mainRate * mainRateReviewNum + rate) / (mainRateReviewNum + 1.0)); //Todo:something wrong with this calculation (every time decraese number)
//        mainRateReviewNum++;
        Log.d("Site", "mainRate="+mainRate + " mainRateReviewNum="+mainRateReviewNum + " rate="+rate);
    }

    public void updateShadeRate(double rate) {
        this.mainRate = (shadeRate * shadeRateReviewNum + rate) / (++shadeRateReviewNum);
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public static Comparator<Site> rateSort = new Comparator<Site>()
    {
        @Override
        public int compare(Site shape1, Site shape2)
        {
            double id1 = Double.valueOf(shape1.getRate());
            double id2 = Double.valueOf(shape2.getRate());

            return Double.compare(id1, id2);
        }
    };

    public static Comparator<Site> nameAscending = new Comparator<Site>()
    {
        @Override
        public int compare(Site shape1, Site shape2)
        {
            String name1 = shape1.getName();
            String name2 = shape2.getName();
            name1 = name1.toLowerCase();
            name2 = name2.toLowerCase();

            return name1.compareTo(name2);
        }
    };


}
