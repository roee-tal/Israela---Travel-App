package com.example.app.modelView;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.app.View.AdminDetailActivity;
import com.example.app.model.AdminDetailsiteModel;
import com.example.app.model.objects.Site;
import com.example.app.modelView.adapters.ViewPagerAdapter;
import com.example.app.modelView.helpClassesMV.ShowToastAndSignOut;

import java.util.ArrayList;

public class AdminDetailsiteMV {


    AdminDetailActivity adminDetailActivity;
    AdminDetailsiteModel adminDetailSiteModel;
    ShowToastAndSignOut showToastAndSignOut;

    ViewPagerAdapter viewPagerAdapter;


    public AdminDetailsiteMV(AdminDetailActivity adminDetailActivity, ViewPagerAdapter mViewPagerAdapter){
        this.adminDetailActivity = adminDetailActivity;
        adminDetailSiteModel = new AdminDetailsiteModel(this);
        showToastAndSignOut = new ShowToastAndSignOut();
        this.viewPagerAdapter = mViewPagerAdapter;
    }

    public void getParsedShapee(String parsedID) {
        adminDetailSiteModel.getParsedShape(parsedID);
    }

    public void updateImageDetailList(String parsedID, ArrayList<Bitmap> images, ViewPagerAdapter mViewPagerAdapter) {
        adminDetailSiteModel.updateImageDetailList(parsedID,images,mViewPagerAdapter);

    }

        public void showToast(String message){
            showToastAndSignOut.showToast(adminDetailActivity,message);
        }

    // Sent Site to the view to set the values
    public void setVal(Site s) {
        adminDetailActivity.initVal(s);
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
                adminDetailActivity.setText(textView, tagContainer,  tagList.get(childNum), false);
                childNum++;
            }
        }
        for (int i = childNum; i<tagList.size(); i++) {
            TextView tagData = new TextView(adminDetailActivity);
            adminDetailActivity.setText(tagData, tagContainer, tagList.get(i), true);
        }
        //Show the strings
        adminDetailActivity.setVal(siteName,siteDetail,s);
    }

    public void bUpdateName(String selectedShapeID, EditText input) {
        adminDetailSiteModel.bUpdateName(selectedShapeID,input);
    }

    public void bUpdateRate(String selectedShapeID, RatingBar generalRate) {
        adminDetailSiteModel.bUpdateRate(selectedShapeID,generalRate);
    }


    public void bUpdateLocation(String selectedShapeID, Spinner spinner) {
        adminDetailSiteModel.bUpdateLocation(selectedShapeID,spinner);
    }

    public void bUpdateDetails(String selectedShapeID, EditText input) {
        adminDetailSiteModel.bUpdateDetails(selectedShapeID,input);
    }

    public void bAddEvent(String selectedShapeID, EditText inputDate, EditText inputDetail) {
        adminDetailSiteModel.bAddEvent(selectedShapeID,inputDate,inputDetail);
    }
}
