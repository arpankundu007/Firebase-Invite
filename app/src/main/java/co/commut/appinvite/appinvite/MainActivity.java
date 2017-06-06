package co.commut.appinvite.appinvite;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;

public class MainActivity extends AppCompatActivity {

    private static final String REFER_CODE = "ABCXYZ123", DRIVER_NAME = "TestDriver";
    private static final
    String INVITATION_TITLE = "Invite partners",
            INVITATION_MESSAGE = "Please install Commut Partner app for added benefits. Refer Code: " + REFER_CODE,
            INVITATION_CALL_TO_ACTION = "Share";
    ProgressDialog pd;
    Button shareReferral;
    TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        shareReferral = (Button) findViewById(R.id.invite_friends);
        textView = (TextView) findViewById(R.id.textView);
        textView.setVisibility(View.GONE);
        pd = new ProgressDialog(this);
        if (pd.isShowing())
            pd.dismiss();
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        shareReferral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogBuilder.setTitle("Share Via");
                alertDialogBuilder.setMessage("Share your referral code");
                alertDialogBuilder.setPositiveButton("Share via SMS/Email", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shareviaFirebase();
                    }
                });
                alertDialogBuilder.setNegativeButton("Share via other", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shareViaWhatsApp();
                    }
                });
                alertDialogBuilder.show();
            }
        });
    }

    private void shareViaWhatsApp() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, "https://code.coupon/" + REFER_CODE + "/" + DRIVER_NAME);
        startActivity(Intent.createChooser(share, "Share using"));
    }

    private void shareviaFirebase() {

        pd.setTitle("Loading contacts");
        pd.setMessage("Please wait ...");
        pd.show();
        Intent intent = new AppInviteInvitation.IntentBuilder(INVITATION_TITLE)
                .setMessage(INVITATION_MESSAGE)
                .setDeepLink(Uri.parse("commut://code.coupon/" + REFER_CODE + "/" + DRIVER_NAME))
                .setCallToActionText(INVITATION_CALL_TO_ACTION)
                .build();
        startActivityForResult(intent, 0);


        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(AppInvite.API)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.d(getString(R.string.app_name), "onConnectionFailed:" + connectionResult);
                        //showMessage("Sorry, the connection has failed.");
                    }
                })
                .build();
        AppInvite.AppInviteApi.getInvitation(googleApiClient, this, true)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(AppInviteInvitationResult result) {
                            }
                        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                pd.dismiss();
                // You successfully sent the invite,
                // we can dismiss the button.
                shareReferral.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
                textView.setText("App referred successfully");
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                StringBuilder sb = new StringBuilder();
                sb.append("Sent ").append(Integer.toString(ids.length)).append(" invitations: ");
                for (String id : ids) sb.append("[").append(id).append("]");
                Log.d(getString(R.string.app_name), sb.toString());

            } else {
                // Sending failed or it was canceled using the back button
                // showMessage("Sorry, I wasn't able to send the invites");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(pd.isShowing())
            pd.dismiss();
    }
}
