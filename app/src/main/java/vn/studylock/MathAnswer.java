package vn.studylock;
import java.math.*;
public final class MathAnswer {
    private MathAnswer(){}
    // Compare decimal values exactly; fractions must be reduced when requested.
    public static boolean correct(String prompt,String expected,String input){
        if(input==null||input.length()>32)return false;
        String x=input.trim().replace(',', '.').replace('−','-');
        try{
            if(prompt.contains("rút gọn")&&x.contains("/")){
                String[] a=x.split("/",-1);if(a.length!=2||!a[0].matches("-?[0-9]+")||!a[1].matches("[0-9]+"))return false;
                BigInteger n=new BigInteger(a[0]),d=new BigInteger(a[1]);if(d.signum()<=0||!n.gcd(d).equals(BigInteger.ONE)||d.equals(BigInteger.ONE))return false;
                return x.equals(expected);
            }
            if(expected.contains("/"))return false;
            if(!x.matches("-?[0-9]+(\\.[0-9]+)?"))return false;
            return new BigDecimal(x).compareTo(new BigDecimal(expected.replace(',','.')))==0;
        }catch(RuntimeException e){return false;}
    }
}
