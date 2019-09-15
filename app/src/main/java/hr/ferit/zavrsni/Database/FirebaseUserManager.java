package hr.ferit.zavrsni.Database;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import hr.ferit.zavrsni.Models.User;
import hr.ferit.zavrsni.interfaces.INewUserListener;

public class FirebaseUserManager {


    private static FirebaseUserManager instance;
    private DatabaseReference usersReference;
    private ValueEventListener mUserListener;
    private INewUserListener userListener;
    private Map<String, User> userMap;


    private FirebaseUserManager() {
        usersReference = FirebaseDatabase.getInstance().getReference().child("users");
        userMap = new HashMap<>();
    }

    public static FirebaseUserManager getInstance() {
        if (instance == null) {
            instance = new FirebaseUserManager();
        }
        return instance;
    }

    public void setUserInterface(INewUserListener listener) {
        this.userListener = listener;
    }

    public void addUserListener() {
        if (mUserListener == null) {
            mUserListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        User user = child.getValue(User.class);
                        userMap.put(user.getUserID(), user);
                    }
                    userListener.onUsersInitialize();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
        }
        usersReference.addListenerForSingleValueEvent(mUserListener);
    }

    public void removeUserListeners() {
        if (mUserListener != null) usersReference.removeEventListener(mUserListener);
    }

    public boolean checkIfNewUSer(String mUserID) {
        boolean state = true;
        if (userMap.containsKey(mUserID)) state = false;
        return state;
    }

    public void addUserToDatabase(User user) {
        usersReference.child(user.getUserID()).setValue(user);
    }
}
