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

import com.example.fooddonationapplication.model.User;
import com.example.fooddonationapplication.ui.general.LoginActivity;
import com.example.fooddonationapplication.R;
import com.example.fooddonationapplication.util.Util;
import com.example.fooddonationapplication.util.constant.IntentNameExtra;
import com.example.fooddonationapplication.util.constant.RequestCodeConstant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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

    private EditText fullNameEditText, telephoneNumberEditText;
    private TextInputLayout fullNameLayout, telephoneNumberLayout;
    private Button logOutButton, updateProfileButton, updateCredentialButton;
    private ProgressBar progressBar, updateCredentialProgressBar;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private boolean hasChanged;

    private User oldUserData = new User();
    private User newUserData = new User();
    View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile_donator,container,false);

        fullNameEditText = rootView.findViewById(R.id.edit_profile_name);
        telephoneNumberEditText = rootView.findViewById(R.id.edit_profile_telephone_number);
        progressBar = rootView.findViewById(R.id.edit_profile_progressbar);
        logOutButton = rootView.findViewById(R.id.edit_profile_log_out);
        updateProfileButton = rootView.findViewById(R.id.edit_profile_confirm);

        fullNameLayout = rootView.findViewById(R.id.edit_profile_name_layout);
        telephoneNumberLayout = rootView.findViewById(R.id.edit_profile_telephone_number_layout);

        updateCredentialButton = rootView.findViewById(R.id.donatorProfileUpdateCredentialButton);
        updateCredentialProgressBar = rootView.findViewById(R.id.donatorProfileUpdateCredentialProgressBar);

        progressBar.setVisibility(View.INVISIBLE);
        updateCredentialProgressBar.setVisibility(View.INVISIBLE);

        fullNameEditText.setText(user.getDisplayName());
        oldUserData.setName(user.getDisplayName());

        hasChanged = false;

        // Retrieving data from activity
        FragmentActivity fragmentActivity = requireActivity();
        oldUserData.setPhone(fragmentActivity.getIntent().getStringExtra("phone"));
        telephoneNumberEditText.setText(oldUserData.getPhone());
        if (user == null) {
            Toast.makeText(getContext(), "NULL DATA", Toast.LENGTH_SHORT).show();
            // TODO: Alert when data is null
            fragmentActivity.finish();
        }

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    private void checkingChanges() {
        newUserData.setName(fullNameEditText.getText().toString());
        newUserData.setPhone(telephoneNumberEditText.getText().toString());

        hasChanged = !oldUserData.isSameDonator(newUserData);
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
                updateUserDatabase();
            }
        }
    }

    private void getDonatorDocumentList() {
        db.collection("donators").whereEqualTo("uuid", user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> list = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                list.add(document.getId());
                            }
                            Log.d(TAG, list.toString());
                            updateDonatorDatabase((ArrayList) list);
                        }
                    }
                });
    }

    private void updateDonatorDatabase(ArrayList list) {
        WriteBatch batch = db.batch();
        for (int i = 0; i < list.size(); i++) {
            DocumentReference donatorReference = db.collection("donators").document((String) list.get(i));
            batch.update(donatorReference, "name", newUserData.getName());
            batch.update(donatorReference, "phone", newUserData.getPhone());
        }

        DocumentReference userReference = db.collection("users").document(user.getUid());
        batch.update(userReference, "name", newUserData.getName());
        batch.update(userReference, "phone", newUserData.getPhone());

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                updateUserDatabase();
            }
        });
    }

    private void updateUserDatabase() {
        WriteBatch batch = db.batch();
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
                        openChangeCredential();
                    } else {
                        Toast.makeText(getContext(), "Reauthentication failed.", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(getContext(), "Reauthentication failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openChangeCredential() {
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
}
