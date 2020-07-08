package com.example.fooddonationapplication.ui.social_community.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.fooddonationapplication.ui.general.LoginActivity;
import com.example.fooddonationapplication.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SocialCommunityProfileFragment extends Fragment {

    private TextView emailTextView, fullNameTextView, telephoneNumberTextView;
    private Button logOutButton;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_social_community_profile, container, false);
        emailTextView = view.findViewById(R.id.socialCommunityProfileEmail);
        fullNameTextView = view.findViewById(R.id.socialCommunityProfileFullName);
        telephoneNumberTextView = view.findViewById(R.id.socialCommunityProfileTelephoneNumber);
        logOutButton = view.findViewById(R.id.socialCommunityProfileLogOutButton);

        emailTextView.setText(user.getEmail());
        fullNameTextView.setText(user.getDisplayName());

        db.collection("users").document(user.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            telephoneNumberTextView.setText(documentSnapshot.getString("phone"));
                        }
                    }
                });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        return view;
    }
}
