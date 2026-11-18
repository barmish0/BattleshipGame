package com.example.battleshipgame.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

/** Reusable visual language for the naval/tactical interface. */
public final class Ui {
    public static final int NAVY=Color.rgb(5,19,35), PANEL=Color.rgb(13,43,67), PANEL_LIGHT=Color.rgb(20,61,87), CYAN=Color.rgb(47,215,232), INK=Color.rgb(225,246,250), MUTED=Color.rgb(158,194,205), ORANGE=Color.rgb(255,176,65);
    private Ui(){}
    public static int dp(Context c,int v){return (int)(v*c.getResources().getDisplayMetrics().density+.5f);}
    public static GradientDrawable shape(int color,float radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(radius);return d;}
    public static LinearLayout page(Context c){LinearLayout p=new LinearLayout(c);p.setOrientation(LinearLayout.VERTICAL);p.setPadding(dp(c,20),dp(c,18),dp(c,20),dp(c,18));GradientDrawable bg=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{Color.rgb(5,18,33),Color.rgb(8,42,65)});p.setBackground(bg);return p;}
    public static TextView text(Context c,String value,float size,int color){TextView t=new TextView(c);t.setText(value);t.setTextSize(size);t.setTextColor(color);t.setGravity(Gravity.CENTER);return t;}
    public static TextView title(Context c,String value,String overline){LinearLayout box=new LinearLayout(c);box.setOrientation(LinearLayout.VERTICAL);box.setGravity(Gravity.CENTER);TextView o=text(c,overline.toUpperCase(),11,CYAN);o.setLetterSpacing(.18f);TextView h=text(c,value,30,INK);h.setTypeface(null,1);box.addView(o);box.addView(h);return h;}
    public static TextView card(Context c,String title,String body){TextView t=text(c,title+"\n"+body,17,INK);t.setGravity(Gravity.RIGHT);t.setLineSpacing(dp(c,4),1f);t.setPadding(dp(c,18),dp(c,14),dp(c,18),dp(c,14));GradientDrawable g=shape(PANEL,dp(c,18));g.setStroke(dp(c,1),Color.rgb(40,100,125));t.setBackground(g);return t;}
    public static Button button(Context c,String caption,boolean primary){Button b=new Button(c);b.setText(caption);b.setTextSize(16);b.setTextColor(primary?NAVY:INK);b.setAllCaps(false);b.setGravity(Gravity.CENTER);GradientDrawable bg=shape(primary?CYAN:PANEL_LIGHT,dp(c,16));if(!primary)bg.setStroke(dp(c,1),Color.rgb(68,136,157));b.setBackground(bg);b.setPadding(dp(c,12),dp(c,11),dp(c,12),dp(c,11));return b;}
    public static void spaced(LinearLayout holder,View view,int top){LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.topMargin=dp(holder.getContext(),top);holder.addView(view,lp);}
}
