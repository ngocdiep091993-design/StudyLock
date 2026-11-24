package vn.studylock;
public final class LearningRules {
    private LearningRules(){}
    public static int nextLevel(int level,int streak){return Math.max(1,Math.min(3,level+(streak>=3?1:streak<=-2?-1:0)));}
    public static int streak(int previous,boolean right){return right?Math.max(0,previous)+1:Math.min(0,previous)-1;}
    public static int tierIndex(int minutes){return minutes==5?0:minutes==20?2:1;}
    public static long restRemaining(long now,long until){return Math.max(0,until-now);}
}
