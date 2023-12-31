package com.karatekids.wikipediarace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;

public class FindFriendActivity extends AppCompatActivity {

    public static String friends = "";

    private static final int[] TABLE_HEADERS = {R.string.friends_id, R.string.friends_name};

    private static String selectedFriendName = "";
    private static String selectedFriendId = "";

    //minor change
    //ChatGPT usage: No
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);


        TableView<String[]> tableView = (TableView<String[]>) findViewById(R.id.friends_table);
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(this, TABLE_HEADERS));

        int colorEvenRows = getResources().getColor(R.color.white);
        int colorOddRows = getResources().getColor(R.color.LightGrey);
        tableView.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));

        tableView.addDataClickListener(new FriendsListListener());

            try {
                JSONArray friendsArr = new JSONArray(friends);

                String[][] friendsData = new String[friendsArr.length()][2];

                for (int i = 0; i < friendsArr.length(); i++) {
                    String[] rowData = new String[2];

                    JSONObject element = friendsArr.getJSONObject(i);

                    friendsData[i][0] = element.getString("_id");
                    friendsData[i][1] = element.getString("name");
                }

                tableView.setDataAdapter(new SimpleTableDataAdapter(this, friendsData));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        findViewById(R.id.validate_friend_id_bt).setOnClickListener(new View.OnClickListener() {
            //ChatGPT usage: No
            @Override
            public void onClick(View view) {
                TextView friend_id = (TextView) findViewById(R.id.friend_id_bar);

                Networker.addFriend(friend_id.getText().toString());

                if(friends != null && !friends.isEmpty()) {
                    try {
                        JSONArray friendsArr = new JSONArray(friends);

                        int friendsCount = friendsArr.length();

                        int count = 0;

                        while(friendsArr.length() == friendsCount && count < 6) {
                            Networker.getFriends();
                            friendsArr = new JSONArray(friends);
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            count++;
                        }


                        String[][] friendsData = new String[friendsArr.length()][2];

                        for (int i = 0; i < friendsArr.length(); i++) {

                            JSONObject element = friendsArr.getJSONObject(i);

                            friendsData[i][0] = element.getString("_id");
                            friendsData[i][1] = element.getString("name");
                        }

                        tableView.setDataAdapter(new SimpleTableDataAdapter(FindFriendActivity.this, friendsData));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        findViewById(R.id.friend_game_bt).setOnClickListener(new View.OnClickListener(){
            //ChatGPT usage: No
            @Override
            public void onClick(View view){
                Intent friendLobby = new Intent(FindFriendActivity.this, LobbyActivity.class);
                friendLobby.putExtra("game_mode","friend");
                friendLobby.putExtra("friend_id", selectedFriendId);
                startActivity(friendLobby);
            }
        });

        TextView player_id = (TextView) findViewById(R.id.user_id_value_tv);
        player_id.setText(Networker.getId());
    }

    //ChatGPT usage: No
    // https://stackoverflow.com/questions/18404271/android-back-button-to-specific-activity#:~:text=If%20you%20need%20to%20go%20back%20what%20ever,Your%20intent%20here%20%2F%2F%20%2F%2F%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2A%2F%2F%20return%20true%3B%20%7D
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(FindFriendActivity.this, PlayGameActivity.class));
        finish();

    }

    //ChatGPT usage: No
    private class FriendsListListener implements TableDataClickListener<String[]> {
        //ChatGPT usage: No
        @Override
        public void onDataClicked(int rowIndex, String clickedString[]) {
            try {
                JSONArray friendsArr = new JSONArray(friends);
                JSONObject obj = friendsArr.getJSONObject(rowIndex);

                selectedFriendName = obj.getString("name");
                ((TextView) findViewById(R.id.clicked_friend_tv)).setText(selectedFriendName);
                selectedFriendId = obj.getString("_id");
                ((TextView) findViewById(R.id.clicked_friend_id_tv)).setText(selectedFriendId);
                ((Button) findViewById(R.id.friend_game_bt)).setEnabled(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}