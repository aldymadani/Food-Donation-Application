package com.example.fooddonationapplication.ui.donator.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fooddonationapplication.ui.general.LoginActivity;
import com.example.fooddonationapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class DonatorProfileFragment extends Fragment {

    private static final String TAG = "EditProfileFragment";

    private TextView emailTextView, passwordTextView, passwordConfirmTextView, fullNameTextView, telephoneNumberTextView;
    private TextInputLayout passwordLayout, passwordConfirmLayout;
    private Button logOutButton, editProfileButton;
    private ProgressBar progressBar;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String newPhoneNumber;
    private String oldPhoneNumber;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_donator,container,false);

        emailTextView = rootView.findViewById(R.id.edit_profile_email);
        passwordTextView = rootView.findViewById(R.id.edit_profile_password);
        fullNameTextView = rootView.findViewById(R.id.edit_profile_name);
        telephoneNumberTextView = rootView.findViewById(R.id.edit_profile_telephone_number);
        progressBar = rootView.findViewById(R.id.edit_profile_progressbar);
        logOutButton = rootView.findViewById(R.id.edit_profile_log_out);
        editProfileButton = rootView.findViewById(R.id.edit_profile_confirm);
        passwordConfirmTextView = rootView.findViewById(R.id.edit_profile_password_confirm);

        passwordLayout = rootView.findViewById(R.id.edit_profile_password_layout);
        passwordConfirmLayout = rootView.findViewById(R.id.edit_profile_password_confirm_layout);

        emailTextView.setText(user.getEmail());
        fullNameTextView.setText(user.getDisplayName());

        final String oldName = user.getDisplayName();
        final String oldEmail = user.getEmail();

        db.collection("users").document(user.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            oldPhoneNumber = documentSnapshot.getString("phone");
                            telephoneNumberTextView.setText(documentSnapshot.getString("phone")); // TODO later pass from login to reduce read
                        }
                    }
                });

        progressBar.setVisibility(View.INVISIBLE);

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newEmail = emailTextView.getText().toString();
                String newName = fullNameTextView.getText().toString();
                newPhoneNumber = telephoneNumberTextView.getText().toString();
                if (!newEmail.equals(oldEmail)) {
                    user.updateEmail(newEmail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "User email address updated.", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "User email address updated.");
                                    }
                                }
                            });
                }

                if (!passwordTextView.getText().toString().isEmpty() || !passwordConfirmTextView.getText().toString().isEmpty()) {
                    if (passwordConfirmTextView.getText().toString().isEmpty() || passwordTextView.getText().toString().isEmpty()) {
                        passwordConfirmLayout.setError("Please insert your new password in both fields");
                        passwordLayout.setError("Please insert your new password in both fields");
                    } else if (passwordTextView.getText().toString().length() <= 5 || passwordConfirmTextView.getText().toString().length() <= 5) {
                        passwordConfirmLayout.setError("Password minimum is 6 digit");
                        passwordLayout.setError("Password minimum is 6 digit");
                    } else if (!passwordTextView.getText().toString().equals(passwordConfirmTextView.getText().toString())) {
                        passwordConfirmLayout.setError("The password is not matching");
                    } else {
                        passwordConfirmLayout.setErrorEnabled(false);
                        passwordLayout.setErrorEnabled(false);
                        user.updatePassword(passwordConfirmTextView.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "User password updated.", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "User password updated.");
                                        }
                                    }
                                });
                    }
                }

                if (!newName.equals(oldName) || !newPhoneNumber.equals(oldPhoneNumber)) {
                    editProfileButton.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(newName)
                            .build();
                    WriteBatch batch = db.batch();
                    DocumentReference userReference = db.collection("users").document(user.getUid());
                    if (!newPhoneNumber.equals(oldPhoneNumber)) {
                        batch.update(userReference, "name", newName);
                        batch.update(userReference, "phone", newPhoneNumber);
                    } else {
                        batch.update(userReference, "name", newName);
                    }
                    batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            getData();
                        }
                    });
                }
            }
        });
        return rootView;
    }

    private void getData() {
        db.collection("donators").whereEqualTo("uuid",user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> list = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                list.add(document.getId());
                            }
                            Log.d(TAG, list.toString());
                            updateData((ArrayList) list);
                        }
                    }
                });
    }

    private void updateData(ArrayList list) {
        WriteBatch batch = db.batch();
        for (int i = 0; i < list.size(); i++) {
            if (!newPhoneNumber.equals(oldPhoneNumber)) {
                DocumentReference ref = db.collection("donators").document((String) list.get(i));
                batch.update(ref, "name", user.getDisplayName());
                batch.update(ref, "phone", newPhoneNumber);
            } else {
                DocumentReference ref = db.collection("donators").document((String) list.get(i));
                batch.update(ref, "name", user.getDisplayName());
            }
        }

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getContext(), "Profile is successfully updated", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                editProfileButton.setVisibility(View.VISIBLE);
            }
        });
    }
}
