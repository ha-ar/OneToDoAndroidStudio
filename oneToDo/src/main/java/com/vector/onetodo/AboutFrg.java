package com.vector.onetodo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.mikhaellopez.circularimageview.CircularImageView;

/**
 * Created by Fuji on 18/05/2015.
 */
public class AboutFrg extends Fragment{

    private AQuery aq;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_frag, container, false);

        aq = new AQuery(getActivity(), view);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_top);
        if (toolbar != null)
            ((ActionBarActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("About");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
//        setFont();
        setHasOptionsMenu(true);
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.plain, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().getSupportFragmentManager().popBackStack();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        aq.id(R.id.buttonFB).clicked(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String facebookUrl = "https://www.facebook.com/JRummyApps";
                try {
                    int versionCode = getActivity().getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
                    if (versionCode >= 3002850) {
                        Uri uri = Uri.parse("fb://facewebmodal/f?href=" + facebookUrl);
                        startActivity(new Intent(Intent.ACTION_VIEW, uri));;
                    } else {
                        // open the Facebook app using the old method (fb://profile/id or fb://page/id)
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/336227679757310")));
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    // Facebook is not installed. Open the browser
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl)));
                }
            }
        });

        aq.id(R.id.buttonTwitter).clicked(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String twitterID = "498418164";
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("twitter://user?user_id="+twitterID));
                    startActivity(intent);

                }catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://twitter.com/#!/"+twitterID)));
                }
            }
        });
        aq.id(R.id.buttonEmail).clicked(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(intent.EXTRA_EMAIL, "hello@onesout.com");
                Intent mailer = Intent.createChooser(intent, null);
                startActivity(mailer);
            }
        });
        aq.id(R.id.buttonSupport).clicked(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String myDeviceModel = android.os.Build.MODEL;
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(intent.EXTRA_EMAIL, "support@onesout.com");
                intent.putExtra(intent.EXTRA_SUBJECT, "Need Support");
                intent.putExtra(intent.EXTRA_TEXT, myDeviceModel);
                Intent mailer = Intent.createChooser(intent, null);
                startActivity(mailer);
            }
        });
    }
}
