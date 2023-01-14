package com.example.app.modelView;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.app.View.DetailActivity;
import com.example.app.model.DetailSiteModel;
import com.example.app.model.objects.Site;
import com.example.app.modelView.adapters.ViewPagerAdapter;
import com.example.app.modelView.helpClassesMV.ShowToastAndSignOut;

import java.util.ArrayList;

public class DetailSiteMV {

    DetailActivity detailActivity;
    DetailSiteModel detailSiteModel;
    ShowToastAndSignOut showToastAndSignOut;

    ViewPagerAdapter viewPagerAdapter;


    public DetailSiteMV(DetailActivity detailActivity, ViewPagerAdapter mViewPagerAdapter){
        this.detailActivity = detailActivity;
        detailSiteModel = new DetailSiteModel(this);
        showToastAndSignOut = new ShowToastAndSignOut();
        this.viewPagerAdapter = mViewPagerAdapter;
    }


    public void getParsedShapee(String parsedID)
    {

        detailSiteModel.getParsedShape(parsedID);
    }

    public void showToast(String message){
        showToastAndSignOut.showToast(detailActivity,message);
    }

    // Sent Site to the view to set the values
    public void setVal(Site s) {
        detailActivity.initVal(s);
    }

    // Set vals of the site
    public void setVals(Site s, TextView siteName, TextView siteDetail, LinearLayout tagContainer) {
        ArrayList<String> tagList = s.getTags();
        int childCount = tagContainer.getChildCount(), childNum = 0;
        // Move over the layout to find the rellevant values and them through the view(activity)
        for (int i = 0; i < childCount; i++) {
            View child = tagContainer.getChildAt(i);
            if (child instanceof TextView) {
                TextView textView = (TextView) child;
                // Design the data
                detailActivity.setText(textView, tagContainer,  tagList.get(childNum), false);
                childNum++;
            }
        }
        for (int i = childNum; i<tagList.size(); i++) {
            TextView tagData = new TextView(detailActivity);
            detailActivity.setText(tagData, tagContainer, tagList.get(i), true);
        }
        //Show the strings
        detailActivity.setVal(siteName,siteDetail,s);

    }


    public void updateImageDetailList(String shapeID, ArrayList<Bitmap> images, ViewPagerAdapter mViewPagerAdapter) {
        detailSiteModel.updateImageDetailList(shapeID,images,mViewPagerAdapter);

    }

    public void bSubmit(RatingBar mainRateBar, AlertDialog dialog, String selectedShapeID) {
        detailSiteModel.bSubmit(mainRateBar, dialog, selectedShapeID);
    }

    public void LoadEvent(String shapeID) {
        detailSiteModel.LoadEvent(shapeID);
    }

    public void updateTripDetails(Site s) {
        detailActivity.updateTripDetails(s);
    }

    public void whenJoinGroup(String selectedShapeID) {
        detailSiteModel.whenJoinGroup(selectedShapeID);
    }

    public void welcomeMessage() {
        detailActivity.welcomeMessage();
    }

    public void addReview(String name, String review, AlertDialog dialog) {
        detailSiteModel.addReview(name,review,dialog);
    }

    public void showReviews(ArrayAdapter<String> adapterReview, ArrayList<String> arrayListReviews) {
        detailSiteModel.showReview(adapterReview,arrayListReviews);
    }
}
