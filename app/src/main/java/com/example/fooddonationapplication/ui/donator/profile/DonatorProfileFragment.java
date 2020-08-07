package com.example.fooddonationapplication.ui.donator.profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.fooddonationapplication.model.Donator;
import com.example.fooddonationapplication.ui.general.LoginActivity;
import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.util.Util;
import com.example.fooddonationapplication.util.constant.IntentNameExtra;
import com.example.fooddonationapplication.util.constant.RequestCodeConstant;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DonatorProfileFragment extends Fragment implements View.OnFocusChangeListener {

    private static final String TAG = "EditProfileFragment";

    private EditText fullNameEditText, telephoneNumberEditText, totalDonationEditText;
    private TextInputLayout fullNameLayout, telephoneNumberLayout;
    private Button logOutButton, updateProfileButton, updateCredentialButton;
    private ProgressBar progressBar, updateCredentialProgressBar;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private boolean hasChanged;

    private Donator oldUserData;
    private Donator newUserData = new Donator();
    View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile_donator,container,false);
        initializeComponents();

        hasChanged = false;

        // Retrieving data from activity
        FragmentActivity fragmentActivity = requireActivity();
        oldUserData = fragmentActivity.getIntent().getParcelableExtra(IntentNameExtra.DONATOR_MODEL);
        if (oldUserData == null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            oldUserData = document.toObject(Donator.class);
                            initializeTextData();
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
//            Util.backToLogin(fragmentActivity);
        } else {
            initializeTextData();
        }


        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsubscribeNotification();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        updateCredentialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(getContext(), LoginActivity.class);
                loginIntent.putExtra(IntentNameExtra.IS_REAUTH, true);
                loginIntent.putExtra(IntentNameExtra.USER_EMAIL, user.getEmail());

                startActivityForResult(loginIntent, RequestCodeConstant.REAUTH_REQUEST_CODE);
            }
        });

        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullNameEditText.clearFocus();
                telephoneNumberEditText.clearFocus();
                Util.hideKeyboard(requireActivity());
                if (!hasEmptyField()) {
                    logOutButton.setEnabled(false);
                    updateCredentialButton.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    updateProfileButton.setVisibility(View.INVISIBLE);
                    checkingChanges();
                }
            }
        });
        return rootView;
    }

    private void unsubscribeNotification() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("FoodDonation")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "SubscribeToTopic To Topic FoodDonation";
                        if (!task.isSuccessful()) {
                            msg = "Failed: SubscribeToTopic To Topic FoodDonation";
                        }
                        Log.d(TAG, msg);
//                        Toast.makeText(MainSocialCommunityActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        final String userUuid = user.getUid();
        FirebaseMessaging.getInstance().unsubscribeFromTopic(userUuid)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "SubscribeToTopic To " + userUuid;
                        if (!task.isSuccessful()) {
                            msg = "Failed: SubscribeToTopic To Topic FoodDonation";
                        }
                        Log.d(TAG, msg);
