package com.max.def.firebasetutorial;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rilixtech.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity
{
    private LinearLayout authPhone;
    private LinearLayout authOTP;

    private CountryCodePicker ccp;

    private AppCompatEditText phoneNumber;
    private AppCompatEditText otpCode;

    private AppCompatButton sendOTP;
    private AppCompatButton verifyOTP;
    private AppCompatButton resendOTP;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private PhoneAuthProvider.ForceResendingToken token;

    private String verificationId;

    private FirebaseAuth firebaseAuth;

    private String mobileNumber;
    private String phoneCode;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        authPhone = findViewById(R.id.auth_phone);
        authOTP = findViewById(R.id.auth_otp);

        ccp = findViewById(R.id.ccp);

        phoneNumber = findViewById(R.id.number);
        otpCode = findViewById(R.id.otp);

        sendOTP = findViewById(R.id.send_otp);
        verifyOTP = findViewById(R.id.verify_otp);
        resendOTP = findViewById(R.id.resent_otp);

        // set default country of user on edittext and country code picker

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        String defaultCountry = telephonyManager != null ? telephonyManager.getSimCountryIso() : null;

        ccp.setDefaultCountryUsingNameCode(defaultCountry);
        ccp.setCountryPreference(defaultCountry);
        ccp.setCountryForNameCode(defaultCountry);

        ccp.registerPhoneNumberTextView(phoneNumber);

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks()
        {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
            {
                Toast.makeText(LoginActivity.this, "Verification Completed", Toast.LENGTH_SHORT).show();

                signInUser();
            }

            @Override
            public void onVerificationFailed(FirebaseException e)
            {

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken)
            {
                super.onCodeSent(s, forceResendingToken);

                verificationId = s;
                token = forceResendingToken;

                authPhone.setVisibility(View.GONE);

                authOTP.setVisibility(View.VISIBLE);
            }
        };

        sendOTP.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String number = phoneNumber.getText().toString();

                if (TextUtils.isEmpty(number))
                {
                    Toast.makeText(LoginActivity.this, "Please Enter your mobile number", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    phoneCode = ccp.getSelectedCountryCodeWithPlus();

                    mobileNumber = number.replaceAll("\\s+",""); // to remove all spaces from the mobile number

                    sendOTPToMobile(phoneCode,mobileNumber);
                }
            }
        });

        verifyOTP.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String otp = otpCode.getText().toString();

                if (TextUtils.isEmpty(otp))
                {
                    Toast.makeText(LoginActivity.this, "Enter the otp", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,otp);

                    signInWithPhoneNumber(credential);
                }
            }
        });

        resendOTP.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneCode+phoneNumber,60,TimeUnit.SECONDS,LoginActivity.this,callbacks,token);
            }
        });

    }

    private void signInWithPhoneNumber(PhoneAuthCredential credential)
    {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                signInUser();
            }
        });
    }

    private void signInUser()
    {
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null)
        {
            signInUser();
        }
    }

    private void sendOTPToMobile(String phoneCode, String mobileNumber)
    {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneCode + mobileNumber,60, TimeUnit.SECONDS,LoginActivity.this,callbacks);
    }
}
