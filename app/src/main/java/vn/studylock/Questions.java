package vn.studylock;
import java.util.*;
public final class Questions {
    public static final class Q {
        public final String subject, prompt, answer; public final List<String> choices;
        Q(String s,String p,String a,List<String> c){subject=s;prompt=p;answer=a;choices=Collections.unmodifiableList(c);}
    }
    private final Random r; private String last="";
    public Questions(){this(new Random());} public Questions(Random random){r=random;}
    private int n(int a,int b){return a+r.nextInt(b-a+1);}
    private Q q(String s,String p,String a,String... wrong){
        Set<String> set=new LinkedHashSet<>();set.add(a);Collections.addAll(set,wrong);
        if(set.size()!=4)throw new IllegalArgumentException("Cần 4 đáp án khác nhau: "+p);
        List<String> c=new ArrayList<>(set);Collections.shuffle(c,r);return new Q(s,p,a,c);
    }
    private Q num(String p,int a){int d=n(1,9);return q("Toán",p,""+a,""+(a+d),""+(a-d),""+(a+2*d));}
    private Q math(int g){
        int a,b,c,d;
        switch(g){
            case 4:
                switch(n(0,6)){
                    case 0:a=n(100,9999);b=n(100,9999);return num(a+" + "+b+" = ?",a+b);
                    case 1:a=n(1000,9999);b=n(100,a);return num(a+" − "+b+" = ?",a-b);
                    case 2:a=n(12,99);b=n(2,12);return num(a+" × "+b+" = ?",a*b);
                    case 3:a=n(5,30);b=n(2,12);return num(a*b+" : "+b+" = ?",a);
                    case 4:a=n(3,25);b=n(2,20);return num("Chu vi hình chữ nhật dài "+a+" cm, rộng "+b+" cm là bao nhiêu cm?",2*(a+b));
                    case 5:a=n(3,25);b=n(2,20);return num("Diện tích hình chữ nhật dài "+a+" cm, rộng "+b+" cm là bao nhiêu cm²?",a*b);
                    default:a=n(2,9);b=n(1,a-1);c=n(2,20);return num("Tìm "+b+"/"+a+" của "+(a*c)+".",b*c);
                }
            case 5:
                switch(n(0,6)){
                    case 0:a=n(11,99);b=n(11,99);return decimal(dec(a)+" + "+dec(b)+" = ?",a+b);
                    case 1:a=n(20,99);b=n(1,a-1);return decimal(dec(a)+" − "+dec(b)+" = ?",a-b);
                    case 2:a=n(2,9)*5;b=n(2,20);return num("Tìm "+a+"% của "+b*20+".",a*b/5);
                    case 3:a=n(2,20);b=n(2,20)*2;return num("Tam giác có đáy "+b+" cm và chiều cao "+a+" cm. Diện tích (cm²)?",a*b/2);
                    case 4:a=n(2,12);b=n(2,12);c=n(2,12);return num("Thể tích hình hộp chữ nhật "+a+" × "+b+" × "+c+" cm (cm³)?",a*b*c);
                    case 5:a=n(2,15);b=n(20,80);return num("Đi "+b*a+" km trong "+a+" giờ. Vận tốc trung bình (km/h)?",b);
                    default:a=n(10,99);return num(dec(a)+" m bằng bao nhiêu cm?",a*10);
                }
            default:
                switch(n(0,6)){
                    case 0:a=n(-30,30);b=n(-30,30);return num("("+a+") + ("+b+") = ?",a+b);
                    case 1:a=n(-20,20);b=n(-20,20);return num("("+a+") − ("+b+") = ?",a-b);
                    case 2:a=n(2,12);b=n(-12,-2);return num(a+" × ("+b+") = ?",a*b);
                    case 3:a=n(2,15);b=n(2,15);return num("Ước chung lớn nhất của "+a+" và "+b+"?",gcd(a,b));
                    case 4:a=n(2,12);b=n(2,12);return num("Bội chung nhỏ nhất của "+a+" và "+b+"?",a/gcd(a,b)*b);
                    case 5:a=n(2,9);b=n(2,5);c=n(1,a-1);d=n(1,a-1);return fraction("Tính "+c+"/"+a+" + "+d+"/"+a+" (rút gọn).",c+d,a);
                    default:a=n(2,15);b=n(2,15);return num("Giá trị của 2 × ("+a+" + "+b+") − "+a+"?",a+2*b);
                }
        }
    }
    private static int gcd(int a,int b){while(b!=0){int t=a%b;a=b;b=t;}return Math.abs(a);}
    private static String dec(int a){return (a/10)+","+(a%10);}
    private Q decimal(String p,int a){return q("Toán",p,dec(a),dec(a+1),dec(a+10),dec(a+11));}
    private static String frac(int a,int b){int g=gcd(a,b);return b/g==1?""+a/g:(a/g)+"/"+(b/g);}
    private Q fraction(String p,int a,int b){return q("Toán",p,frac(a,b),frac(a+1,b),frac(a+2,b),frac(a+3,b));}
    // Each row: prompt | correct | three distractors. All original short practice questions.
    private static final String[][] E4={
      {"I ___ a student.","am","is","are","be"},{"She ___ my sister.","is","am","are","be"},{"They ___ happy.","are","am","is","be"},
      {"Choose the plural of 'book'.","books","bookes","book's","book"},{"Choose the plural of 'child'.","children","childs","childes","child"},
      {"What color is grass?","green","blue","pink","black"},{"What is the opposite of 'big'?","small","tall","long","fast"},
      {"I have ___ apple.","an","a","two","many"},{"There ___ two cats.","are","is","am","be"},
      {"A fish can ___.","swim","fly","read","write"},{"My father's mother is my ___.","grandmother","sister","aunt","cousin"},
      {"Which day comes after Monday?","Tuesday","Sunday","Friday","Saturday"},{"Which number is 'twelve'?","12","20","2","21"},
      {"This ___ my pencil.","is","am","are","be"},{"We ___ English at school.","study","studies","studying","to study"},
      {"Where do you sleep?","In a bedroom","In a river","On a bus stop","In a shoe"},{"What is the opposite of 'hot'?","cold","old","new","short"},
      {"He ___ a blue bag.","has","have","having","to have"},{"How ___ are you? – I'm ten.","old","many","much","long"},
      {"Which word means 'quả cam'?","orange","banana","grape","pear"},{"Which word means 'bút chì'?","pencil","ruler","eraser","book"},
      {"___ is your name?","What","How many","Where","When"},{"It is ___ on a rainy day.","wet","dry","empty","silent"},{"I can ___ a bike.","ride","rides","riding","to ride"}
    };
    private static final String[][] E5={
      {"He usually ___ to school at seven.","goes","go","going","to go"},{"They ___ football yesterday.","played","play","playing","plays"},
      {"I ___ my homework now.","am doing","do yesterday","does","did tomorrow"},{"How ___ books are there?","many","much","long","old"},
      {"How ___ milk do you want?","much","many","often","old"},{"My birthday is ___ May.","in","on","at","to"},
      {"The lesson starts ___ eight o'clock.","at","on","in","of"},{"We have English ___ Monday.","on","at","in","of"},
      {"Choose the past tense of 'go'.","went","goed","goes","going"},{"Choose the past tense of 'eat'.","ate","eated","eats","eating"},
      {"What did you do yesterday?","I visited my aunt.","I am visiting my aunt now.","I will visit tomorrow.","I visits my aunt."},
      {"She is ___ than her brother.","taller","tallest","more tall","tall"},{"An elephant is ___ than a mouse.","heavier","lighter","smaller","shorter"},
      {"I have a toothache. I should see a ___.","dentist","pilot","farmer","driver"},{"Which word means 'thư viện'?","library","museum","factory","market"},
      {"There isn't ___ water in the bottle.","any","many","a","an"},{"Would you like some tea? – ___.","Yes, please","Yes, I am","No, I don't am","I tea"},
      {"What is the opposite of 'easy'?","difficult","happy","quiet","early"},{"She wants ___ a doctor.","to be","be","being yesterday","is"},
      {"My mother ___ dinner every evening.","cooks","cook","cooking","to cook"},{"We ___ at home last night.","were","are","is","am"},
      {"Choose the plural of 'foot'.","feet","foots","foot's","foot"},{"You should ___ your hands before eating.","wash","washes","washing","washed"},
      {"The hospital is next ___ the bank.","to","at","of","from"}
    };
    private static final String[][] E6={
      {"Listen! Someone ___ at the door.","is knocking","knock","knocks yesterday","knocked tomorrow"},{"My sister ___ English every day.","studies","study","studying","is study"},
      {"There ___ some apples on the table.","are","is","am","be"},{"This is the ___ building in town.","tallest","taller","tall","more tall"},
      {"This book is ___ than that one.","more interesting","most interesting","interestinger","interesting"},{"You ___ wear a helmet when riding a motorbike.","must","mustn't","are","was"},
      {"You ___ litter in the classroom.","mustn't","are","were","has"},{"We ___ a film last night.","watched","watch","watching","watches"},
      {"I think it ___ rain tomorrow.","will","did","was","has yesterday"},{"There is ___ rice in the bowl.","some","many","a","an"},
      {"How often do you exercise?","Twice a week","Two meters","At school","Five dollars"},{"The opposite of 'noisy' is ___.","quiet","crowded","busy","modern"},
      {"The cat is hiding ___ the table, below it.","under","above","in front of","on"},{"My house is near school, ___ I walk there.","so","but","although","or"},
      {"I like English ___ it is interesting.","because","but","or","although"},{"This is ___ umbrella.","an","a","many","two"},
      {"He didn't ___ to school yesterday.","go","went","goes","going"},{"___ you play badminton yesterday?","Did","Do","Are","Is"},
      {"If it rains, we ___ stay at home.","will","did","were","has"},{"Choose the correct question.","Where do you live?","Where you do live?","Where does you live?","Where living you?"},
      {"This is my pen. It is ___.","mine","my","me","I"},{"Which word means 'môi trường'?","environment","entertainment","equipment","agreement"},
      {"We should ___ paper to protect the environment.","recycle","waste","throw away","burn"},{"She is good ___ maths.","at","on","in the","of"}
    };
    private Q english(int grade){String[][] all=grade==4?E4:grade==5?E5:E6;String[] x=all[r.nextInt(all.length)];return q("Tiếng Anh",x[0],x[1],x[2],x[3],x[4]);}
    public Q next(int grade,String mode){Q x; do {x=(mode.equals("math")||mode.equals("mixed")&&r.nextBoolean())?math(grade):english(grade);}while(x.prompt.equals(last));last=x.prompt;return x;}
}
