package com.example.echat.ui.users;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.echat.Adapter.UserAdapter;
import com.example.echat.Model.User;
import com.example.echat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {

    private UsersViewModel usersViewModel;
    private RecyclerView recyclerView;
    private List<User> myUsers;
    private UserAdapter userAdapter;
    private EditText searchUser;
    private ImageButton searchUserImage;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        usersViewModel =
                new ViewModelProvider(this).get(UsersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_users, container, false);

        recyclerView = root.findViewById(R.id.user_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        myUsers = new ArrayList<>();

        //function to read users
        readUsers();

        searchUser = root.findViewById(R.id.search_user);
        searchUserImage = root.findViewById(R.id.search_user_icon);

        searchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                searchUsers(s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return root;
    }

    public void searchUsers(String s){
        final FirebaseUser cuurentUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("username").startAt(s).endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myUsers.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);

                    assert user != null;
                    assert cuurentUser != null;
                    if(!user.getId().equals(cuurentUser.getUid())){
                        myUsers.add(user);
                    }
                }

                userAdapter = new UserAdapter(getContext(), myUsers, false);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readUsers() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userData = FirebaseDatabase.getInstance().getReference("Users");

        userData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(searchUser.getText().toString().equals("")) {
                    myUsers.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);

                        if (!user.getId().equals(firebaseUser.getUid())) {
                            myUsers.add(user);
                        }

                        userAdapter = new UserAdapter(getContext(), myUsers, false);
                        recyclerView.setAdapter(userAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
