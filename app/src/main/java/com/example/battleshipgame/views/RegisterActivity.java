package com.example.battleshipgame.views;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.battleshipgame.MainActivity;
import com.example.battleshipgame.auth.AuthErrors;
import com.example.battleshipgame.ui.Ui;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/** Creates a new email/password account and stores the commander name as the display name. */
public class RegisterActivity extends AppCompatActivity {
    private static final String SUBMIT="צור חשבון  ◀";
    private static final int MIN_PASSWORD=6;
    private FirebaseAuth auth;
    private EditText name,email,password,confirm;
    private TextView status;
    private Button submit;

    @Override protected void onCreate(Bundle state){
        super.onCreate(state);
        auth=FirebaseAuth.getInstance();

        LinearLayout page=Ui.page(this);
        page.setGravity(Gravity.CENTER_HORIZONTAL);
        TextView badge=Ui.text(this,"⚓  NEW RECRUIT",12,Ui.CYAN);
        badge.setLetterSpacing(.15f);
        Ui.spaced(page,badge,28);
        TextView title=Ui.text(this,"הרשמה",32,Ui.INK);
        title.setTypeface(null,Typeface.BOLD);
        Ui.spaced(page,title,6);
        Ui.spaced(page,Ui.text(this,"צור חשבון והצטרף לצי.",15,Ui.MUTED),4);

        LinearLayout form=Ui.panel(this);
        Ui.spaced(page,form,30);
        name=Ui.field(form,"שם מפקד","איך לקרוא לך?",InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PERSON_NAME|InputType.TYPE_TEXT_FLAG_CAP_WORDS,0);
        email=Ui.field(form,"אימייל","name@example.com",InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,14);
        password=Ui.field(form,"סיסמה","לפחות "+MIN_PASSWORD+" תווים",InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD,14);
        confirm=Ui.field(form,"אימות סיסמה","הקלד שוב את הסיסמה",InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD,14);
        confirm.setImeOptions(EditorInfo.IME_ACTION_DONE);
        confirm.setOnEditorActionListener((v,id,ev)->{if(id==EditorInfo.IME_ACTION_DONE){register();return true;}return false;});
        status=Ui.status(this);
        Ui.spaced(form,status,10);

        submit=Ui.button(this,SUBMIT,true);
        submit.setOnClickListener(v->register());
        Ui.spaced(page,submit,22);

        TextView back=Ui.link(this,"כבר יש לך חשבון?  התחבר");
        back.setOnClickListener(v->finish());
        Ui.spaced(page,back,18);
        setContentView(Ui.scroll(this,page));
    }

    private void register(){
        String commander=name.getText().toString().trim();
        String mail=email.getText().toString().trim();
        String pass=password.getText().toString();
        if(commander.isEmpty()){Ui.showStatus(status,"בחר שם מפקד.",true);name.requestFocus();return;}
        if(!Patterns.EMAIL_ADDRESS.matcher(mail).matches()){Ui.showStatus(status,"הזן כתובת אימייל תקינה.",true);email.requestFocus();return;}
        if(pass.length()<MIN_PASSWORD){Ui.showStatus(status,"הסיסמה צריכה להכיל לפחות "+MIN_PASSWORD+" תווים.",true);password.requestFocus();return;}
        if(!pass.equals(confirm.getText().toString())){Ui.showStatus(status,"הסיסמאות אינן תואמות.",true);confirm.requestFocus();return;}
        setBusy(true);
        auth.createUserWithEmailAndPassword(mail,pass).addOnCompleteListener(this,task->{
            if(!task.isSuccessful()){
                setBusy(false);
                Ui.showStatus(status,AuthErrors.message(task.getException()),true);
                return;
            }
            FirebaseUser user=task.getResult().getUser();
            UserProfileChangeRequest profile=new UserProfileChangeRequest.Builder().setDisplayName(commander).build();
            // The account exists either way; a failed name update shouldn't block entering the app.
            user.updateProfile(profile).addOnCompleteListener(this,t->openMain());
        });
    }

    private void setBusy(boolean busy){
        submit.setEnabled(!busy);
        submit.setAlpha(busy?.6f:1f);
        submit.setText(busy?"יוצר חשבון…":SUBMIT);
    }

    private void openMain(){
        startActivity(new Intent(this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }
}
