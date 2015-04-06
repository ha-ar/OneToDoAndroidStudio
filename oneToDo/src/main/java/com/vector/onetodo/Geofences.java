package com.vector.onetodo;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.reactivelocation.ReactiveLocationProvider;
import com.reactivelocation.observables.geofence.AddGeofenceResult;
import com.reactivelocation.observables.geofence.RemoveGeofencesResult;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;


public class Geofences{
    private static final String TAG = "Geofence";

    private ReactiveLocationProvider reactiveLocationProvider;
    private Context mContext;

    Geofences(Context context) {
        reactiveLocationProvider = new ReactiveLocationProvider(context);
        mContext = context;
    }

    public void clearGeofence() {
        reactiveLocationProvider.removeGeofences(createNotificationBroadcastPendingIntent()).subscribe(new Action1<RemoveGeofencesResult.PendingIntentRemoveGeofenceResult>() {
            @Override
            public void call(RemoveGeofencesResult.PendingIntentRemoveGeofenceResult pendingIntentRemoveGeofenceResult) {
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.d(TAG, "Error removing geofences", throwable);
            }
        });
    }


    private PendingIntent createNotificationBroadcastPendingIntent() {
        return PendingIntent.getBroadcast(mContext, 0, new Intent(mContext, GeofenceBroadcastReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void addGeofence(double lat, double lang, int radius, int enterOrExit, long expiry) {
        final GeofencingRequest geofencingRequest = createGeofencingRequest(lat, lang, radius, enterOrExit, expiry);
        if (geofencingRequest == null) return;

        final PendingIntent pendingIntent = createNotificationBroadcastPendingIntent();
        reactiveLocationProvider
                .removeGeofences(pendingIntent)
                .flatMap(new Func1<RemoveGeofencesResult.PendingIntentRemoveGeofenceResult, Observable<AddGeofenceResult>>() {
                    @Override
                    public Observable<AddGeofenceResult> call(RemoveGeofencesResult.PendingIntentRemoveGeofenceResult pendingIntentRemoveGeofenceResult) {
                        return reactiveLocationProvider.addGeofences(pendingIntent, geofencingRequest);
                    }
                })
                .subscribe(new Action1<AddGeofenceResult>() {
                    @Override
                    public void call(AddGeofenceResult addGeofenceResult) {
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.d(TAG, "Error adding geofence.", throwable);
                    }
                });
    }

    public GeofencingRequest createGeofencingRequest(double lat, double lon, int radius, int enterOrExit, long expiry ) {
        try {
            Geofence geofence = new Geofence.Builder()
                    .setRequestId("GEOFENCE")
                    .setCircularRegion(lat, lon, radius)
                    .setExpirationDuration(expiry)
                    .setTransitionTypes(enterOrExit)
                    .build();
            return new GeofencingRequest.Builder().addGeofence(geofence).build();
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}