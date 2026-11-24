import vn.studylock.*;
import java.util.*;
import java.time.*;
public class LogicTest {
    static void check(boolean x,String msg){if(!x)throw new AssertionError(msg);}
    public static void main(String[] args){
        String s="08:00-08:30\n17:00-17:30";
        check(!Rules.scheduled(s,479),"before opening");check(Rules.scheduled(s,480),"opening");check(Rules.scheduled(s,509),"before end");check(!Rules.scheduled(s,510),"end");check(Rules.scheduled(s,1020),"second window");check(!Rules.scheduled("",480),"blank schedule");check(Rules.scheduled("23:50-00:10",1439),"midnight late");check(Rules.scheduled("23:50-00:10",0),"midnight early");check(!Rules.scheduled("23:50-00:10",10),"midnight end");
        for(String invalid:new String[]{"25:00-08:00","08:00-08:00","8:00-08:30","08:00-08:60","abc"}){boolean error=false;try{Rules.parse(invalid);}catch(IllegalArgumentException e){error=true;}check(error,"reject "+invalid);}
        check(Rules.remaining(1000,601000,3,3)==600000,"10 minute grant");check(Rules.remaining(601000,601000,3,3)==0,"expiry");check(Rules.remaining(1,601000,4,3)==0,"restart expiry");
        check(Rules.format(Rules.parse(s)).equals(s),"multiple windows roundtrip");
        check(Rules.scheduled("08:00-08:30\n08:15-09:00",525),"overlapping windows");
        check(DailyBudget.remaining(90,85*60_000L)==5*60_000L,"remaining budget");
        check(DailyBudget.grantMillis(20,5*60_000L)==5*60_000L,"truncated reward");
        check(DailyBudget.grantMillis(10,0)==0,"no reward after daily cap");
        check(DailyBudget.remaining(90,95*60_000L)==0,"cap never negative");
        check(DailyBudget.remaining(90,0)==90*60_000L,"fresh day");
        check(DailyBudget.remaining(60,65*60_000L)==0,"lowered limit keeps usage");
        check(DailyBudget.observedMillis(1000,1500)==500,"foreground sample");
        check(DailyBudget.observedMillis(1000,60000)==2000,"delayed sample bounded");
        ZoneId zone=ZoneId.of("Asia/Ho_Chi_Minh");
        long midnight=LocalDate.of(2026,11,24).atStartOfDay(zone).toInstant().toEpochMilli();
        Map<String,Long> split=DailyBudget.split(midnight+250,500,zone);
        check(split.size()==2&&split.get("2026-11-23")==250&&split.get("2026-11-24")==250,"midnight accounting");
        check(DailyBudget.split(midnight+500,500,zone).get("2026-11-24")==500,"new date accounting");
        check(DailyBudget.split(midnight,0,zone).isEmpty(),"zero duration");
        String[] week={"08:00-08:30\n08:20-09:00", "23:50-00:10", "", "", "", "10:00-11:00", "23:55-00:05"};
        LocalDateTime mon=LocalDateTime.of(2026,11,23,0,0);
        check(Rules.weekly(week,mon),"Sunday overnight carries to Monday");
        check(!Rules.weekly(week,mon.plusMinutes(5)),"overnight ends exclusively");
        check(Rules.weekly(week,mon.plusDays(1).plusHours(23).plusMinutes(55)),"Tuesday overnight starts");
        check(Rules.weekly(week,mon.plusDays(2).plusMinutes(5)),"Wednesday inherited window");
        check(!Rules.weekly(week,mon.plusDays(1).plusHours(8)),"different weekday schedule");
        check(Rules.weekly(week,mon.plusDays(5).plusHours(10)),"Saturday schedule");
        check(Rules.scheduledRemaining(week,mon.plusHours(8).plusMinutes(29).plusSeconds(30))==30*60_000+30_000,"merge overlap warning");
        check(Rules.scheduledRemaining(week,mon.plusMinutes(4).plusSeconds(1))==59_000,"warning overnight");
        check(Rules.scheduledRemaining(week,mon.plusDays(6).plusHours(23).plusMinutes(59))==360_000,"week wrap warning");
        check(DailyBudget.remaining(0,0)==0,"zero minute day");
        check(MathAnswer.correct("", "2,5", "2.50"),"decimal equivalents");
        check(MathAnswer.correct("", "-3", "−3"),"negative input");
        check(MathAnswer.correct("rút gọn", "2/3", "2/3"),"reduced fraction");
        check(!MathAnswer.correct("rút gọn", "2/3", "4/6"),"unreduced fraction rejected");
        check(!MathAnswer.correct("", "3", "3e0"),"invalid notation");
        check(!MathAnswer.correct("", "2", "2/0"),"division by zero");
        check(!MathAnswer.correct("", "3", ""),"empty input");
        Questions q=new Questions(new Random(17));int total=0;
        for(int g=4;g<=6;g++)for(String mode:new String[]{"math","english","mixed"}){String prev="";for(int i=0;i<10000;i++){Questions.Q item=q.next(g,mode);check(!item.prompt.equals(prev),"repeat");check(item.choices.size()==4&&new HashSet<>(item.choices).size()==4,"choices");check(item.choices.contains(item.answer),"answer present");check(!mode.equals("math")||item.subject.equals("Toán"),"math mode");check(!mode.equals("english")||item.subject.equals("Tiếng Anh"),"English mode");if(item.subject.equals("Toán"))check(MathAnswer.correct(item.prompt,item.answer,item.answer),"generated typed answer");prev=item.prompt;total++;}}
        System.out.println("PASS: schedule boundaries, midnight, invalid inputs, reward expiry, reboot invalidation, daily cap, truncated reward, midnight accounting, multi-window roundtrip, weekly schedule, overnight week wrap, warning, zero limit, typed math; "+total+" generated question checks.");
    }
}
