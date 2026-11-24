package vn.studylock;
import java.util.*;
import java.time.*;
public final class Rules {
    private Rules() {}
    public static int minute(String s) {
        if (!s.matches("[0-9]{2}:[0-9]{2}")) throw new IllegalArgumentException("Giờ phải có dạng HH:mm");
        int h=Integer.parseInt(s.substring(0,2)), m=Integer.parseInt(s.substring(3));
        if(h>23||m>59) throw new IllegalArgumentException("Giờ không hợp lệ: "+s);
        return h*60+m;
    }
    public static List<int[]> parse(String input) {
        List<int[]> windows=new ArrayList<>();
        for(String line: input.split("\\R")) {
            if(line.trim().isEmpty()) continue;
            String[] p=line.trim().split("-",-1);
            if(p.length!=2) throw new IllegalArgumentException("Dùng dạng 08:00-08:30");
            int start=minute(p[0].trim()), end=minute(p[1].trim());
            if(start==end) throw new IllegalArgumentException("Giờ bắt đầu và kết thúc phải khác nhau");
            windows.add(new int[]{start,end});
        }
        return windows;
    }
    public static String hhmm(int minute){return String.format(Locale.ROOT,"%02d:%02d",minute/60,minute%60);}
    public static String format(List<int[]> windows){List<String> lines=new ArrayList<>();for(int[] w:windows)lines.add(hhmm(w[0])+"-"+hhmm(w[1]));return String.join("\n",lines);}
    public static boolean scheduled(String schedule, int now) {
        for(int[] w:parse(schedule)) {
            if(w[0]<w[1] ? now>=w[0]&&now<w[1] : now>=w[0]||now<w[1]) return true;
        }
        return false;
    }
    public static boolean scheduledNow(String schedule) { LocalTime t=LocalTime.now(); return scheduled(schedule,t.getHour()*60+t.getMinute()); }
    /** Monday is index 0. Overnight intervals belong to the day they start. */
    public static boolean weekly(String[] schedules, LocalDateTime t) {
        int day=t.getDayOfWeek().getValue()-1, now=t.getHour()*60+t.getMinute();
        for(int[] w:parse(schedules[day])) if(now>=w[0]&&(w[0]>w[1]||now<w[1]))return true;
        for(int[] w:parse(schedules[(day+6)%7])) if(w[0]>w[1]&&now<w[1])return true;
        return false;
    }
    public static long scheduledRemaining(String[] schedules,LocalDateTime t) {
        BitSet minutes=new BitSet(7*1440);
        for(int d=0;d<7;d++)for(int[] w:parse(schedules[d])){
            int start=d*1440+w[0], end=d*1440+w[1]+(w[0]>w[1]?1440:0);
            minutes.set(start,Math.min(end,7*1440));if(end>7*1440)minutes.set(0,end-7*1440);
        }
        int pos=(t.getDayOfWeek().getValue()-1)*1440+t.getHour()*60+t.getMinute();
        if(!minutes.get(pos))return 0;
        int end=minutes.nextClearBit(pos);if(end>=7*1440)end=7*1440+minutes.nextClearBit(0);
        if(end-pos>=7*1440)return Long.MAX_VALUE;
        return (end-pos)*60_000L-t.getSecond()*1000L-t.getNano()/1_000_000L;
    }
    public static long remaining(long elapsed, long expires, int boot, int storedBoot) {
        return boot==storedBoot && elapsed>=0 && expires>elapsed ? expires-elapsed : 0;
    }
}
