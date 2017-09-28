package com.ipredictfantasy.utility;

import com.ipredictfantasy.dto.MatchesModel;

import java.util.ArrayList;

/**
 * Created by anbu0 on 26/05/2016.
 */
public class GlobalActivity {
    public static ArrayList<MatchesModel> marcharraylist=new ArrayList<>();
    public static String defaultimage="http://ipredictfantasy.com/public/uploads/users/default_profile.jpg";
    public static int delaytime=500;
    public static String GameidFinder(int position){
        String gameid = "1";
        switch (position){
            case 0:
                gameid="1";
                break;
            case 1:
                gameid="2";
                break;
        }
        return gameid;
    }


}
