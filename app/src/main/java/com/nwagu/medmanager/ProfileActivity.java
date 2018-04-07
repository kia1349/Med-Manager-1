package com.nwagu.medmanager;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ProfileActivity extends AppCompatActivity {

    TextView nameText, emailText;
    SimpleDraweeView avatarDraweeView;
    SignInButton signInButton;
    Button signOutButton;

    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameText = (TextView) findViewById(R.id.person_name_text);
        emailText = (TextView) findViewById(R.id.person_email_text);
        avatarDraweeView = (SimpleDraweeView) findViewById(R.id.avatar_view);
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        signOutButton = (Button) findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestEmail()
                .build();

        findViewById(R.id.back_to_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        updateUI();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, Constants.RC_SIGN_IN);
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "signed out", Toast.LENGTH_LONG).show();
                        updateUI();
                    }
                });
    }

    private void updateUI() {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = "Name: " + acct.getDisplayName();
            String personEmail = "Email: " + acct.getEmail();
            Uri personPhoto = acct.getPhotoUrl();

            nameText.setText(personName);
            emailText.setText(personEmail);
            avatarDraweeView.setImageURI(personPhoto);

            signInButton.setVisibility(View.INVISIBLE);
            signOutButton.setVisibility(View.VISIBLE);
        } else {
            nameText.setText(null);
            emailText.setText(null);
            avatarDraweeView.setImageURI(null);

            signInButton.setVisibility(View.VISIBLE);
            signOutButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == Constants.RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            Toast.makeText(getApplicationContext(), "Signed in with " + account.getEmail(), Toast.LENGTH_LONG).show();
            updateUI();
        } catch (ApiException e) {

            String errorMessage = "Sign in failed.";
            switch (e.getStatusCode()) {
                case 17:
                    break;
                case 16:
                    errorMessage = "Sign in was cancelled. Please try again.";
                    break;
                case 13:
                    errorMessage = errorMessage + " That's all we know.";
                    break;
                case 14:
                    errorMessage = "Sign in interrupted. Please try again.";
                    break;
                case 5:
                    errorMessage = "Invalid account name specified.";
                    break;
                case  7:
                    errorMessage = "Network error occurred.";
                    break;
                case 4:
                    errorMessage = "Sign in required.";
                    break;
                default: break;
            }
            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
            updateUI();
        }
    }
}
