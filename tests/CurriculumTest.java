import vn.studylock.*;
import java.util.*;
public class CurriculumTest {
 static void check(boolean b,String s){if(!b)throw new AssertionError(s);}
 public static void main(String[] args){
  Curriculum c=new Curriculum(new Random(2026));int total=0;Set<String> english=new HashSet<>();
  for(int g=4;g<=6;g++)for(int term=0;term<=2;term++)for(int level=1;level<=3;level++)for(String s:new String[]{"math","english"}){
    String prev="";for(int i=0;i<2000;i++){Questions.Q q=c.next(g,term,s,level);check(!q.prompt.equals(prev),"repeat "+q.prompt);check(q.choices.size()==4&&new HashSet<>(q.choices).size()==4,"choices");check(q.choices.contains(q.answer),"missing answer");check(q.subject.equals(s.equals("math")?"Toán":"Tiếng Anh"),"subject");if(s.equals("math"))check(MathAnswer.correct(q.prompt,q.answer,q.answer),"typed: "+q.prompt+" = "+q.answer);else english.add(q.prompt);prev=q.prompt;total++;}}
  check(english.size()==Curriculum.englishCount(),"English bank coverage: "+english.size()+" / "+Curriculum.englishCount());
  check(LearningRules.nextLevel(1,3)==2&&LearningRules.nextLevel(2,3)==3&&LearningRules.nextLevel(3,3)==3,"increase");check(LearningRules.nextLevel(3,-2)==2&&LearningRules.nextLevel(1,-2)==1,"decrease");check(LearningRules.streak(2,false)==-1&&LearningRules.streak(-1,true)==1,"streak reset");check(LearningRules.restRemaining(1000,5000)==4000&&LearningRules.restRemaining(5000,5000)==0,"rest boundary");
  System.out.println("PASS: "+total+" curriculum draws across grades 4–6, terms 0–2, difficulty 1–3, both subjects; "+english.size()+" unique English prompts; adaptive thresholds and rest boundary.");
 }
}
