package com.example.battleshipgame;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.battleshipgame.ui.Ui;
import com.example.battleshipgame.views.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText etName, etBio;

    @Override public void onCreate(Bundle state){super.onCreate(state);
        auth=Server.auth(this); // talk to the local Firebase emulator
        FirebaseUser user=auth.getCurrentUser();
        if(user==null){openLogin();return;}
        String commander=TextUtils.isEmpty(user.getDisplayName())?user.getEmail():user.getDisplayName();
        LinearLayout page=Ui.page(this);page.setGravity(Gravity.CENTER_HORIZONTAL);
        TextView badge=Ui.text(this,"⚓  COMMAND CENTER",12,Ui.CYAN);badge.setLetterSpacing(.15f);Ui.spaced(page,badge,28);
        TextView heading=Ui.text(this,"צוללות טקטי",36,Ui.INK);heading.setTypeface(null,Typeface.BOLD);Ui.spaced(page,heading,6);
        TextView subtitle=Ui.text(this,"הים שקט. הקרב מתחיל עכשיו.",16,Ui.MUTED);Ui.spaced(page,subtitle,4);
        TextView greeting=Ui.text(this,"ברוך שובך, המפקד "+commander,14,Ui.CYAN);Ui.spaced(page,greeting,10);
        TextView mission=Ui.card(this,"משימה חדשה","הצב את הצי, מצא את האויב והטבע את כל הספינות.");Ui.spaced(page,mission,30);
        Button play=Ui.button(this,"▶  משחק חדש",true);play.setOnClickListener(v->startActivity(new Intent(this,SetupActivity.class)));Ui.spaced(page,play,18);
        Button history=Ui.button(this,"▣  היסטוריית קרבות",false);history.setOnClickListener(v->startActivity(new Intent(this,HistoryActivity.class)));Ui.spaced(page,history,12);
        Button settings=Ui.button(this,"⚙  הגדרות",false);settings.setOnClickListener(v->startActivity(new Intent(this,SettingsActivity.class)));Ui.spaced(page,settings,12);

        // --- Server profile (saved via the saveProfile Cloud Function) ---
        TextView profileEyebrow=Ui.text(this,"פרופיל שרת",11,Ui.CYAN);profileEyebrow.setLetterSpacing(.16f);Ui.spaced(page,profileEyebrow,28);
        LinearLayout profile=Ui.panel(this);Ui.spaced(page,profile,10);
        etName=Ui.field(profile,"שם תצוגה","איך לקרוא לך?",InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_WORDS,0);
        etBio=Ui.field(profile,"תיאור קצר","כמה מילים עליך",InputType.TYPE_CLASS_TEXT,12);
        Button save=Ui.button(this,"💾  שמירת פרופיל",false);save.setOnClickListener(this::saveProfile);Ui.spaced(profile,save,14);

        Button loadTester=Ui.button(this,"⚠  כלי בדיקת עומס",false);loadTester.setOnClickListener(v->startActivity(new Intent(this,LoadTesterActivity.class)));Ui.spaced(page,loadTester,18);
        TextView signOut=Ui.link(this,"⎋  התנתק");signOut.setOnClickListener(v->{auth.signOut();openLogin();});Ui.spaced(page,signOut,16);
        TextView footer=Ui.text(this,"BATTLESHIP // OFFLINE MODE",11,Ui.MUTED);footer.setLetterSpacing(.1f);Ui.spaced(page,footer,18);setContentView(Ui.scroll(this,page));}

    /** Sends the profile to the server's saveProfile function and reports the result. */
    public void saveProfile(View view){
        FirebaseFunctions functions=Server.functions(this);
        Map<String,Object> data=new HashMap<>();
        data.put("displayName",etName.getText().toString());
        data.put("bio",etBio.getText().toString());
        functions.getHttpsCallable("saveProfile").call(data)
                .addOnSuccessListener(result->Toast.makeText(this,"נשמר!",Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e->{
                    String msg="שגיאה: "+e.getMessage();
                    if(e instanceof FirebaseFunctionsException){
                        FirebaseFunctionsException fe=(FirebaseFunctionsException)e;
                        if(fe.getCode()==FirebaseFunctionsException.Code.RESOURCE_EXHAUSTED) msg="יותר מדי בקשות, נסו שוב בעוד דקה";
                        else if(fe.getCode()==FirebaseFunctionsException.Code.UNAUTHENTICATED) msg="צריך להתחבר קודם";
                        else if(fe.getCode()==FirebaseFunctionsException.Code.INVALID_ARGUMENT) msg="הקלט לא תקין";
                    }
                    Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
                });
    }

    private void openLogin(){startActivity(new Intent(this,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));finish();}
}
