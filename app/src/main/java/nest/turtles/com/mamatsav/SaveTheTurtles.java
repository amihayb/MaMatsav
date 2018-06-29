package nest.turtles.com.mamatsav;

import android.app.Notification;
import android.app.NotificationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SaveTheTurtles extends AppCompatActivity {

    static String preState = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_the_turtles);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("nest1");

        // Init values
        myRef.child("dismissed").setValue("0");
        myRef.child("emerged").setValue("0");
        myRef.child("hatchState").setValue("1");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String emerged = dataSnapshot.child("emerged").getValue(String.class);
                String dismissed = dataSnapshot.child("dismissed").getValue(String.class);
                //((TextView)findViewById(R.id.TextView_textHello)).setText(value);
                Log.i("amihay", "emerged is: " + emerged);

                if (emerged.equals("1") && dismissed.equals("0")){
                    ((ImageView)findViewById(R.id.imageView_hatch)).setVisibility(View.VISIBLE);
                    //Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    //Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    //r.play();
                    //Define Notification Manager
                    notifyMe();
                }
                else{
                    ((ImageView)findViewById(R.id.imageView_hatch)).setVisibility(View.INVISIBLE);
                }

                String hatchState = dataSnapshot.child("hatchState").getValue(String.class);

                //if (!hatchState.equals(preState)) {
                    //Log.i("amihay", "hatch state" + preState);
                    //preState = hatchState;
                    if ( hatchState.equals("1") )
                        ((ImageView) findViewById(R.id.imageView_status)).setImageResource(R.drawable.screen1);
                    else if ( hatchState.equals("2") )
                        ((ImageView) findViewById(R.id.imageView_status)).setImageResource(R.drawable.screen2);
                    else if ( hatchState.equals("3") )
                        ((ImageView) findViewById(R.id.imageView_status)).setImageResource(R.drawable.screen3);
                    else if ( hatchState.equals("4") )
                        ((ImageView) findViewById(R.id.imageView_status)).setImageResource(R.drawable.screen4);
                    else if ( hatchState.equals("5") )
                        ((ImageView) findViewById(R.id.imageView_status)).setImageResource(R.drawable.screen5);

                    /*switch (hatchState) {
                        case "1":
                            ((ImageView) findViewById(R.id.imageView_status)).setImageResource(R.drawable.screen1);
                            break;

                        case "2":
                            ((ImageView) findViewById(R.id.imageView_status)).setImageResource(R.drawable.screen2);
                            break;

                        case "3":
                            ((ImageView) findViewById(R.id.imageView_status)).setImageResource(R.drawable.screen3);
                            break;

                        case "4":
                            ((ImageView) findViewById(R.id.imageView_status)).setImageResource(R.drawable.screen4);
                            break;

                        case "5":
                            ((ImageView) findViewById(R.id.imageView_status)).setImageResource(R.drawable.screen5);
                            break;
                    }*/
                    //Log.i("amihay", "hatch state changed");
                }


            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        final ImageView imgClick = (ImageView)findViewById(R.id.imageView_hatch);

        imgClick.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                myRef.child("dismissed").setValue("1");
                Log.i("amihay", "dismissed clicked");
                //imgClick.setVisibility(View.INVISIBLE);

            }
        });
    };

    /*public void sendNotification() {

        //Get an instance of NotificationManager//

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");


        // Gets an instance of the NotificationManager service//

        NotificationManager mNotificationManager =

                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // When you issue multiple notifications about the same type of event,
        // it’s best practice for your app to try to update an existing notification
        // with this new information, rather than immediately creating a new notification.
        // If you want to update this notification at a later date, you need to assign it an ID.
        // You can then use this ID whenever you issue a subsequent notification.
        // If the previous notification is still visible, the system will update this existing notification,
        // rather than create a new one. In this example, the notification’s ID is 001//

        NotificationManager.notify().mNotificationManager.notify(001, mBuilder.build());
    }*/

    public void showNotification() {

        Log.i("amihay", "inFunc");
        Notification notification = new NotificationCompat.Builder(this)
                .setTicker("New One!")
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle("New One!")
                .setContentText("wow!")
                .setAutoCancel(true)
                .build();
        Log.i("amihay", "2nd");
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Log.i("amihay", "3rd");
        notificationManager.notify(0, notification);
        Log.i("amihay", "done");
    }

    private void notifyMe() {

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle("wow")
                .setContentText("finaly")
                .setSound(alarmSound)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, mBuilder.build());

    }
}
