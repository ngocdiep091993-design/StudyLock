package vn.studylock;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
final class Ui {
    static final int BG=0xfff5f7f5, INK=0xff172b29, MUTED=0xff526763, TEAL=0xff176b64, WHITE=Color.WHITE, RED=0xff9b312d;
    static int dp(Context c,int x){return (int)(x*c.getResources().getDisplayMetrics().density+.5f);}
    static GradientDrawable bg(Context c,int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(c,radius));return d;}
    static LinearLayout column(Context c){LinearLayout l=new LinearLayout(c);l.setOrientation(LinearLayout.VERTICAL);l.setPadding(dp(c,20),dp(c,20),dp(c,20),dp(c,20));return l;}
    static TextView text(Context c,String s,int size,int color,boolean bold){TextView t=new TextView(c);t.setText(s);t.setTextSize(size);t.setTextColor(color);if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);t.setPadding(0,dp(c,5),0,dp(c,5));return t;}
    static Button button(Context c,String s,boolean primary){Button b=new Button(c);b.setText(s);b.setAllCaps(false);b.setTextSize(16);b.setTextColor(primary?WHITE:INK);b.setBackground(bg(c,primary?TEAL:0xffe3ece8,12));b.setMinHeight(dp(c,48));b.setPadding(dp(c,12),dp(c,8),dp(c,12),dp(c,8));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(0,dp(c,6),0,dp(c,6));b.setLayoutParams(p);return b;}
    static void space(LinearLayout l,int d){View v=new View(l.getContext());l.addView(v,new LinearLayout.LayoutParams(1,dp(l.getContext(),d)));}
}
