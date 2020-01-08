package com.example.rfid;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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

public class DetailPetActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference dataPets;
    private DatabaseReference dataHistorys;
    private String primaryKey;

    private Microgear microgear = new Microgear(this);
    private String appid = "PetFeederIOT";
    private String keyMi = "sBX5S8fIOl56Hzv";
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

    private Button editPetButton;
    private Button scanButton;
    private Button removeButton;

    private String petType;
    private String eatType;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pet);

        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance();
        dataPets = database.getReference("pets");
        dataHistorys = database.getReference("historys");

        MicrogearCallBack callBack = new MicrogearCallBack();
        microgear.setCallback(callBack);
        microgear.connect(appid,keyMi,secret,alias);

        nameText = (EditText) findViewById(R.id.nameTextDetailActivity);
        tagText = (EditText) findViewById(R.id.tagTextDetailActivity);
        userSetText = (EditText) findViewById(R.id.userSetTextDetailActivity);
        dogRadio = (RadioButton) findViewById(R.id.dogRadioDetailActivity);
        catRadio = (RadioButton) findViewById(R.id.catRadioDetailActivity);
        noLimitRadio = (RadioButton) findViewById(R.id.noLimitRadioDetailActivity);
        defaultRadio = (RadioButton) findViewById(R.id.defaultRadioDetailActivity);
        userSetRadio = (RadioButton) findViewById(R.id.userSetRadioDetailActivity);
        editPetButton = (Button) findViewById(R.id.editPetButtonDetailActivity);
        scanButton = (Button) findViewById(R.id.scanButtontDetailActivity);
        removeButton = (Button) findViewById(R.id.removeButtontDetailActivity);

        petType = "";
        eatType = "";

        primaryKey = getIntent().getStringExtra("primaryKey");

        editPetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameText.getText().toString().equals("")
                        || !(catRadio.isChecked() || dogRadio.isChecked())
                        || !(defaultRadio.isChecked() || noLimitRadio.isChecked() || userSetRadio.isChecked())
                        || (userSetRadio.isChecked() && userSetText.getText().toString().equals("")))
                {
                    showEmptyDataDialog();
                }else{
                    dataPets.child(primaryKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Pet pet = dataSnapshot.getValue(Pet.class);
                            if (tagText.getText().toString().equals(pet.getTagID())
                                    && nameText.getText().toString().equals(pet.getName())
                                    && petType.equals(pet.getType())
                                    && eatType.equals(pet.getEatType())
                                    && ((eatType.equals("userSet") && (pet.getEatWeight() == Integer.parseInt(userSetText.getText().toString()))) || !eatType.equals("userSet")))
                            {
                                microgear.disconnect();
                                finish();
                            }else{
                                if (tagText.getText().toString().equals(pet.getTagID()) || tagText.getText().toString().equals("")) {
                                    showEditDataDialog();
                                }else{
                                    dataPets.orderByChild("tagID").equalTo(tagText.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()){
                                                DataSnapshot data = dataSnapshot.getChildren().iterator().next();
                                                showRepeatedDialog(data.child("name").getValue(String.class),data.child("type").getValue(String.class),data.getKey());
                                            }else{
                                                showEditDataDialog();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Toast.makeText(DetailPetActivity.this, "Failed to read data", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(DetailPetActivity.this, "Failed to read data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                microgear.publish("/AndroidAddTag","add");
                showProgressDialog();
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRemoveDialog();
            }
        });

        dataPets.child(primaryKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Pet pet = dataSnapshot.getValue(Pet.class);
                setDataDetail(pet);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DetailPetActivity.this, "Failed to read data", Toast.LENGTH_SHORT).show();
            }
        }); //C
    }

    public void showProgressDialog() {
        progressDialog = new ProgressDialog(DetailPetActivity.this,R.style.DialogStyle);
        progressDialog.setTitle("รอการสแกน Tag");
        progressDialog.setMessage("กำลังรอการสแกน Tag \nจากเครื่องให้อาหารสัตว์เลี้ยง...");
        progressDialog.setCancelable(false);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "ยกเลิก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                microgear.publish("AndroidAddTag","cancel");
            }
        });
        progressDialog.show();
    } //C

    public void showRepeatedDialog(String name, String type, final String key){
        if (type.equals("dog")){
            type = "สุนัข";
        }else{
            type = "แมว";
        }
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DetailPetActivity.this,R.style.DialogStyle);
        alertDialog.setTitle("Tag ซ้ำกับสัตว์เลี้ยงอื่น");
        alertDialog.setMessage("Tag ถูกใช้งานแล้ว  \nโดย "+type+" : "+name+"\nคุณยังต้องการใช้ Tag นี้หรือไม่?"+"\n\nหากตกลง \nTag ของ "+name+" จะถูกนำออก");
        alertDialog.setNegativeButton("ยกเลิก", null);
        alertDialog.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dataPets.child(key+"/tagID").setValue("");
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
                dataPets.child(primaryKey).setValue(data);
                microgear.disconnect();
                finish();
            }
        });
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    public void showEditDataDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DetailPetActivity.this,R.style.DialogStyle);
        alertDialog.setTitle("แก้ไขข้อมูลสัตว์เลี้ยง");
        alertDialog.setMessage("คุณต้องการแก้ไขข้อมูลสัตว์เลี้ยงหรือไม่?");
        alertDialog.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                microgear.disconnect();
                finish();
            }
        });
        alertDialog.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
                dataPets.child(primaryKey).setValue(data);
                microgear.disconnect();
                finish();
            }
        } );
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    public void showEmptyDataDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DetailPetActivity.this,R.style.DialogStyle);
        alertDialog.setTitle("กรอกข้อมูลไม่ครบ");
        alertDialog.setMessage("กรุณากรอกข้อมูลสัตว์เลี้ยงให้ครบถ้วน");
        alertDialog.setNegativeButton("ตกลง", null);
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    public void showDeleteDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DetailPetActivity.this,R.style.DialogStyle);
        alertDialog.setTitle("ลบข้อมูลสัตว์เลี้ยง");
        alertDialog.setMessage("คุณต้องการลบข้อมูลสัตว์เลี้ยงนี้หรือไม่?");
        alertDialog.setNegativeButton("ยกเลิก", null);
        alertDialog.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dataPets.child(primaryKey).removeValue();
                dataHistorys.child(primaryKey).removeValue();
                microgear.disconnect();
                finish();
            }
        } );
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    public void showRemoveDialog(){
        if (!tagText.getText().toString().equals("")){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(DetailPetActivity.this,R.style.DialogStyle);
            alertDialog.setTitle("นำ Tag ออก");
            alertDialog.setMessage("คุณต้องการนำ Tag : "+tagText.getText().toString()+"\nออกจากสัตว์เลี้ยงนี้หรือไม่?");
            alertDialog.setNegativeButton("ยกเลิก", null);
            alertDialog.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    tagText.setText("");
                }
            });
            AlertDialog dialog = alertDialog.create();
            dialog.show();
        }else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(DetailPetActivity.this,R.style.DialogStyle);
            alertDialog.setTitle("ไม่มีข้อมูล Tag ");
            alertDialog.setMessage("ทำการเพิ่มข้อมูล Tag โดยกดปุ่มสแกน");
            alertDialog.setNegativeButton("ตกลง", null);
            AlertDialog dialog = alertDialog.create();
            dialog.show();
        }
    }

    public void showBackDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DetailPetActivity.this,R.style.DialogStyle);
        alertDialog.setTitle("ละทิ้งการแก้ไขข้อมูล");
        alertDialog.setMessage("กลับสู่หน้าหลัก\nโดยละทิ้งการแก้ไขหรือไม่?");
        alertDialog.setNegativeButton("ยกเลิก", null);
        alertDialog.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                microgear.disconnect();
                finish();
            }
        });
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.delete:
                showDeleteDialog();
                return true;
            case android.R.id.home:
                dataPets.child(primaryKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Pet pet = dataSnapshot.getValue(Pet.class);
                        if (tagText.getText().toString().equals(pet.getTagID())
                                && nameText.getText().toString().equals(pet.getName())
                                && petType.equals(pet.getType())
                                && eatType.equals(pet.getEatType())
                                && ((eatType.equals("userSet") && (pet.getEatWeight() == Integer.parseInt(userSetText.getText().toString())))
                                || !eatType.equals("userSet")))
                        {
                            microgear.disconnect();
                            finish();
                        }else{
                            showBackDialog();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(DetailPetActivity.this, "Failed to read data", Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setDataDetail(Pet pet) {
        tagText.setText(pet.getTagID());
        nameText.setText(pet.getName());

        if (pet.getType().equals("cat")) {
            catRadio.setChecked(true);
            petType = "cat";
        } else {
            dogRadio.setChecked(true);
            petType = "dog";
        }

        if (pet.getEatType().equals("userSet")){
            userSetRadio.setChecked(true);
            Log.i("detail-eatWeight",pet.getEatWeight()+"");
            Log.i("detail-userSetText",userSetText.getText().toString());
            userSetText.setText(pet.getEatWeight()+"");
            eatType = "userSet";
        }else if (pet.getEatType().equals("noLimit")){
            noLimitRadio.setChecked(true);
            eatType = "noLimit";
        }else{
            defaultRadio.setChecked(true);
            eatType = "default";
        }
    }

    public void onTypeRadioClicked(View view){
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId())
        {
            case R.id.dogRadioDetailActivity:
                if (checked){
                    petType = "dog";
                }
                break;
            case R.id.catRadioDetailActivity:
                if (checked){
                    petType = "cat";
                }
                break;
        }

        Log.i("detailPet-petType",petType+"");
    }

    public void onFeedRadioClicked(View view){
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId())
        {
            case R.id.noLimitRadioDetailActivity:
                if (checked){
                    eatType = "noLimit";
                    userSetText.setText("");
                    userSetText.setEnabled(false);
                }
                break;
            case R.id.defaultRadioDetailActivity:
                if (checked){
                    eatType = "default";
                    userSetText.setText("");
                    userSetText.setEnabled(false);
                }
                break;
            case R.id.userSetRadioDetailActivity:
                if (checked) {
                    eatType = "userSet";
                    userSetText.setEnabled(true);
                }
        }

        Log.i("detailPet-eatType",eatType+"");
    }

    @Override
    public void onBackPressed(){
        dataPets.child(primaryKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Pet pet = dataSnapshot.getValue(Pet.class);
                if (tagText.getText().toString().equals(pet.getTagID())
                        && nameText.getText().toString().equals(pet.getName())
                        && petType.equals(pet.getType())
                        && eatType.equals(pet.getEatType())
                        && ((eatType.equals("userSet") && (pet.getEatWeight() == Integer.parseInt(userSetText.getText().toString()))) || !eatType.equals("userSet")))
                {
                    microgear.disconnect();
                    finish();
                }else{
                    showBackDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DetailPetActivity.this, "Failed to read data", Toast.LENGTH_SHORT).show();
            }
        });
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