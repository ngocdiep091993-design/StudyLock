package vn.studylock;

import java.time.*;
import java.util.*;

/** Pure Java accounting helpers; elapsed time is measured by a monotonic clock. */
public final class DailyBudget {
    private DailyBudget() {}
    public static long remaining(int limitMinutes,long usedMillis) {
        return Math.max(0,limitMinutes*60_000L-Math.max(0,usedMillis));
    }
    public static long grantMillis(int rewardMinutes,long remaining) {
        return Math.max(0,Math.min(rewardMinutes*60_000L,remaining));
    }
    // Only count a recently observed foreground interval. A delayed callback may
    // mean the device was asleep or the service stopped receiving events.
    public static long observedMillis(long previous,long now) {
        return Math.max(0,Math.min(2000L,now-previous));
    }
    /** Divide a measured interval across local calendar days (including DST). */
    public static Map<String,Long> split(long endWall,long duration,ZoneId zone) {
        Map<String,Long> result=new LinkedHashMap<>();
        long cursor=endWall-Math.max(0,duration);
        while(cursor<endWall){
            LocalDate date=Instant.ofEpochMilli(cursor).atZone(zone).toLocalDate();
            long boundary=date.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli();
            long next=Math.min(endWall,boundary);
            result.merge(date.toString(),next-cursor,Long::sum);
            cursor=next;
        }
        return result;
    }
}