//                        Toast.makeText(MainSocialCommunityActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initializeTextData() {
        telephoneNumberEditText.setText(oldUserData.getPhone());
        fullNameEditText.setText(user.getDisplayName());
        DecimalFormat df = new DecimalFormat("#.###");
        final String formattedTotalDonation = df.format(oldUserData.getTotalDonation());
        totalDonationEditText.setText(formattedTotalDonation);
    }

    private void checkingChanges() {
        newUserData.setName(fullNameEditText.getText().toString());
        newUserData.setPhone(telephoneNumberEditText.getText().toString());

        hasChanged = !oldUserData.isSame(newUserData);
        if (!hasChanged) {
            logOutButton.setEnabled(true);
            updateCredentialButton.setEnabled(true);
            progressBar.setVisibility(View.INVISIBLE);
            updateProfileButton.setVisibility(View.VISIBLE);
            Toast.makeText(requireActivity(), "No Changes", Toast.LENGTH_SHORT).show();
        } else {
            if (!oldUserData.getName().equalsIgnoreCase(newUserData.getName())) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(newUserData.getName())
                        .build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    getDonatorDocumentList();
                                    Log.d(TAG, "User profile updated.");
                                }
                            }
                        });
            } else {
                getDonatorDocumentList();
            }
        }
    }

    private void getDonatorDocumentList() {
        db.collection("donations").whereEqualTo("donatorId", user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> list = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                list.add(document.getId());
                            }
                            Log.d(TAG, list.toString());
                            updateDatabase((ArrayList) list);
                        }
                    }
                });
    }

    private void updateDatabase(ArrayList list) {
        WriteBatch batch = db.batch();
        for (int i = 0; i < list.size(); i++) {
            DocumentReference donatorReference = db.collection("donations").document((String) list.get(i));
            batch.update(donatorReference, "donatorName", newUserData.getName());
            batch.update(donatorReference, "donatorPhone", newUserData.getPhone());
        }

        DocumentReference userReference = db.collection("users").document(user.getUid());
        batch.update(userReference, "name", newUserData.getName());
        batch.update(userReference, "phone", newUserData.getPhone());

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getContext(), "Profile is successfully updated", Toast.LENGTH_SHORT).show();
                oldUserData.setName(newUserData.getName());
                oldUserData.setPhone(newUserData.getPhone());
                logOutButton.setEnabled(true);
                updateCredentialButton.setEnabled(true);
                progressBar.setVisibility(View.INVISIBLE);
                updateProfileButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private boolean hasEmptyField() {
        boolean hasEmptyField = true;
        boolean fullNameIsValidated = false;
        if (fullNameEditText.getText().toString().isEmpty()) {
            fullNameLayout.setError("Please insert your name");
        } else {
            fullNameLayout.setErrorEnabled(false);
            fullNameIsValidated = true;
        }

        boolean telephoneNumberIsValidated = false;
        if (telephoneNumberEditText.getText().toString().isEmpty()) {
            telephoneNumberLayout.setError("Please input your telephone number");
        } else if (telephoneNumberEditText.getText().toString().length() <= 5) {
            telephoneNumberLayout.setError("Password must be at least 6 characters");
        } else {
            telephoneNumberLayout.setErrorEnabled(false);
            telephoneNumberIsValidated = true;
        }

        if (fullNameIsValidated && telephoneNumberIsValidated) {
            hasEmptyField = false;
        }
        return hasEmptyField;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodeConstant.REAUTH_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    boolean isReauthSuccess = data.getBooleanExtra(IntentNameExtra.REAUTH_RESULT, false);
                    if (isReauthSuccess) {
                        Toast.makeText(getContext(), "Reauthentication successful.", Toast.LENGTH_SHORT).show();
                        openChangeCredentialDialog();
                    } else {
                        Toast.makeText(getContext(), "Reauthentication failed.", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(getContext(), "Reauthentication failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openChangeCredentialDialog() {
        Util.hideKeyboard(requireActivity());
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View updateCredentialDialog = getLayoutInflater().inflate(R.layout.dialog_option, null);
        mBuilder.setView(updateCredentialDialog);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        TextView dialogTitle = updateCredentialDialog.findViewById(R.id.registerDialogTitle);
        Button emailButton = updateCredentialDialog.findViewById(R.id.registerDialogDonatorButton);
        Button passwordButton = updateCredentialDialog.findViewById(R.id.registerDialogSocialCommunityButton);
        dialogTitle.setText("Choose Credential:");
        emailButton.setText("E-mail");
        passwordButton.setText("Password");

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                View updateCredentialDialog = getLayoutInflater().inflate(R.layout.dialog_credential_update_email, null);
                mBuilder.setView(updateCredentialDialog);
                final AlertDialog emailDialog = mBuilder.create();
                emailDialog.show();
                final TextInputLayout emailInputLayout = updateCredentialDialog.findViewById(R.id.updateEmailCredentialDialogEmailLayout);
                final EditText emailInput = updateCredentialDialog.findViewById(R.id.updateEmailCredentialDialogEmail);
                Button updateEmailButton = updateCredentialDialog.findViewById(R.id.updateEmailCredentialDialogButton);
                updateEmailButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                        if (emailInput.getText().toString().isEmpty()) {
                            emailInputLayout.setError("Please input your new email");
                        } else if (!emailInput.getText().toString().matches(emailPattern)) {
                            emailInputLayout.setError("Please input a valid email");
                        } else if (emailInput.getText().toString().equalsIgnoreCase(user.getEmail())) {
                            emailInputLayout.setError("The new email is the same with the current email");
                        } else {
                            String newEmail = emailInput.getText().toString();
                            emailDialog.dismiss();
                            updateProfileButton.setEnabled(false);
                            logOutButton.setEnabled(false);
                            updateCredentialButton.setVisibility(View.INVISIBLE);
                            updateCredentialProgressBar.setVisibility(View.VISIBLE);
                            user.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        updateCredentialButton.setVisibility(View.VISIBLE);
                                        updateCredentialProgressBar.setVisibility(View.INVISIBLE);
                                        updateProfileButton.setEnabled(true);
                                        logOutButton.setEnabled(true);
                                        Toast.makeText(getContext(), "User email address updated.", Toast.LENGTH_SHORT).show();
                                    } else if (!task.isSuccessful()) {
                                        Log.d(TAG, String.valueOf(task.getException()));
                                        Toast.makeText(getContext(), String.valueOf(task.getException()), Toast.LENGTH_SHORT).show();
                                        updateCredentialButton.setVisibility(View.VISIBLE);
                                        updateCredentialProgressBar.setVisibility(View.INVISIBLE);
                                        updateProfileButton.setEnabled(true);
                                        logOutButton.setEnabled(true);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        passwordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                View updateCredentialDialog = getLayoutInflater().inflate(R.layout.dialog_credential_update_password, null);
                mBuilder.setView(updateCredentialDialog);
                final AlertDialog passwordDialog = mBuilder.create();
                passwordDialog.show();

                final TextInputLayout passwordInputLayout = updateCredentialDialog.findViewById(R.id.updatePasswordCredentialDialogPasswordLayout);
                final EditText passwordInput = updateCredentialDialog.findViewById(R.id.updatePasswordCredentialDialogPassword);
                final TextInputLayout confirmPasswordInputLayout = updateCredentialDialog.findViewById(R.id.updatePasswordCredentialDialogConfirmPasswordLayout);
                final EditText confirmPasswordInput = updateCredentialDialog.findViewById(R.id.updatePasswordCredentialDialogConfirmPassword);
                Button updatePasswordButton = updateCredentialDialog.findViewById(R.id.updatePasswordCredentialPassDialogUpdateButton);
                updatePasswordButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (passwordInput.getText().toString().isEmpty() && confirmPasswordInput.getText().toString().isEmpty()) {
                            passwordInputLayout.setError("Please insert your new password");
                            confirmPasswordInputLayout.setError("Please input your new password");
                        } else if (!passwordInput.getText().toString().isEmpty() || !confirmPasswordInput.getText().toString().isEmpty()) {
                            if (passwordInput.getText().toString().isEmpty()) {
                                passwordInputLayout.setError("Please insert your new password");
                            } else if (passwordInput.getText().toString().length() <= 5) {
                                passwordInputLayout.setError("Password minimum is 6 digit");
                            } else {
                                passwordInputLayout.setErrorEnabled(false);
                            }

                            if (confirmPasswordInput.getText().toString().isEmpty()) {
                                confirmPasswordInputLayout.setError("Please insert your new password");
                            } else if (confirmPasswordInput.getText().toString().length() <= 5) {
                                confirmPasswordInputLayout.setError("Password minimum is 6 digit");
                            } else {
                                confirmPasswordInputLayout.setErrorEnabled(false);
                            }

                            if (!passwordInput.getText().toString().equals(confirmPasswordInput.getText().toString())) {
                                confirmPasswordInputLayout.setError("The password is not matching");
                            } else {
                                passwordDialog.dismiss();
                                Util.hideKeyboard(requireActivity());
                                updateProfileButton.setEnabled(false);
                                logOutButton.setEnabled(false);
                                updateCredentialButton.setVisibility(View.INVISIBLE);
                                updateCredentialProgressBar.setVisibility(View.VISIBLE);
                                user.updatePassword(passwordInput.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            updateCredentialButton.setVisibility(View.VISIBLE);
                                            updateCredentialProgressBar.setVisibility(View.INVISIBLE);
                                            updateProfileButton.setEnabled(true);
                                            logOutButton.setEnabled(true);
                                            Toast.makeText(getContext(), "User password updated.", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "User password updated.");
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
    }

    private void initializeComponents() {
        fullNameEditText = rootView.findViewById(R.id.edit_profile_name);
        telephoneNumberEditText = rootView.findViewById(R.id.edit_profile_telephone_number);
        progressBar = rootView.findViewById(R.id.edit_profile_progressbar);
        logOutButton = rootView.findViewById(R.id.edit_profile_log_out);
        updateProfileButton = rootView.findViewById(R.id.edit_profile_confirm);
        totalDonationEditText = rootView.findViewById(R.id.editProfileTotalDonation);

        fullNameLayout = rootView.findViewById(R.id.edit_profile_name_layout);
        telephoneNumberLayout = rootView.findViewById(R.id.edit_profile_telephone_number_layout);

        updateCredentialButton = rootView.findViewById(R.id.donatorProfileUpdateCredentialButton);
        updateCredentialProgressBar = rootView.findViewById(R.id.donatorProfileUpdateCredentialProgressBar);

        // On Focus initialization
        fullNameEditText.setOnFocusChangeListener(this);
        telephoneNumberEditText.setOnFocusChangeListener(this);

        // Hide progress bar
        progressBar.setVisibility(View.INVISIBLE);
        updateCredentialProgressBar.setVisibility(View.INVISIBLE);

        // Disable the total donation field
        totalDonationEditText.setFocusable(false);
        totalDonationEditText.setFocusableInTouchMode(false);
        totalDonationEditText.setCursorVisible(false);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            switch (v.getId()) {
                case R.id.edit_profile_name:
                    fullNameLayout.setErrorEnabled(false);
                    break;
                case R.id.edit_profile_telephone_number:
                    telephoneNumberLayout.setErrorEnabled(false);
                    break;
            }
        }
    }
}
