package com.example.battleshipgame.views;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.battleshipgame.Server;
import com.example.battleshipgame.ui.Ui;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Classroom load-testing tool. Sends up to {@value #MAX_REQUESTS} requests per
 * round to the server at the typed IP. For use only against the server the
 * teacher designates, during class.
 */
public class LoadTesterActivity extends AppCompatActivity {
    private static final int MAX_REQUESTS = 200;
    private EditText etHost, etCount;
    private TextView tvResult;

    interface RequestMaker { Task<?> make(int i); }

    @Override protected void onCreate(Bundle state){
        super.onCreate(state);
        LinearLayout page=Ui.page(this);
        TextView eyebrow=Ui.text(this,"LOAD TESTER",11,Ui.ORANGE);eyebrow.setLetterSpacing(.16f);Ui.spaced(page,eyebrow,24);
        TextView title=Ui.text(this,"כלי בדיקת עומס",28,Ui.INK);title.setTypeface(null,Typeface.BOLD);Ui.spaced(page,title,4);
        Ui.spaced(page,Ui.text(this,"לשימוש מול שרת הכיתה בלבד, בזמן השיעור.",14,Ui.MUTED),4);

        LinearLayout form=Ui.panel(this);Ui.spaced(page,form,24);
        etHost=Ui.field(form,"IP של הצוות המותקף","192.168.1.23",InputType.TYPE_CLASS_TEXT,0);
        etCount=Ui.field(form,"כמה בקשות (עד "+MAX_REQUESTS+")","10",InputType.TYPE_CLASS_NUMBER,12);

        Button login=Ui.button(this,"התחבר לשרת",true);login.setOnClickListener(this::loginToTarget);Ui.spaced(page,login,20);
        Button r1=Ui.button(this,"סבב 1: הצפה",false);r1.setOnClickListener(this::flood);Ui.spaced(page,r1,12);
        Button r2=Ui.button(this,"סבב 2: קלט לא תקין",false);r2.setOnClickListener(this::badInput);Ui.spaced(page,r2,12);
        Button r3=Ui.button(this,"סבב 3: בלי אימות",false);r3.setOnClickListener(this::noAuth);Ui.spaced(page,r3,12);
        Button r4=Ui.button(this,"סבב 4: הרשמה המונית",false);r4.setOnClickListener(this::massSignup);Ui.spaced(page,r4,12);

        tvResult=Ui.text(this,"",16,Ui.INK);tvResult.setGravity(Gravity.CENTER);Ui.spaced(page,tvResult,24);
        setContentView(Ui.scroll(this,page));
    }

    private String host(){ return etHost.getText().toString().trim(); }

    private int count(){
        int n=10;
        try { n=Integer.parseInt(etCount.getText().toString().trim()); } catch (NumberFormatException ignored) { }
        return Math.max(1,Math.min(n,MAX_REQUESTS));
    }

    private boolean needsHost(){
        if(host().isEmpty()){ tvResult.setText("הזן כתובת IP של השרת."); return true; }
        return false;
    }

    private FirebaseFunctions functions(){ return FirebaseFunctions.getInstance(Server.app(this,host())); }

    private Map<String,Object> profile(String name,String bio){
        Map<String,Object> data=new HashMap<>();
        data.put("displayName",name);
        data.put("bio",bio);
        return data;
    }

    // sends n requests at once and reports how many failed and how long it took
    private void runRound(String roundName,RequestMaker maker){
        int n=count();
        AtomicInteger done=new AtomicInteger();
        AtomicInteger failed=new AtomicInteger();
        long start=System.currentTimeMillis();
        tvResult.setText(roundName+": sending "+n+" requests...");
        for(int i=0;i<n;i++){
            maker.make(i).addOnCompleteListener(task->{
                if(!task.isSuccessful()) failed.incrementAndGet();
                if(done.incrementAndGet()==n){
                    long ms=System.currentTimeMillis()-start;
                    tvResult.setText(roundName+": "+n+" requests, "+failed.get()+" failed, "+ms+" ms");
                }
            });
        }
    }

    // creates a test user on the target server and signs in (needed from lesson 3 on)
    public void loginToTarget(View view){
        if(needsHost()) return;
        FirebaseAuth auth=FirebaseAuth.getInstance(Server.app(this,host()));
        auth.createUserWithEmailAndPassword("tester@class.test","123456")
                .addOnCompleteListener(task->{
                    if(task.isSuccessful()){
                        tvResult.setText("signed in as tester");
                    } else {
                        auth.signInWithEmailAndPassword("tester@class.test","123456")
                                .addOnCompleteListener(t2->tvResult.setText(
                                        t2.isSuccessful()?"signed in as tester":"sign in failed"));
                    }
                });
    }

    // round 1: many fast requests from one user
    public void flood(View view){
        if(needsHost()) return;
        FirebaseFunctions fn=functions();
        runRound("flood",i->fn.getHttpsCallable("saveProfile").call(profile("user"+i,"hello")));
    }

    // round 2: empty, huge, missing and wrong-type values
    public void badInput(View view){
        if(needsHost()) return;
        FirebaseFunctions fn=functions();
        runRound("bad input",i->{
            Map<String,Object> data=new HashMap<>();
            switch(i%4){
                case 0: data.put("displayName",""); break;
                case 1: data.put("displayName",new String(new char[50000]).replace('\0','x')); break;
                case 2: data.put("bio","no name at all"); break;
                default: data.put("displayName",12345); break;
            }
            return fn.getHttpsCallable("saveProfile").call(data);
        });
    }

    // round 3: the same call without being signed in
    public void noAuth(View view){
        if(needsHost()) return;
        FirebaseAuth.getInstance(Server.app(this,host())).signOut();
        FirebaseFunctions fn=functions();
        runRound("no auth",i->fn.getHttpsCallable("saveProfile").call(profile("anonymous"+i,"hello")));
    }

    // round 4: many new accounts in a loop
    public void massSignup(View view){
        if(needsHost()) return;
        FirebaseAuth auth=FirebaseAuth.getInstance(Server.app(this,host()));
        long stamp=System.currentTimeMillis();
        runRound("mass signup",i->auth.createUserWithEmailAndPassword(
                "load"+i+"-"+stamp+"@class.test","123456"));
    }
}
