package com.perimeterx.models.activities;

import com.perimeterx.models.PXContext;
import com.perimeterx.utils.Constants;

/**
 * Created by shikloshi on 07/11/2016.
 */
public class ActivityFactory {

    public static Activity createActivity(final String activityType, final String appId, final PXContext ctx) {
        ActivityDetails details;
        switch (activityType) {
            case Constants.ACTIVITY_BLOCKED:
                details = new BlockActivityDetails(ctx);
                break;
            case Constants.ACTIVITY_PAGE_REQUESTED:
                details = new PageRequestedActivityDetails(ctx);
                break;
            default:
                throw new IllegalArgumentException("No such activity: " + activityType);
        }
        return new Activity(activityType, appId, ctx, details);
    }

}
