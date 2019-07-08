package com.daovu67.iotapp.views.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daovu67.iotapp.R;
import com.daovu67.iotapp.adapters.CardAdapter;
import com.daovu67.iotapp.control.RecyclerItemClickListener;
import com.daovu67.iotapp.control.ServerConnect;
import com.daovu67.iotapp.models.Card;
import com.daovu67.iotapp.models.Devices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MAINACTIVITY";
    //server infor
    private String serverUri = "tcp://m16.cloudmqtt.com:11210";
    private String clientId = MqttClient.generateClientId();
    private MqttAndroidClient client = new MqttAndroidClient(MainActivity.this, serverUri,
            clientId);

    private ServerConnect serverConnect = new ServerConnect(this, client);
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ArrayList<Card> listCard = new ArrayList<>();
    private ArrayList<Devices> listDevices = new ArrayList<>();

    private ImageButton addCard;
    private RecyclerView recyclerView;
    private CardAdapter cardAdapter;
    private ImageButton btnLogout;
    private TextView noDeviceTxt, tvTem, tvHum;

    private String subTemperature = "-1", subHumidity = "-1", subRL1 = "-1", subRL2 = "-1",
            subRL3 = "-1", subRL4 = "-1";
    private boolean flagTem = false, flagHum = false, flagRelay1 = false, flagRelay2 = false,
            flagRelay3 = false, flagRelay4 = false;
    private boolean relayStatus1, relayStatus2, relayStatus3, relayStatus4;
    private boolean readDataFlag = true;
    private boolean flag = true;




    @Override
    protected void onStart() {
        super.onStart();
        //check internet connection
        if (isNetworkAvailable(MainActivity.this)) {
            mAuth.addAuthStateListener(mAuthListener);

        } else {

            Toast.makeText(this, "No internet connection, please check again!!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_main);
        Init();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveUserSetting();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //check internet connection
        if (isNetworkAvailable(MainActivity.this)) {
            serverConnect.connectMQTT();
        }

        if (readDataFlag) {
            loadUserSetting();
            readDataFlag = false;
        }
    }

    private void Init() {
        addCard = findViewById(R.id.addButton);
        recyclerView = findViewById(R.id.recycleView);
        btnLogout = findViewById(R.id.logout);
        noDeviceTxt = findViewById(R.id.nodevicetxt);
        tvTem = findViewById(R.id.tv_tem);
        tvHum = findViewById(R.id.tv_hum);
        setDevice();
        initRecyclerView();
    }

    public void setDevice() {
        listDevices.add(new Devices("Relay1", "OFF", R.drawable.ic_off, Color.parseColor("#448AFF")));
        listDevices.add(new Devices("Relay2", "OFF", R.drawable.ic_off, Color.parseColor("#448AFF")));
        listDevices.add(new Devices("Relay3", "OFF", R.drawable.ic_off, Color.parseColor("#448AFF")));
        listDevices.add(new Devices("Relay4", "OFF", R.drawable.ic_off, Color.parseColor("#448AFF")));

    }

    public void initRecyclerView() {
        Log.d(TAG,"recyclerview");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        cardAdapter = new CardAdapter(listCard, getApplicationContext());
        recyclerView.setAdapter(cardAdapter);
    }

    // callback message from mqtt server
    private void controlMQTT() {
        client.setCallback(new MqttCallback() {

            @Override
            public void connectionLost(Throwable cause) {
                serverConnect.connectMQTT();
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void messageArrived(String topic, MqttMessage message) {

                try {
                    switch (topic) {

                        case "ESP/temperature": {
                            Log.d(TAG,"temperature callback");
                            if ((new String(message.getPayload())).equals(subTemperature)) {
                                flagTem = false;
                            } else {
                                subTemperature = new String(message.getPayload());
                                flagTem = true;
                            }
                        }
                        break;
                        case "ESP/humidity": {
                            if ((new String(message.getPayload())).equals(subHumidity)) {
                                flagHum = false;
                            } else {
                                subHumidity = new String(message.getPayload());
                                flagHum = true;
                            }
                        }
                        break;

                        case "ESPg/RL1": {
                            Log.d(TAG,"rl1 callback");
                            if ((new String(message.getPayload())).equals(subRL1)) {
                                flagRelay1 = false;
                            } else {
                                subRL1 = new String(message.getPayload());
                                flagRelay1 = true;
                            }

                        }
                        break;
                        case "ESPg/RL2": {
                            if ((new String(message.getPayload())).equals(subRL2)) {
                                flagRelay2 = false;
                            } else {
                                subRL2 = new String(message.getPayload());
                                flagRelay2 = true;
                            }

                        }
                        break;
                        case "ESPg/RL3": {
                            if ((new String(message.getPayload())).equals(subRL3)) {
                                flagRelay3 = false;
                            } else {
                                subRL3 = new String(message.getPayload());
                                flagRelay3 = true;
                            }
                        }
                        break;
                        case "ESPg/RL4": {
                            if ((new String(message.getPayload())).equals(subRL4)) {
                                flagRelay4 = false;
                            } else {
                                subRL4 = new String(message.getPayload());
                                flagRelay4 = true;
                            }
                        }
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (flagTem) {
                    tvTem.setText(subTemperature + "°C");
                    flagTem = false;
                }

                if (flagHum) {
                    tvHum.setText(subHumidity + "%");
                    flagHum = false;
                }


                if (flagRelay1) {
                    if (subRL1.equals("1")) {
                        listDevices.get(0).setData("ON");
                        listDevices.get(0).setImage(R.drawable.ic_on);
                        relayStatus1 = true;
                        flagRelay1 = false;
                    } else {
                        listDevices.get(0).setData("OFF");
                        listDevices.get(0).setImage(R.drawable.ic_off);
                        relayStatus1 = false;
                        flagRelay1 = false;
                    }
                    cardAdapter.notifyDataSetChanged();
                }

                if (flagRelay2) {
                    if (subRL2.equals("1")) {
                        listDevices.get(1).setData("ON");
                        listDevices.get(1).setImage(R.drawable.ic_on);
                        relayStatus2 = true;
                        flagRelay2 = false;
                    } else {
                        listDevices.get(1).setData("OFF");
                        listDevices.get(1).setImage(R.drawable.ic_off);
                        relayStatus2 = false;
                        flagRelay2 = false;
                    }
                    cardAdapter.notifyDataSetChanged();
                }

                if (flagRelay3) {
                    if (subRL3.equals("1")) {
                        listDevices.get(2).setData("ON");
                        listDevices.get(2).setImage(R.drawable.ic_on);
                        relayStatus3 = true;
                        flagRelay3 = false;
                    } else {
                        listDevices.get(2).setData("OFF");
                        listDevices.get(2).setImage(R.drawable.ic_off);
                        relayStatus3 = false;
                        flagRelay3 = false;
                    }
                    cardAdapter.notifyDataSetChanged();
                }

                if (flagRelay4) {
                    if (subRL4.equals("1")) {
                        listDevices.get(3).setData("ON");
                        listDevices.get(3).setImage(R.drawable.ic_on);
                        relayStatus4 = true;
                        flagRelay4 = false;
                    } else {
                        listDevices.get(3).setData("OFF");
                        listDevices.get(3).setImage(R.drawable.ic_off);
                        relayStatus4 = false;
                        flagRelay4 = false;
                    }
                    cardAdapter.notifyDataSetChanged();
                }

            }


            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

    }

    private void onClick() {

        noDeviceTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDialog();
                Log.d(TAG,"no devices textview");
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOutDialog();
                Log.d(TAG,"logout");
            }
        });

        addCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDialog();
                Log.d(TAG,"add dialog button");
            }
        });

        //recyclerview onclick control mqtt
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (listCard.get(position).getDevice().getName().equals("Relay1")) {
                    if (relayStatus1) {
                        serverConnect.setPublish("ESPn/RL1", "0");
                        Log.d(TAG,"public rl1");

                    } else {
                        serverConnect.setPublish("ESPn/RL1", "1");

                    }
                }

                if (listCard.get(position).getDevice().getName().equals("Relay2")) {
                    if (relayStatus2) {
                        serverConnect.setPublish("ESPn/RL2", "0");

                    } else {
                        serverConnect.setPublish("ESPn/RL2", "1");

                    }
                }

                if (listCard.get(position).getDevice().getName().equals("Relay3")) {
                    if (relayStatus3) {
                        serverConnect.setPublish("ESPn/RL3", "0");

                    } else {
                        serverConnect.setPublish("ESPn/RL3", "1");

                    }
                }

                if (listCard.get(position).getDevice().getName().equals("Relay4")) {
                    if (relayStatus4) {
                        serverConnect.setPublish("ESPn/RL4", "0");

                    } else {
                        serverConnect.setPublish("ESPn/RL4", "1");

                    }
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

                if (flag) {
                    editDialog(position);
                    flag = false;
                }
            }
        }));
    }

    // edit dialog
    @SuppressLint("SetTextI18n")
    private void editDialog(final int position) {

        final String[] spinnerText = new String[1];
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_edit);
        //ánh xạ
        dialog.setCancelable(false);
        Button saveBtn = dialog.findViewById(R.id.editSave);
        Button deleteBtn = dialog.findViewById(R.id.editDelete);
        Button cancel = dialog.findViewById(R.id.editCancel);
        final EditText name = dialog.findViewById(R.id.editCardName);
        TextView header = dialog.findViewById(R.id.editHeader);
        final Spinner spinner = dialog.findViewById(R.id.editColorSelect);

        header.setText("Edit " + listCard.get(position).getCardName());
        name.setHint(listCard.get(position).getCardName());
        spinner.setSelection(devicePosition(listCard.get(position).getDevice().getName()));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                spinnerText[0] = spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinnerText[0] = listCard.get(position).getDevice().getName();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (!TextUtils.isEmpty(name.getText())) {
                    listCard.get(position).setCardName(name.getText().toString());
                }
                listCard.get(position).setDevice(listDevices.get(devicePosition(spinnerText[0])));
                cardAdapter.notifyDataSetChanged();
                flag = true;
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listCard.remove(position);
                cardAdapter.notifyItemRemoved(position);
                Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_SHORT).show();

                if (listCard.size() == 0) noDeviceTxt.setVisibility(View.VISIBLE);
                flag = true;

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                flag = true;
            }
        });
        dialog.show();
    }

    //add devices dialog
    private void addDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_add);
        dialog.setCancelable(false);
        Button addButton = dialog.findViewById(R.id.addNewDevice);
        final Spinner spinner = dialog.findViewById(R.id.deviceSelect);
        final EditText editText = dialog.findViewById(R.id.addCardName);
        Button cancelBtn = dialog.findViewById(R.id.cancel);
        final String[] spinnerText = new String[1];

        //spiner select devices
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                spinnerText[0] = spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listCard.add(new Card(listDevices.get(devicePosition(spinnerText[0])), editText.getText().toString()));
                cardAdapter.notifyItemInserted(listCard.size());
                saveUserSetting();
                if (listCard.size() != 0) noDeviceTxt.setVisibility(View.INVISIBLE);
                dialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    // logout dialog
    private void logOutDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm log out !");
        builder.setMessage("Do you really want to log out ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.show();
    }

    private int devicePosition(String deviceName) {
        int position = 0;
        switch (deviceName) {

            case "Relay1":
                position = 0;
                break;
            case "Relay2":
                position = 1;
                break;
            case "Relay3":
                position = 2;
                break;
            case "Relay4":
                position = 3;
                break;
        }
        return position;
    }

    //check network
    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    //loding user setting
    private void loadUserSetting() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("userSetting", Context.MODE_PRIVATE);
        if (sharedPreferences.getInt("cardNumber", 0) == 0) noDeviceTxt.setVisibility(View.VISIBLE);
        for (int i = 0; i < sharedPreferences.getInt("cardNumber", 0); i++) {
            listCard.add(new Card(listDevices.get(devicePosition(Objects.requireNonNull(sharedPreferences.
                    getString("card" + i, "")))), sharedPreferences.getString("cardName" + i, "")));
            cardAdapter.notifyItemChanged(i);
        }
    }

    // save user seting using shared preferences
    private void saveUserSetting() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("userSetting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("cardNumber", listCard.size());
        for (int i = 0; i < listCard.size(); i++) {
            editor.putString("card" + i, listCard.get(i).getDevice().getName());
            editor.putString("cardName" + i, listCard.get(i).getCardName());
        }

        editor.apply();
    }

    private void updateUI(final FirebaseUser user) {
        //check current account
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (user == null || !user.isEmailVerified()) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    overridePendingTransition(0, 0);
                } else {
                    serverConnect.connectMQTT();
                    controlMQTT();
                    onClick();
                }
            }
        };
    }

}

