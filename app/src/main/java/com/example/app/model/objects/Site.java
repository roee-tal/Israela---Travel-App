package com.example.app.model.objects;

import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;


public class Site
{
    private String id;
    private String name;
    private double rate;
    private int mainRateReviewNum;
    private double shadeRate;
    private int shadeRateReviewNum;
    private String detail;
    private Location location;
    private GroupEvent event;
    private Category category;
    private HashMap<String, Review> reviews;
    public Site(){};


    public Site(String id, String name, double mainRate, String detail, Location location, double shadeRate, int shadeRateReviewNum, int mainRateReviewNum, GroupEvent event, Category category) {
        this.id = id;
        this.name = name;
        this.rate = mainRate;
        this.detail = detail;
        this.location = location;
        this.shadeRate = shadeRate;
        this.shadeRateReviewNum = 1;
        this.mainRateReviewNum = 1;
        this.event = event;
        this.category = category;
        this.reviews = new HashMap<>();
    }

    /**
     * the calculate to update rate and update it
     * @param rate the new rate
     */
    public void updateRate(double rate) {
        Log.d("Site", "mainRate="+ this.rate + " mainRateReviewNum="+mainRateReviewNum + " rate="+rate);
        this.rate = ((this.rate * mainRateReviewNum + rate) / (mainRateReviewNum + 1.0));
        this.rate = Math.round(this.rate * 10) / 10.0;
        Log.d("Site", "mainRate="+ this.rate + " mainRateReviewNum="+mainRateReviewNum + " rate="+rate);
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

    public double getRate() {
        return rate;
    }

    public int getMainRateReviewNum() {
        return mainRateReviewNum;
    }

    public GroupEvent getEvent() {
        return event;
    }

    public void setEvent(GroupEvent event) {
        this.event = event;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public HashMap<String, Review> getReviews() {

        return reviews;
    }

    public ArrayList<String> getTags() {
        ArrayList<String> tags = new ArrayList<String>();
        tags.add(this.getCategory().name());
        tags.add(this.getLocation().name());
        if (this.getEvent() != null)
            tags.add("Has Event");
        return tags;
    }
}
