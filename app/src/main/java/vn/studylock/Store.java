package vn.studylock;
import android.content.*;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Base64;
import java.security.*;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.*;
import java.time.*;
final class Store {
    static final Map<String,String> DEFAULTS=new LinkedHashMap<>();
    static {String[][] a={{"com.google.android.youtube","YouTube"},{"com.google.android.apps.youtube.kids","YouTube Kids"},{"com.zhiliaoapp.musically","TikTok"},{"com.ss.android.ugc.trill","TikTok (quốc tế)"},{"com.zhiliaoapp.musically.go","TikTok Lite"},{"com.facebook.katana","Facebook"},{"com.facebook.lite","Facebook Lite"},{"com.android.chrome","Chrome"},{"com.sec.android.app.sbrowser","Samsung Internet"},{"org.mozilla.firefox","Firefox"},{"com.microsoft.emmx","Microsoft Edge"},{"com.opera.browser","Opera"},{"com.opera.mini.native","Opera Mini"},{"com.brave.browser","Brave"},{"com.android.browser","Trình duyệt Android"},{"com.UCMobile.intl","UC Browser"},{"com.duckduckgo.mobile.android","DuckDuckGo"},{"com.android.vending","CH Play"},{"com.sec.android.app.samsungapps","Galaxy Store"},{"com.huawei.appmarket","AppGallery"},{"com.xiaomi.mipicks","GetApps"},{"com.heytap.market","OPPO App Market"},{"com.bbk.appstore","vivo App Store"}}; for(String[] x:a)DEFAULTS.put(x[0],x[1]);}
    final Context ctx; final SharedPreferences p;
    Store(Context c){ctx=c.getApplicationContext();p=ctx.getSharedPreferences("studylock",Context.MODE_PRIVATE);}
    boolean hasPin(){return p.contains("hash");}
    String schedule(){return p.getString("schedule","08:00-08:30");}
    int profile(){return p.getInt("activeProfile",0);}
    String profileKey(String key){return profile()==0?key:"profile_"+profile()+"_"+key;}
    String profileName(int id){return p.getString("name_"+id,"Trẻ "+(id+1));}
    int profileCount(){return p.getInt("profileCount",1);}
    void selectProfile(int id){if(id<0||id>=profileCount())return;clearBonus();p.edit().putInt("activeProfile",id).apply();}
    void addProfile(String name){int id=profileCount();if(id>=8)return;p.edit().putString("name_"+id,name).putInt("profileCount",id+1).apply();selectProfile(id);}
    void renameProfile(String name){p.edit().putString("name_"+profile(),name).apply();}
    int grade(){return p.getInt(profileKey("grade"),4);}
    int term(){return p.getInt(profileKey("term"),0);}
    int difficulty(){return p.getInt(profileKey("difficulty"),1);}
    boolean adaptive(){return p.getBoolean(profileKey("adaptive"),true);}
    int level(String subject){return adaptive()?p.getInt(profileKey("level_"+subject),difficulty()):difficulty();}
    void adapt(String subject,boolean right){if(!adaptive())return;String key=profileKey("streak_"+subject);int streak=LearningRules.streak(p.getInt(key,0),right);int level=LearningRules.nextLevel(level(subject),streak);p.edit().putInt(profileKey("level_"+subject),level).putInt(key,streak>=3||streak<=-2?0:streak).apply();}
    int tierCount(int minutes){return p.getInt("tier_"+minutes,Math.max(count(),minutes==5?1:minutes==10?2:4));}
    int restMinutes(){return p.getInt("restMinutes",0);}
    long restRemaining(){if(restMinutes()==0||!today().equals(p.getString("restDate",""))||bonus()>0)return 0;return LearningRules.restRemaining(System.currentTimeMillis(),p.getLong("restUntil",0));}
    void saveLearning(int term,int difficulty,boolean adaptive,int[] tiers,int rest,boolean reset){SharedPreferences.Editor e=p.edit().putInt(profileKey("term"),term).putInt(profileKey("difficulty"),difficulty).putBoolean(profileKey("adaptive"),adaptive).putInt("restMinutes",rest);for(int i=0;i<3;i++)e.putInt("tier_"+new int[]{5,10,20}[i],tiers[i]);if(reset)for(String sub:new String[]{"math","english"})e.remove(profileKey("level_"+sub)).remove(profileKey("streak_"+sub));e.apply();}
     int reward(){return p.getInt("reward",10);} int count(){return p.getInt("count",1);} String mode(){return p.getString(profileKey("mode"),"mixed");}
    Set<String> packages(){return new HashSet<>(p.getStringSet("packages",DEFAULTS.keySet()));}
    boolean safePackage(String pkg){
        if(pkg==null||pkg.equals(ctx.getPackageName())||pkg.equals("android")||pkg.equals("com.android.settings")||pkg.equals("com.android.systemui")||pkg.equals("com.android.phone")||pkg.equals("com.android.server.telecom")||pkg.equals("com.android.emergency")||pkg.equals("com.google.android.dialer")||pkg.equals("com.samsung.android.dialer")||pkg.equals("com.android.dialer")||pkg.equals("com.google.android.apps.safetyhub"))return true;
        try{android.telecom.TelecomManager t=(android.telecom.TelecomManager)ctx.getSystemService(Context.TELECOM_SERVICE);if(t!=null&&pkg.equals(t.getDefaultDialerPackage()))return true;}catch(RuntimeException ignored){}
        try{Intent home=new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME);for(android.content.pm.ResolveInfo r:ctx.getPackageManager().queryIntentActivities(home,0))if(pkg.equals(r.activityInfo.packageName))return true;}catch(RuntimeException ignored){}
        return false;
    }
    boolean enabled(){return p.getBoolean("enabled",true);}
    String schedule(int day){return p.getString("schedule_"+day,schedule());}
    String[] week(){String[] out=new String[7];for(int d=0;d<7;d++)out[d]=schedule(d);return out;}
    boolean scheduled(){try{return Rules.weekly(week(),LocalDateTime.now());}catch(IllegalArgumentException ex){return false;}}
    private String cachedKey=""; private LocalDateTime cachedEnd; private boolean cachedUnlimited;
    long scheduledRemaining(){LocalDateTime now=LocalDateTime.now();String[] w=week();String key=Arrays.toString(w)+now.withSecond(0).withNano(0);if(!key.equals(cachedKey)){long ms=Rules.scheduledRemaining(w,now);cachedUnlimited=ms==Long.MAX_VALUE;cachedEnd=cachedUnlimited?now:now.plusNanos(ms*1_000_000);cachedKey=key;}return cachedUnlimited?Long.MAX_VALUE:Math.max(0,Duration.between(now,cachedEnd).toMillis());}
    long sessionRemaining(){return Math.min(dailyRemaining(),Math.max(scheduledRemaining(),bonus()));}
    boolean typedMath(){return p.getBoolean(profileKey("typedMath"),true);}
    boolean warning(){return p.getBoolean("warning",true);}
    void recordAnswer(String subject,boolean right){String k=profileKey("quiz_"+today()+"_"+(subject.equals("Toán")?"math":"english")+"_"+(right?"right":"wrong"));p.edit().putInt(k,p.getInt(k,0)+1).apply();}
    int answers(LocalDate date,String subject,boolean right){return p.getInt(profileKey("quiz_"+date+"_"+subject+"_"+(right?"right":"wrong")),0);}
    long used(LocalDate date){return p.getLong("used_"+date,0);}
    
    int boot(){return Settings.Global.getInt(ctx.getContentResolver(),Settings.Global.BOOT_COUNT,-1);}
    long remaining(){int b=boot();if(b==-1)return 0;return Rules.remaining(SystemClock.elapsedRealtime(),p.getLong("expires",0),b,p.getInt("boot",-2));}
    private static long volatileExpiry=0;
    private static String volatileDate="";
    String today(){return LocalDate.now().toString();}
    int limit(int day){return p.getInt("limit_"+day,p.getInt("dailyLimit",90));}
    int limit(){return limit(LocalDate.now().getDayOfWeek().getValue()-1);}
    long usedToday(){return p.getLong("used_"+today(),0);}
    long dailyRemaining(){return DailyBudget.remaining(limit(),usedToday());}
    boolean exhausted(){return hasPin()&&enabled()&&dailyRemaining()==0;}
    void record(long endWall,long duration){
        if(duration<=0)return;
        SharedPreferences.Editor editor=p.edit();
        for(Map.Entry<String,Long> e:DailyBudget.split(endWall,duration,ZoneId.systemDefault()).entrySet()){
            String key="used_"+e.getKey();
            editor.putLong(key,p.getLong(key,0)+e.getValue());
        }
        editor.apply();
    }
    long bonus(){if(boot()==-1)return today().equals(volatileDate)?Math.max(0,volatileExpiry-SystemClock.elapsedRealtime()):0;return today().equals(p.getString("bonusDate",""))?remaining():0;}
    boolean allowed(){return !hasPin()||!enabled()||(!exhausted()&&(scheduled()||bonus()>0));}
    long grant(int minutes){if(restRemaining()>0)return 0;long duration=DailyBudget.grantMillis(minutes,dailyRemaining());if(duration<=0)return 0;long until=SystemClock.elapsedRealtime()+duration;p.edit().putLong("restUntil",System.currentTimeMillis()+duration+restMinutes()*60000L).putString("restDate",today()).apply();int b=boot();if(b==-1){volatileExpiry=until;volatileDate=today();}else p.edit().putLong("expires",until).putInt("boot",b).putString("bonusDate",today()).apply();return duration;}
    void clearBonus(){volatileExpiry=0;volatileDate="";p.edit().remove("expires").remove("boot").remove("bonusDate").remove("restUntil").remove("restDate").apply();}
    void save(String[] schedules,int grade,int reward,int count,String mode,Set<String> packages,boolean enabled,int[] limits,boolean typed,boolean warning){SharedPreferences.Editor e=p.edit();for(int d=0;d<7;d++){e.putString("schedule_"+d,schedules[d]);e.putInt("limit_"+d,limits[d]);}e.putInt(profileKey("grade"),grade).putInt("reward",reward).putInt("count",count).putString(profileKey("mode"),mode).putStringSet("packages",new HashSet<>(packages)).putBoolean("enabled",enabled).putBoolean(profileKey("typedMath"),typed).putBoolean("warning",warning).apply();clearBonus();}
    private static byte[] hash(String pin,byte[] salt) throws Exception {PBEKeySpec spec=new PBEKeySpec(pin.toCharArray(),salt,120000,256);try{return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();}finally{spec.clearPassword();}}
    boolean setPin(String pin){try{byte[] salt=new byte[16];new SecureRandom().nextBytes(salt);p.edit().putString("salt",Base64.encodeToString(salt,Base64.NO_WRAP)).putString("hash",Base64.encodeToString(hash(pin,salt),Base64.NO_WRAP)).remove("fails").remove("retry").apply();return true;}catch(Exception e){return false;}}
    long waitSeconds(){return Math.max(0,(p.getLong("retry",0)-System.currentTimeMillis()+999)/1000);}
    boolean verify(String pin){if(waitSeconds()>0)return false;try{byte[] salt=Base64.decode(p.getString("salt",""),Base64.DEFAULT), expected=Base64.decode(p.getString("hash",""),Base64.DEFAULT);if(MessageDigest.isEqual(expected,hash(pin,salt))){p.edit().remove("fails").remove("retry").apply();return true;}}catch(Exception ignored){}int f=p.getInt("fails",0)+1;if(f>=5)p.edit().putInt("fails",0).putLong("retry",System.currentTimeMillis()+30000).apply();else p.edit().putInt("fails",f).apply();return false;}
}
