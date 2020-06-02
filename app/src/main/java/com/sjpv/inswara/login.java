package com.sjpv.inswara;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

import static com.google.firebase.auth.PhoneAuthProvider.*;
import static java.util.concurrent.TimeUnit.SECONDS;

public class login extends AppCompatActivity {
    EditText ph;
    EditText otp_txt;
    Button login_btn;
    FirebaseAuth firebaseAuth;
    PhoneAuthProvider fauth;
    public static String TAG="******************************************************************TAG";
    public boolean flag=false;
    String verificationID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_btn=findViewById(R.id.login);
        ph=findViewById(R.id.phone);
        otp_txt=findViewById(R.id.otp);
        Log.d(TAG,"Activity started");
        firebaseAuth=FirebaseAuth.getInstance();
        fauth=PhoneAuthProvider.getInstance();
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Button -> pressed");
                if(flag==false){
                    String ph_no=ph.getText().toString();
                    Log.d(TAG,"button -> press ");
                    if(ph_no.isEmpty() || ph_no.length()==10){
                        ph_no="+91"+ph_no;
                        Log.d(TAG,"phone : "+ ph_no);
                        sendOtp(ph_no);
                    }

                }else{
                    if(otp_txt.getText().length() == 6){
                        String otp_r=otp_txt.getText().toString();
                        Log.d(TAG,"button -> otp : "+otp_r);
                        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationID,otp_r);
                    }
                }
            }
        });
    }

    private void sendOtp(String ph_no) {
        fauth.verifyPhoneNumber(ph_no, 60, SECONDS, this, new OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG,"sendotp -> Verify complete");
                signInWithCredential(phoneAuthCredential);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Log.d(TAG,"sendotp -> code sent");
                login_btn.setText("Verify");
                flag=false;
                verificationID=s;
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.d(TAG,"sendotp -> OTP send failed : "+ e.getMessage());
                Toast.makeText(login.this, "Failed to send OTP.Try later", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void signInWithCredential(PhoneAuthCredential phoneAuthCredential) {
        firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG,"signwithcredential -> task complete");
                if(task.isComplete()){
                    Log.d(TAG,"Sign in success");
                    startActivity(new Intent(getApplicationContext(),VideoView.class));
                }else{
                    Log.d(TAG,"Verification failed");
                    Toast.makeText(login.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
