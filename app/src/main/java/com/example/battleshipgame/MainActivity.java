package com.example.battleshipgame;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.battleshipgame.ui.Ui;
import com.example.battleshipgame.views.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    @Override public void onCreate(Bundle state){super.onCreate(state);
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){openLogin();return;}
        String commander=TextUtils.isEmpty(user.getDisplayName())?user.getEmail():user.getDisplayName();
        LinearLayout page=Ui.page(this);page.setGravity(Gravity.CENTER_HORIZONTAL);
        TextView badge=Ui.text(this,"⚓  COMMAND CENTER",12,Ui.CYAN);badge.setLetterSpacing(.15f);Ui.spaced(page,badge,28);
        TextView heading=Ui.text(this,"צוללות טקטי",36,Ui.INK);heading.setTypeface(null,1);Ui.spaced(page,heading,6);
        TextView subtitle=Ui.text(this,"הים שקט. הקרב מתחיל עכשיו.",16,Ui.MUTED);Ui.spaced(page,subtitle,4);
        TextView greeting=Ui.text(this,"ברוך שובך, המפקד "+commander,14,Ui.CYAN);Ui.spaced(page,greeting,10);
        TextView mission=Ui.card(this,"משימה חדשה","הצב את הצי, מצא את האויב והטבע את כל הספינות.");Ui.spaced(page,mission,30);
        Button play=Ui.button(this,"▶  משחק חדש",true);play.setOnClickListener(v->startActivity(new Intent(this,SetupActivity.class)));Ui.spaced(page,play,18);
        Button history=Ui.button(this,"▣  היסטוריית קרבות",false);history.setOnClickListener(v->startActivity(new Intent(this,HistoryActivity.class)));Ui.spaced(page,history,12);
        Button settings=Ui.button(this,"⚙  הגדרות",false);settings.setOnClickListener(v->startActivity(new Intent(this,SettingsActivity.class)));Ui.spaced(page,settings,12);
        TextView signOut=Ui.link(this,"⎋  התנתק");signOut.setOnClickListener(v->{FirebaseAuth.getInstance().signOut();openLogin();});Ui.spaced(page,signOut,16);
        TextView footer=Ui.text(this,"BATTLESHIP // OFFLINE MODE",11,Ui.MUTED);footer.setLetterSpacing(.1f);Ui.spaced(page,footer,18);setContentView(Ui.scroll(this,page));}

    private void openLogin(){startActivity(new Intent(this,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));finish();}
}
