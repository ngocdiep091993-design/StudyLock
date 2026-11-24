package vn.studylock;
import android.accessibilityservice.AccessibilityService;
import android.content.*;
import android.graphics.PixelFormat;
import android.os.*;
import android.view.*;
import android.view.accessibility.AccessibilityEvent;
import android.widget.*;
import java.util.*;
public class LockService extends AccessibilityService {
    private Store store; private WindowManager wm; private View overlay; private String foreground=""; private boolean leaving=false;
    private long lastSample=-1; private boolean warned=false; private boolean charging=false, capOverlay=false;
    private final Curriculum curriculum=new Curriculum(new Random()); private final Random random=new Random(); private Questions.Q question; private int correct=0, selectedTier=0, questionLevel=1; private boolean restOverlay=false; private TextView restText;
    private Questions.Q nextQuestion(){String mode=store.mode();String subject=mode.equals("mixed")?(random.nextBoolean()?"math":"english"):mode;questionLevel=store.level(subject);return curriculum.next(store.grade(),store.term(),subject,questionLevel);}
    private int tier(){return selectedTier==0?store.reward():selectedTier;}
    private int required(){return store.tierCount(tier());}
    private final Handler handler=new Handler(Looper.getMainLooper());
    private final Runnable tick=new Runnable(){public void run(){evaluate();handler.postDelayed(this,500);}};
    private final SharedPreferences.OnSharedPreferenceChangeListener changed=(p,k)->{if(k!=null&&(k.startsWith("schedule_")||k.startsWith("limit_")||k.startsWith("tier_")||k.equals("activeProfile")||k.equals("restMinutes")||k.matches("(?:profile_[0-9]+_)?(?:grade|term|difficulty|adaptive|mode|typedMath)")||Arrays.asList("schedule","grade","reward","count","mode","packages","enabled","dailyLimit","typedMath","warning").contains(k))){resetQuiz();handler.post(this::evaluate);}};
    private final BroadcastReceiver screen=new BroadcastReceiver(){@Override public void onReceive(Context c,Intent i){if(Intent.ACTION_SCREEN_OFF.equals(i.getAction())){settle();charging=false;foreground="";leaving=false;hide();resetQuiz();}}};
    private boolean receiverRegistered=false;
    @Override protected void onServiceConnected(){super.onServiceConnected();settle();lastSample=-1;charging=false;if(store!=null)store.p.unregisterOnSharedPreferenceChangeListener(changed);store=new Store(this);wm=(WindowManager)getSystemService(WINDOW_SERVICE);store.p.registerOnSharedPreferenceChangeListener(changed);if(!receiverRegistered){IntentFilter f=new IntentFilter(Intent.ACTION_SCREEN_OFF);if(Build.VERSION.SDK_INT>=33)registerReceiver(screen,f,Context.RECEIVER_NOT_EXPORTED);else registerReceiver(screen,f);receiverRegistered=true;}handler.removeCallbacks(tick);handler.post(tick);}
    @Override public void onAccessibilityEvent(AccessibilityEvent event){if(store==null||event.getEventType()!=AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED||event.getPackageName()==null)return;
        String pkg=event.getPackageName().toString();String cls=event.getClassName()==null?"":event.getClassName().toString();
        // Ignore our overlay's events. An actual opening of MainActivity is not restricted.
        if(pkg.equals(getPackageName())){if(cls.equals(MainActivity.class.getName())){settle();charging=false;foreground=pkg;leaving=false;hide();}return;}
        if(leaving&&pkg.equals(foreground))return;settle();leaving=false;foreground=pkg;evaluate();}
    private void settle(){long now=SystemClock.elapsedRealtime();if(store!=null&&charging&&lastSample>=0){store.record(System.currentTimeMillis(),DailyBudget.observedMillis(lastSample,now));}lastSample=now;}
    private void evaluate(){if(store==null)return;settle();charging=false;if(leaving)return;PowerManager power=(PowerManager)getSystemService(POWER_SERVICE);android.app.KeyguardManager key=(android.app.KeyguardManager)getSystemService(KEYGUARD_SERVICE);if(!power.isInteractive()||key.isKeyguardLocked()){hide();return;}
        boolean target=!foreground.isEmpty()&&store.packages().contains(foreground)&&!store.safePackage(foreground);
        boolean allowed=store.allowed();
        if(!target||allowed){hide();if(allowed)resetQuiz();charging=target&&allowed&&store.hasPin()&&store.enabled();
        if(charging){long left=store.sessionRemaining();if(left>60000)warned=false;else if(left>0&&store.warning()&&!warned){warned=true;Toast.makeText(this,"StudyLock: còn tối đa "+((left+999)/1000)+" giây giải trí.",Toast.LENGTH_LONG).show();}}return;}
        warned=false; boolean capped=store.exhausted();
        boolean resting=!capped&&store.restRemaining()>0;if(overlay!=null&&(capOverlay!=capped||restOverlay!=resting))resetQuiz();if(resting&&restText!=null)restText.setText("Còn "+((store.restRemaining()+999)/1000)+" giây. Hãy nghỉ mắt và vận động nhẹ.");
        if(overlay==null)show();}
    private void resetQuiz(){correct=0;selectedTier=0;question=null;if(overlay!=null)hide();}
    private void show(){capOverlay=store.exhausted();restOverlay=!capOverlay&&store.restRemaining()>0;boolean blocked=capOverlay||restOverlay;if(!blocked&&question==null)question=nextQuestion();
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.setBackgroundColor(Ui.BG);LinearLayout l=Ui.column(this);l.setGravity(Gravity.CENTER_VERTICAL);scroll.addView(l,new ScrollView.LayoutParams(-1,-2));
        l.addView(Ui.text(this,"STUDYLOCK  •  HỌC ĐỂ MỞ KHÓA",13,Ui.TEAL,true));l.addView(Ui.text(this,capOverlay?"Đã hết thời gian hôm nay":restOverlay?"Đến giờ nghỉ":"Đến giờ ôn bài!",30,Ui.INK,true));l.addView(Ui.text(this,capOverlay?"Đã đạt giới hạn "+store.limit()+" phút/ngày. Không thể làm bài để mở khóa thêm. Hạn mức mới bắt đầu lúc 00:00.":restOverlay?"Hết thời gian thưởng. Nghỉ trước khi làm bài tiếp theo.":"Ứng dụng tạm khóa. Chọn mức thưởng rồi trả lời đúng liên tiếp. Thời gian không vượt hạn mức còn lại hôm nay.",17,Ui.MUTED,false));Ui.space(l,16);if(restOverlay){restText=Ui.text(this,"Còn "+((store.restRemaining()+999)/1000)+" giây",18,Ui.TEAL,true);l.addView(restText);}
        TextView meta=Ui.text(this,"",14,Ui.TEAL,true),prompt=Ui.text(this,"",22,Ui.INK,true),feedback=Ui.text(this,"",15,Ui.RED,true);LinearLayout answers=new LinearLayout(this);answers.setOrientation(LinearLayout.VERTICAL);if(!blocked){l.addView(Ui.text(this,"Hồ sơ: "+store.profileName(store.profile())+" • Chọn mức thưởng",14,Ui.MUTED,false));LinearLayout choices=new LinearLayout(this);choices.setOrientation(LinearLayout.VERTICAL);l.addView(choices);l.addView(meta);l.addView(prompt);
        for(int m:new int[]{5,10,20}){Button b=Ui.button(this,m+" phút / "+store.tierCount(m)+" câu đúng",m==tier());choices.addView(b);b.setOnClickListener(v->{if(question==null||overlay==null)return;if(tier()!=m){selectedTier=m;correct=0;question=nextQuestion();for(int i=0;i<choices.getChildCount();i++)choices.getChildAt(i).setAlpha(new int[]{5,10,20}[i]==m?1f:0.65f);feedback.setText("Đã chọn "+m+" phút.");renderAgain(meta,prompt,answers,feedback);}});}}
        if(!blocked){l.addView(answers);l.addView(feedback);}Ui.space(l,8);
        Button exit=Ui.button(this,"Về màn hình chính",false);exit.setOnClickListener(v->{leaving=true;boolean ok=performGlobalAction(GLOBAL_ACTION_HOME);if(ok){hide();handler.postDelayed(()->{if(leaving){leaving=false;evaluate();}},1000);}else{leaving=false;}});l.addView(exit);
        if(!blocked)renderAgain(meta,prompt,answers,feedback);WindowManager.LayoutParams lp=new WindowManager.LayoutParams(-1,-1,WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,WindowManager.LayoutParams.FLAG_SECURE|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,PixelFormat.OPAQUE);lp.gravity=Gravity.TOP|Gravity.START;
        scroll.setOnApplyWindowInsetsListener((v,insets)->{if(Build.VERSION.SDK_INT>=30){android.graphics.Insets i=insets.getInsets(WindowInsets.Type.systemBars()|WindowInsets.Type.displayCutout());v.setPadding(i.left,i.top,i.right,i.bottom);}else v.setPadding(insets.getSystemWindowInsetLeft(),insets.getSystemWindowInsetTop(),insets.getSystemWindowInsetRight(),insets.getSystemWindowInsetBottom());return insets;});
        try{wm.addView(scroll,lp);overlay=scroll;scroll.requestApplyInsets();}catch(RuntimeException e){overlay=null;android.util.Log.w("StudyLock","Could not display lock overlay",e);}}
    private void renderAgain(TextView meta,TextView prompt,LinearLayout answers,TextView feedback){
        meta.setText("LỚP "+store.grade()+"  /  "+question.subject+"  /  MỨC "+questionLevel+"  /  CÂU "+(correct+1)+"/"+required()+"  /  "+tier()+" phút");prompt.setText(question.prompt);answers.removeAllViews();
        final Questions.Q shown=question;
        if(shown.subject.equals("Toán")&&store.typedMath()){
            // Internal keypad avoids losing foreground detection to an external keyboard.
            TextView entry=Ui.text(this,"",26,Ui.INK,true);entry.setHint("Nhập kết quả");entry.setBackground(Ui.bg(this,Ui.WHITE,8));entry.setPadding(12,8,12,8);answers.addView(entry);
            for(String[] keys:new String[][]{{"7","8","9","Xóa"},{"4","5","6","−"},{"1","2","3","/"},{"0",",","Xóa hết"}}){LinearLayout row=new LinearLayout(this);row.setOrientation(LinearLayout.HORIZONTAL);for(String k:keys){Button button=Ui.button(this,k,false);row.addView(button,new LinearLayout.LayoutParams(0,-2,1));button.setOnClickListener(v->{String x=entry.getText().toString();if(k.equals("Xóa hết"))entry.setText("");else if(k.equals("Xóa")){if(!x.isEmpty())entry.setText(x.substring(0,x.length()-1));}else if(x.length()<32)entry.setText(x+k);});}answers.addView(row);}
            Button submit=Ui.button(this,"Kiểm tra đáp án",true);answers.addView(submit);submit.setOnClickListener(v->{String x=entry.getText().toString();if(x.isEmpty()){feedback.setTextColor(Ui.RED);feedback.setText("Nhập kết quả trước khi kiểm tra.");return;}submitAnswer(shown,MathAnswer.correct(shown.prompt,shown.answer,x),meta,prompt,answers,feedback);});
        }else for(String choice:shown.choices){Button b=Ui.button(this,choice,false);answers.addView(b);b.setOnClickListener(v->submitAnswer(shown,shown.answer.equals(choice),meta,prompt,answers,feedback));}
    }
    private void submitAnswer(Questions.Q shown,boolean right,TextView meta,TextView prompt,LinearLayout answers,TextView feedback){
        if(question!=shown||overlay==null)return;
        if(store.allowed()){hide();resetQuiz();return;}if(store.exhausted()||store.restRemaining()>0){resetQuiz();evaluate();return;}
        store.recordAnswer(shown.subject,right);store.adapt(shown.subject.equals("Toán")?"math":"english",right);
        if(right){correct++;if(correct>=required()){long grant=store.grant(tier());warned=false;hide();resetQuiz();if(grant>0)Toast.makeText(this,"Chính xác! Thêm tối đa "+((grant+999)/1000)+" giây.",Toast.LENGTH_SHORT).show();evaluate();return;}feedback.setTextColor(Ui.TEAL);feedback.setText("Chính xác! Tiếp tục câu tiếp theo.");}
        else{correct=0;feedback.setTextColor(Ui.RED);feedback.setText("Chưa đúng. Đáp án: "+shown.answer+". Hãy thử câu mới.");}
        question=nextQuestion();renderAgain(meta,prompt,answers,feedback);
    }
    private void hide(){restText=null;if(overlay!=null){try{wm.removeViewImmediate(overlay);}catch(RuntimeException ignored){}overlay=null;}}
    @Override public void onInterrupt(){settle();charging=false;hide();}
    @Override public boolean onUnbind(Intent intent){settle();charging=false;handler.removeCallbacksAndMessages(null);hide();return super.onUnbind(intent);}
    @Override public void onDestroy(){settle();charging=false;handler.removeCallbacksAndMessages(null);hide();if(store!=null)store.p.unregisterOnSharedPreferenceChangeListener(changed);if(receiverRegistered){unregisterReceiver(screen);receiverRegistered=false;}super.onDestroy();}
}
