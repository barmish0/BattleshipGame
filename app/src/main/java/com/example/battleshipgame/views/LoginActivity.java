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

/** Entry screen: signs in with email and password, or sends the user to registration. */
public class LoginActivity extends AppCompatActivity {
    private static final String SUBMIT="התחבר  ◀";
    private FirebaseAuth auth;
    private EditText email,password;
    private TextView status;
    private Button submit;

    @Override protected void onCreate(Bundle state){
        super.onCreate(state);
        auth=FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null){openMain();return;}

        LinearLayout page=Ui.page(this);
        page.setGravity(Gravity.CENTER_HORIZONTAL);
        TextView badge=Ui.text(this,"⚓  SECURE ACCESS",12,Ui.CYAN);
        badge.setLetterSpacing(.15f);
        Ui.spaced(page,badge,28);
        TextView title=Ui.text(this,"התחברות",32,Ui.INK);
        title.setTypeface(null,Typeface.BOLD);
        Ui.spaced(page,title,6);
        Ui.spaced(page,Ui.text(this,"הזדהה כדי לחזור לחדר הפיקוד.",15,Ui.MUTED),4);

        LinearLayout form=Ui.panel(this);
        Ui.spaced(page,form,30);
        email=Ui.field(form,"אימייל","name@example.com",InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,0);
        password=Ui.field(form,"סיסמה","הסיסמה שלך",InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD,14);
        password.setImeOptions(EditorInfo.IME_ACTION_DONE);
        password.setOnEditorActionListener((v,id,ev)->{if(id==EditorInfo.IME_ACTION_DONE){login();return true;}return false;});
        TextView forgot=Ui.link(this,"שכחת סיסמה?");
        forgot.setGravity(Gravity.LEFT);
        forgot.setOnClickListener(v->resetPassword());
        Ui.spaced(form,forgot,4);
        status=Ui.status(this);
        Ui.spaced(form,status,2);

        submit=Ui.button(this,SUBMIT,true);
        submit.setOnClickListener(v->login());
        Ui.spaced(page,submit,22);

        Ui.spaced(page,Ui.text(this,"עדיין אין לך חשבון?",14,Ui.MUTED),28);
        Button register=Ui.button(this,"✚  יצירת חשבון חדש",false);
        register.setOnClickListener(v->startActivity(new Intent(this,RegisterActivity.class)));
        Ui.spaced(page,register,10);

        TextView footer=Ui.text(this,"BATTLESHIP // FIREBASE AUTH",11,Ui.MUTED);
        footer.setLetterSpacing(.1f);
        Ui.spaced(page,footer,30);
        setContentView(Ui.scroll(this,page));
    }

    private void login(){
        String mail=email.getText().toString().trim();
        String pass=password.getText().toString();
        if(!Patterns.EMAIL_ADDRESS.matcher(mail).matches()){Ui.showStatus(status,"הזן כתובת אימייל תקינה.",true);email.requestFocus();return;}
        if(pass.isEmpty()){Ui.showStatus(status,"הזן סיסמה.",true);password.requestFocus();return;}
        setBusy(true);
        auth.signInWithEmailAndPassword(mail,pass).addOnCompleteListener(this,task->{
            if(task.isSuccessful()){openMain();return;}
            setBusy(false);
            Ui.showStatus(status,AuthErrors.message(task.getException()),true);
        });
    }

    private void resetPassword(){
        String mail=email.getText().toString().trim();
        if(!Patterns.EMAIL_ADDRESS.matcher(mail).matches()){Ui.showStatus(status,"הזן את האימייל שלך למעלה, ואז לחץ שוב על \"שכחת סיסמה?\".",true);email.requestFocus();return;}
        auth.sendPasswordResetEmail(mail).addOnCompleteListener(this,task->{
            if(task.isSuccessful()) Ui.showStatus(status,"אם קיים חשבון עם הכתובת הזו, נשלח אליה קישור לאיפוס סיסמה.",false);
            else Ui.showStatus(status,AuthErrors.message(task.getException()),true);
        });
    }

    private void setBusy(boolean busy){
        submit.setEnabled(!busy);
        submit.setAlpha(busy?.6f:1f);
        submit.setText(busy?"מתחבר…":SUBMIT);
    }

    private void openMain(){
        startActivity(new Intent(this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }
}
