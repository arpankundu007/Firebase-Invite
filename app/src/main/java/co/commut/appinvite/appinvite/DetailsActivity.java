package co.commut.appinvite.appinvite;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteReferral;

/**
 * Created by WarMach on 6/5/2017.
 */

public class DetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if(getIntent()!=null){
            String data = getIntent().getDataString();
            Log.d("Data: ", data);
            String[] array = data.split("/");

            if (array.length > 0) {
                TextView tv = (TextView) findViewById(R.id.discount);
                tv.setText("You have been successfully referred by " + array[array.length-1] +  " using referral code " + array[array.length-2]);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
//        if(intent!=null){
//            Log.d("Intent data: ",  getIntent().toString());
//        }
        if (AppInviteReferral.hasReferral(intent)) {
            // Extract the information from the Intent
            String deepLink = AppInviteReferral.getDeepLink(intent);
            Log.d(getString(R.string.app_name),
                    "Found Referral: " + AppInviteReferral.getInvitationId(intent) + ":" + deepLink);

            String[] array = deepLink.split("/");

            if (array.length > 0) {
                TextView tv = (TextView) findViewById(R.id.discount);
                tv.setText("You have been successfully referred by " + array[array.length-1] +  " using referral code " + array[array.length-2]);
            }
        }
    }
}
