package com.example.rfid;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import io.netpie.microgear.Microgear;
import io.netpie.microgear.MicrogearEventListener;

public class AddPetActivity extends AppCompatActivity {
    private DatabaseReference database;

    private Microgear microgear = new Microgear(this);
    private String appid = "PetFeederIOT";
    private String key = "sBX5S8fIOl56Hzv";
    private String secret = "jqCJuS4h8fuT9qDlDIQLQrPf8";
    private String alias = "Android";

    private ActionBar actionBar;
    private EditText nameText;
    private EditText tagText;
    private EditText userSetText;
    private RadioButton dogRadio;
    private RadioButton catRadio;
    private RadioButton noLimitRadio;
    private RadioButton defaultRadio;
    private RadioButton userSetRadio;
    private Button addPetButton;
    private Button scanButton;

    private String petType;
    private String eatType;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)   {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        MicrogearCallBack callBack = new MicrogearCallBack();
        microgear.setCallback(callBack);
        microgear.connect(appid, key, secret, alias);

        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance().getReference();

        nameText = (EditText) findViewById(R.id.nameTextAddActivity);
        tagText = (EditText) findViewById(R.id.tagTextAddActivity);
        userSetText = (EditText) findViewById(R.id.userSetTextAddActivity);
        dogRadio = (RadioButton) findViewById(R.id.dogRadioAddActivity);
        catRadio = (RadioButton) findViewById(R.id.catRadioAddActivity);
        defaultRadio = (RadioButton) findViewById(R.id.defaultRadioAddActivity);
        noLimitRadio = (RadioButton) findViewById(R.id.noLimitRadioAddActivity);
        userSetRadio = (RadioButton) findViewById(R.id.userSetRadioAddActivity);
        addPetButton = (Button) findViewById(R.id.addPetButtonAddActivity);
        scanButton = (Button) findViewById(R.id.scanButtonAddActivity);

        petType = "";
        eatType = "";

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("addPet-scanButton","click");
                microgear.publish("/AndroidAddTag","add");
                showProgressDialog();
            }
        });

        addPetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tagText.getText().toString().equals("")
                        || nameText.getText().toString().equals("")
                        || !(catRadio.isChecked() || dogRadio.isChecked())
                        || !(defaultRadio.isChecked() || noLimitRadio.isChecked() || userSetRadio.isChecked())
                        || (userSetRadio.isChecked() && userSetText.getText().toString().equals("")))
                {
                    showEmptyDataDialog();
                }else {
                    database.child("pets").orderByChild("tagID").equalTo(tagText.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                DataSnapshot data = dataSnapshot.getChildren().iterator().next();
                                Log.i("database-key",data.getKey());
                                Log.i("database-name",data.child("name").getValue(String.class));
                                Log.i("database-type",data.child("type").getValue(String.class));
                                showRepeatedDialog(data.child("name").getValue(String.class),data.child("type").getValue(String.class),data.getKey());
                            }else {
                                Map<String,Object> data = new HashMap<>();
                                Log.i("database-exists",dataSnapshot.exists()+"");
                                data.put("name",nameText.getText().toString());
                                data.put("tagID",tagText.getText().toString());
                                data.put("type",petType);
                                data.put("eatType",eatType);
                                if (eatType.equals("default")){
                                    if (petType.equals("dog")){
                                        data.put("eatWeight", 290);
                                    }else{
                                        data.put("eatWeight", 60);
                                    }
                                }else if (eatType.equals("noLimit")){
                                    data.put("eatWeight", 9999);
                                }else {
                                    data.put("eatWeight",Integer.parseInt(userSetText.getText().toString()));
                                }
                                database.child("pets").push().setValue(data);
                                microgear.disconnect();
                                finish();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(AddPetActivity.this, "Failed to read data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }


    public void showRepeatedDialog(String name, String type, final String key){
        if (type.equals("dog")){
            type = "สุนัข";
        }else{
            type = "แมว";
        }
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddPetActivity.this,R.style.DialogStyle);
        alertDialog.setTitle("Tag ซ้ำกับสัตว์เลี้ยงอื่น");
        alertDialog.setMessage("Tag ถูกใช้งานแล้ว  \nโดย "+type+" : "+name+"\nคุณยังต้องการใช้ Tag นี้หรือไม่?"+"\n\nหากตกลง \nTag ของ "+name+" จะถูกนำออก");
        alertDialog.setNegativeButton("ยกเลิก", null);
        alertDialog.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                database.child("pets/"+key+"/tagID").setValue("");
                Map<String,Object> data = new HashMap<>();
                data.put("name",nameText.getText().toString());
                data.put("tagID",tagText.getText().toString());
                data.put("type",petType);
                data.put("eatType",eatType);
                if (eatType.equals("default")){
                    if (petType.equals("dog")){
                        data.put("eatWeight", 290);
                    }else{
                        data.put("eatWeight", 60);
                    }
                }else if (eatType.equals("noLimit")){
                    data.put("eatWeight", 9999);
                }else {
                    data.put("eatWeight",Integer.parseInt(userSetText.getText().toString()));
                }
                database.child("pets").push().setValue(data);
                microgear.disconnect();
                finish();
            }
        });
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    public void showProgressDialog() {
        progressDialog = new ProgressDialog(AddPetActivity.this,R.style.DialogStyle);
        progressDialog.setTitle("รอการสแกน Tag");
        progressDialog.setMessage("กำลังรอการสแกน Tag \nจากเครื่องให้อาหารสัตว์เลี้ยง...");
        progressDialog.setCancelable(false);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "ยกเลิก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("progress-cancel","cancel");
                microgear.publish("AndroidAddTag","cancel");
            }
        });
        progressDialog.show();
    }

    public void showEmptyDataDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddPetActivity.this,R.style.DialogStyle);
        alertDialog.setTitle("กรอกข้อมูลไม่ครบ");
        alertDialog.setMessage("กรุณากรอกข้อมูลสัตว์เลี้ยงให้ครบถ้วน");
        alertDialog.setNegativeButton("ตกลง", null);
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                Log.i("addPet-backHome","back home");
                microgear.disconnect();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onTypeRadioClicked(View view){
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId())
        {
            case R.id.dogRadioAddActivity:
                if (checked){
                    petType = "dog";
                }
                break;
            case R.id.catRadioAddActivity:
                if (checked){
                    petType = "cat";
                }
                break;
        }

        Log.i("addPet-petType",petType+"");
    }

    public void onFeedRadioClicked(View view){
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId())
        {
            case R.id.noLimitRadioAddActivity:
                if (checked){
                    eatType = "noLimit";
                    userSetText.setText("");
                    userSetText.setEnabled(false);
                }
                break;
            case R.id.defaultRadioAddActivity:
                if (checked){
                    eatType = "default";
                    userSetText.setText("");
                    userSetText.setEnabled(false);
                }
                break;
            case R.id.userSetRadioAddActivity:
                if (checked) {
                    eatType = "userSet";
                    userSetText.setEnabled(true);
                }
        }

        Log.i("addPet-eatType",eatType+"");
    }

    @Override
    public void onBackPressed(){
        microgear.disconnect();
        finish();
    }

    class MicrogearCallBack implements MicrogearEventListener {

        @Override
        public void onConnect() {
            Log.i("netpie-Connected","Now I'm connected with netpie");
            microgear.subscribe("/NodemcuSendTag");
        }

        @Override
        public void onMessage(String topic, String message) {
            Log.i("netpie-Message",topic+" : "+message);
            if (topic.contains("NodemcuSendTag")&&progressDialog.isShowing()) {
                tagText.setText(message);
                progressDialog.dismiss();
                microgear.publish("AndroidAddTag","complete");
            }

        }

        @Override
        public void onPresent(String token) {
            Log.i("netpie-present","New friend Connect :"+token);
        }

        @Override
        public void onAbsent(String token) {
            Log.i("netpie-absent","Friend lost :"+token);
        }

        @Override
        public void onDisconnect() {
            Log.i("netpie-disconnect","Disconnected");
        }

        @Override
        public void onError(String error) {
            Log.i("netpie-exception","Exception : "+error);
        }

        @Override
        public void onInfo(String info) {
            Log.i("netpie-info","info : "+info);
        }
    }
}
