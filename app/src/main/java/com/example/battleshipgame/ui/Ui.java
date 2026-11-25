package com.example.battleshipgame.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

/** Reusable visual language for the naval/tactical interface. */
public final class Ui {
    public static final int NAVY=Color.rgb(5,19,35), PANEL=Color.rgb(13,43,67), PANEL_LIGHT=Color.rgb(20,61,87), CYAN=Color.rgb(47,215,232), INK=Color.rgb(225,246,250), MUTED=Color.rgb(158,194,205), ORANGE=Color.rgb(255,176,65), ERROR=Color.rgb(255,118,118);
    private static final int OUTLINE=Color.rgb(40,100,125);
    private Ui(){}
    public static int dp(Context c,int v){return (int)(v*c.getResources().getDisplayMetrics().density+.5f);}
    public static GradientDrawable shape(int color,float radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(radius);return d;}
    public static LinearLayout page(Context c){LinearLayout p=new LinearLayout(c);p.setOrientation(LinearLayout.VERTICAL);p.setPadding(dp(c,20),dp(c,18),dp(c,20),dp(c,18));GradientDrawable bg=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{Color.rgb(5,18,33),Color.rgb(8,42,65)});p.setBackground(bg);return p;}
    public static TextView text(Context c,String value,float size,int color){TextView t=new TextView(c);t.setText(value);t.setTextSize(size);t.setTextColor(color);t.setGravity(Gravity.CENTER);return t;}
    public static TextView title(Context c,String value,String overline){LinearLayout box=new LinearLayout(c);box.setOrientation(LinearLayout.VERTICAL);box.setGravity(Gravity.CENTER);TextView o=text(c,overline.toUpperCase(),11,CYAN);o.setLetterSpacing(.18f);TextView h=text(c,value,30,INK);h.setTypeface(null,Typeface.BOLD);box.addView(o);box.addView(h);return h;}
    public static TextView card(Context c,String title,String body){TextView t=text(c,title+"\n"+body,17,INK);t.setGravity(Gravity.RIGHT);t.setLineSpacing(dp(c,4),1f);t.setPadding(dp(c,18),dp(c,14),dp(c,18),dp(c,14));GradientDrawable g=shape(PANEL,dp(c,18));g.setStroke(dp(c,1),OUTLINE);t.setBackground(g);return t;}
    public static Button button(Context c,String caption,boolean primary){Button b=new Button(c);b.setText(caption);b.setTextSize(16);b.setTextColor(primary?NAVY:INK);b.setAllCaps(false);b.setGravity(Gravity.CENTER);GradientDrawable bg=shape(primary?CYAN:PANEL_LIGHT,dp(c,16));if(!primary)bg.setStroke(dp(c,1),Color.rgb(68,136,157));b.setBackground(bg);b.setPadding(dp(c,12),dp(c,11),dp(c,12),dp(c,11));return b;}
    public static void spaced(LinearLayout holder,View view,int top){LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.topMargin=dp(holder.getContext(),top);holder.addView(view,lp);}

    /** A rounded panel that groups form fields. */
    public static LinearLayout panel(Context c){
        LinearLayout p=new LinearLayout(c);
        p.setOrientation(LinearLayout.VERTICAL);
        p.setPadding(dp(c,18),dp(c,16),dp(c,18),dp(c,18));
        GradientDrawable g=shape(PANEL,dp(c,20));
        g.setStroke(dp(c,1),OUTLINE);
        p.setBackground(g);
        return p;
    }

    /** Adds a label plus a text input to {@code holder} and returns the input. */
    public static EditText field(LinearLayout holder,String label,String hint,int inputType,int top){
        Context c=holder.getContext();
        TextView l=text(c,label,13,MUTED);
        l.setGravity(Gravity.RIGHT);
        spaced(holder,l,top);
        EditText e=new EditText(c);
        e.setSingleLine(true);
        e.setInputType(inputType);
        e.setTypeface(Typeface.DEFAULT); // password inputs default to monospace
        e.setTextSize(16);
        e.setTextColor(INK);
        e.setHint(hint);
        e.setHintTextColor(Color.argb(120,158,194,205));
        e.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
        e.setPadding(dp(c,14),dp(c,12),dp(c,14),dp(c,12));
        GradientDrawable focused=shape(NAVY,dp(c,14));
        focused.setStroke(dp(c,2),CYAN);
        GradientDrawable idle=shape(NAVY,dp(c,14));
        idle.setStroke(dp(c,1),OUTLINE);
        StateListDrawable bg=new StateListDrawable();
        bg.addState(new int[]{android.R.attr.state_focused},focused);
        bg.addState(new int[]{},idle);
        e.setBackground(bg);
        spaced(holder,e,6);
        return e;
    }

    /** A small text-only action, e.g. "Forgot password?". */
    public static TextView link(Context c,String value){
        TextView t=text(c,value,14,CYAN);
        t.setPadding(dp(c,4),dp(c,8),dp(c,4),dp(c,8));
        t.setClickable(true);
        t.setFocusable(true);
        return t;
    }

    /** A hidden message line for form errors or confirmations. */
    public static TextView status(Context c){
        TextView t=text(c,"",14,ERROR);
        t.setGravity(Gravity.RIGHT);
        t.setVisibility(View.GONE);
        return t;
    }

    public static void showStatus(TextView status,String message,boolean error){
        status.setText(message);
        status.setTextColor(error?ERROR:CYAN);
        status.setVisibility(View.VISIBLE);
    }

    /** Wraps a page so it scrolls and stays clear of the system bars and keyboard. */
    public static ScrollView scroll(Activity a,LinearLayout page){
        WindowCompat.setDecorFitsSystemWindows(a.getWindow(),false);
        WindowInsetsControllerCompat bars=WindowCompat.getInsetsController(a.getWindow(),a.getWindow().getDecorView());
        bars.setAppearanceLightStatusBars(false); // light icons on the dark page
        bars.setAppearanceLightNavigationBars(false);
        ScrollView s=new ScrollView(a);
        s.setFillViewport(true);
        s.setBackgroundColor(NAVY);
        s.addView(page,new ScrollView.LayoutParams(-1,-1));
        int l=page.getPaddingLeft(),t=page.getPaddingTop(),r=page.getPaddingRight(),b=page.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(s,(v,insets)->{
            Insets inset=insets.getInsets(WindowInsetsCompat.Type.systemBars()|WindowInsetsCompat.Type.ime());
            page.setPadding(l+inset.left,t+inset.top,r+inset.right,b+inset.bottom);
            return insets;
        });
        return s;
    }
}
