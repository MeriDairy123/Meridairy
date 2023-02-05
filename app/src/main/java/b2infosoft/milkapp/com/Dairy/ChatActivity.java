package b2infosoft.milkapp.com.Dairy;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by Microsoft on 07-Sep-17.
 */

public class ChatActivity extends AppCompatActivity {

    private final static int INTERVAL = 1000 * 5; //1 sec
    String friendname = "", friend_id = "", imgPath = "", mobileNumber = "", firebase_tocan = "",
            unic_customer_for_mobile = "";
    TextView toolbar_title, tvStatus;
    Context mContext;
    Toolbar toolbar;
    SessionManager sessionManager;

    LinearLayout layout;
    RelativeLayout layout_2;
    LinearLayout sendButton;
    EditText ediMessageArea;
    Firebase reference1, reference2;
    ScrollView scrollView;
    FirebaseDatabase database;
    DatabaseReference myRef;
    OkHttpClient mClient = new OkHttpClient();

    CircleImageView userImg;
    String userGroupId = "", userMobie = "", password = "", chatWith = "", Token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activuty_chat);
        mContext = ChatActivity.this;
        sessionManager = new SessionManager(mContext);
        userGroupId = sessionManager.getValueSesion(SessionManager.Key_UserGroupID);
        Constant.OnChat = "Yes";
        Constant.ChatContext = mContext;

        toolbar = findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(getString(R.string.CHAT_LIST));
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ediMessageArea = findViewById(R.id.ediMessageArea);
        friendname = getIntent().getStringExtra("FRIEND_NAME");
        unic_customer_for_mobile = getIntent().getStringExtra("unic_customer_for_mobile");
        friend_id = getIntent().getStringExtra("FRIEND_id");
        // imgPath = getIntent().getStringExtra("IMAGE_PATH");
        mobileNumber = getIntent().getStringExtra("FRIEND_mob");
        firebase_tocan = getIntent().getStringExtra("firebase_tocan");
        System.out.println("==userGroupId=====" + userGroupId);
        if (userGroupId.equals("2")) {
            chatWith = unic_customer_for_mobile;
            userMobie = sessionManager.getValueSesion(SessionManager.KEY_Mobile);
        } else {
            chatWith = mobileNumber;
            userMobie = sessionManager.getValueSesion(SessionManager.Key_unic_customer_for_mobile);
        }

        Token = firebase_tocan;
        System.out.println("==friendname=====" + friendname);
        System.out.println("==unic_customer_for_mobile=====" + unic_customer_for_mobile);
        System.out.println("==friend_id=====" + friend_id);

        System.out.println("==chatWith=====" + chatWith);
        System.out.println("==firebase_tocan=====" + firebase_tocan);
        System.out.println("==userMobie=====" + userMobie);
        Firebase.setAndroidContext(this);
        FirebaseApp.initializeApp(ChatActivity.this);
        database = FirebaseDatabase.getInstance();
        toolbar_title.setText(friendname);

        sendButton = findViewById(R.id.sendButton);
        layout = findViewById(R.id.layout1);
        layout_2 = findViewById(R.id.layout2);
        scrollView = findViewById(R.id.scrollView);

        myRef = database.getReference("messages");
        reference1 = new Firebase("https://meridairy-25d2d.firebaseio.com/messages/" + userMobie + "_" + chatWith);
        reference2 = new Firebase("https://meridairy-25d2d.firebaseio.com/messages/" + chatWith + "_" + userMobie);
        System.out.println("==reference1=====" + reference1);
        System.out.println("==reference2=====" + reference2);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = ediMessageArea.getText().toString().trim();
                if (!messageText.equals("")) {
                    String refreshedToken = firebase_tocan;//add your user refresh tokens who are logged in with firebase.
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(refreshedToken);
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user", userMobie);
                    System.out.println("==user=====" + userMobie);
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                    System.out.println("==KEY_Name=====" + sessionManager.getValueSesion(SessionManager.KEY_Name));

                    sendMessage(jsonArray, "A New message from " + sessionManager.getValueSesion(SessionManager.KEY_Name), "" + messageText, "Http:\\google.com", "" + messageText);
                    ediMessageArea.setText("");
                }
            }
        });


        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);

                System.out.println("message=====>>>" + "" + s);
                System.out.println("user=====>>>" + "" + map.get("user").toString());

                try {
                    if (map.get("user").toString() != null) {
                        String message = map.get("message").toString();
                        String userName = map.get("user").toString();
                        if (userName.equals(userMobie)) {
                            addMessageBox(message, 1);
                        } else {
                            addMessageBox(message, 2);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                String messageID = dataSnapshot.getKey();

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    public void addMessageBox(String message, int type) {
        View descriptionLayout;

        TextView textView;
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;
        if (type != 1) {
            LayoutInflater inflater = getLayoutInflater();
            descriptionLayout = inflater.inflate(R.layout.item_chat_left, null, false);
            textView = descriptionLayout.findViewById(R.id.text_msg);
            textView.setText(message);
            userImg = descriptionLayout.findViewById(R.id.userImg);//userImg
//            Log.d("imgPath>>>", imgPath);
            //  Glide.with(getApplicationContext()).load(imgPath).error(R.drawable.default_user).into(userImg);
            lp2.gravity = Gravity.LEFT;
        } else {
            LayoutInflater inflater = getLayoutInflater();
            descriptionLayout = inflater.inflate(R.layout.item_chat_right, null, false);
            textView = (TextView) descriptionLayout.findViewById(R.id.text_msg);
            // userImg = (CircleImageView) descriptionLayout.findViewById(R.id.userImgright);//userImg
            textView.setText(message);
            // Glide.with(getApplicationContext()).load(sessionManager.getValueSesion(SessionManager.KEY_profileimg)).error(R.drawable.default_user).into(userImg);
            lp2.gravity = Gravity.RIGHT;
        }
        layout.addView(descriptionLayout);
        scrollView.post(new Runnable() {
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Glide.with(getApplicationContext()).resumeRequests();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //  Glide.clear(userImg);
        //  Glide.with(getApplicationContext()).pauseRequests();
    }


    public void sendMessage(JSONArray recipients, final String title, final String body, final String icon, final String message) {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    JSONObject root = new JSONObject();
                    JSONObject notification = new JSONObject();
                    notification.put("body", body);
                    notification.put("title", title);
                    notification.put("icon", icon);
                    JSONObject data = new JSONObject();
                    data.put("message", message);
                    root.put("notification", notification);
                    root.put("data", data);
                    root.put("registration_ids", recipients);
                    String result = postToFCM(root.toString());
                    Log.d("Main Activity", "Result: " + result);
                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                try {
                    JSONObject resultJson = new JSONObject(result);
                    int success, failure;
                    success = resultJson.getInt("success");
                    failure = resultJson.getInt("failure");
                    //  Toast.makeText(ChatActivity.this, "Message Success: " + success + "Message Failed: " + failure, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ChatActivity.this, "Message Failed, Unknown error occurred.", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    String postToFCM(String bodyString) throws IOException {
        final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, bodyString);
        Request request = new Request.Builder()
                .url(FCM_MESSAGE_URL)
                .post(body)
                .addHeader("Authorization", "key=" + mContext.getString(R.string.firebaseDBKey))
                .build();
        Response response = mClient.newCall(request).execute();

        return response.body().string();

    }

    @Override
    public void onBackPressed() {

        finish();
    }
}
