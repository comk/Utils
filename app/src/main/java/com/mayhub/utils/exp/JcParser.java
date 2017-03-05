package com.mayhub.utils.exp;



import com.mayhub.utils.exp.dict.SampleSentenceBean;
import com.mayhub.utils.exp.dict.WordAnalaysisBean;
import com.mayhub.utils.exp.dict.WordBean;
import com.mayhub.utils.exp.dict.WordProperty;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/3/5.
 */
public class JcParser {


    private static void processText(){
        String ss = "sumimasenn<br>（1）〔あやまるとき〕对不起duìbuqǐ,抱歉bàoqiàn.<br>例句:ご迷惑をおかけして誠にすみません<br>译文:给您添麻烦实在对不起.<br>例句:約束の時間におくれて,すみませんでした<br>译文:比约好的时间来迟了,真对不起.<br>（2）〔たのむとき・感謝するとき〕劳驾láojià,对不起duìbuqǐ,谢谢xièxie,借光jièguāng『口』.<br>例句:すみませんが,その窓をあけてくれませんか<br>译文:劳驾,请您把那扇shàn窗户打开好吗?<br>例句:すみませんが火を貸してください<br>译文:劳驾〔对不起〕,借个火儿!<br>例句:すみません,ちょっと通してください<br>译文:借（借）光,让我过去;借光,借光.⇒すまない<br>" +
                "『注意』“劳驾”は“劳您的驾”とするとさらにていねい.“借光”は道をあけてもらうときよく使い,“借借光”“借光,借光”ともいう.“对不起”はものを尋ねたり頼むときの呼びかけにも使うことがある.店員や係の人に対する呼びかけには“同志”,老人には“老大爷”“老大娘”,子どもには“小朋友”をよく使う.道でものを尋ねるときは“请问”“我打听一下”などを使う.";
        String ss1 = "dasu だす<br>（1）〔外に・よそに〕出chū;［送り出す］送sòng;［取り出す］拿出náchū,取出qǔchū;［手さぐりで］掏出tāochū.<br>例句:財布を出す<br>译文:拿出钱包.<br>例句:懐中からピストルを出す<br>译文:从怀里掏出手枪shǒuqiāng.<br>例句:鳥をかごから出してやる<br>译文:把鸟从笼子lóngzi放出来.<br>例句:ごみを出す<br>译文:把垃圾拿出去.<br>例句:娘を嫁に出す<br>译文:把女儿嫁jià出去.<br>例句:生徒を社会に出す<br>译文:把学生送上社会." +
                " 『比較』“……出来”と“……出去”: 物を外へ出す場合,その物が話し手に近づくときは,動詞＋“出来”,話し手から遠ざかるときは,動詞＋“出去”の形で表す.<br>（2）〔伸ばす〕伸出shēnchū;［つきだす］挺出tǐngchū;［前に］探出tànchū.<br>例句:手を出す<br>译文:伸手.<br>例句:胸を出す<br>译文:挺起胸膛xiōngtáng.<br>例句:舌を出す<br>译文:吐tǔ舌头shétou.<br>例句:窓から首を出す<br>译文:从窗子探出头来.<br>（3）〔現す〕露出lòuchū.<br>例句:腕を出す<br>译文:露出胳膊gēbo.<br>例句:喜びを顔に出す<br>译文:脸上露出喜悦的表情;喜形于色.<br>例句:ぼろを出す<br>译文:露出马脚.<br>例句:セーターの外にワイシャツの襟を出す<br>译文:把衬衫chènshān的领子弄到毛衣的外边.";
        String ss2 = "sainn サイン<br>（1）〔合図〕信号xìnhào.<br>例句:コール・sign<br>译文:（电台的）呼号.<br>例句:たがいに目と目でsignする<br>译文:互相用眼睛┏示意〔打信号〕.<br>（2）〈野球〉暗号ànhào.<br>例句:バッテリー間のsign<br>译文:投手和接手之间的暗号.<br>例句:signを送る<br>译文:递dì暗号.<br>例句:signをかわす<br>译文:交换暗号.<br>（3）〔署名〕签名qiānmíng,署名shǔmíng,签字qiānzì,签署qiānshǔ.<br>例句:sign入りの記事<br>译文:（报刊上）有作者署名的文章.<br>例句:書類にsignする<br>译文:在文件上签字.<br>例句:人にsignしてもらう<br>译文:请人签名.<br>例句:signぜめにあう<br>译文:受到要求签名者的包围bāowéi.<br>例句:受取のsignをお願いします<br>译文:请给签收qiānshōu一下.<br>例句:sign帳<br>译文:签名纪念册cè." +
                "<br>『比較』“签名”“签字”“签署”: “签名”は名前を記すこと.“签字”は書類に責任者として名前を記すこと.“签署”は重要書類・条約などに正式に署名すること.";
        String ss3 = "souda<br>Ⅰ《助動》<br>（1）〔伝聞,…という話だ〕据说jùshuō,听说tīngshuō;［うわさによると］传闻chuánwén;［言い伝えによると］传说chuánshuō.<br>例句:午後から雨になるそうだ<br>译文:据说午后要下雨.<br>例句:留守中にお越しくださったそうで,失礼しました<br>译文:听说我不在家的时候您来过了,实在对不起." +
                "『参考』話した人が誰であるかわかっているときには,“听他说〔据他说〕,……”のような形を用いる.“据说”のほうがかたい表現.<br>（2）〔外から見て判断した推量,…のように見える〕好象hǎoxiàng（是）,象是xiàngshi,（象）……似的shìde,似乎sìhu;看样子kàn yàngzi,看来kànlai.<br>例句:健康そうな顔<br>译文:健康（似）的脸色.<br>例句:うれしそうに笑う<br>译文:高兴（似）地笑.<br>例句:自信のなさそうな様子<br>译文:（好象）缺乏自信的样子.<br>例句:先生はとても元気そうでした<br>译文:老师象是很健康的样子.<br>例句:これがよさそうだ<br>译文:这个象挺好似的.<br>例句:子どもが食べたそうに見ている<br>译文:孩子想吃似地看着.<br>（3）〔根拠・論理にもとづいた推量〕好象hǎoxiàng（是）,象是xiàngshi,（象）……似的shìde,似乎sìhu;看样子kàn yàngzi,看来kànlai.<br>例句:聞いたところでは,そんなに昔のことでもなさそうだ<br>译文:听起来,也不象是很久以前的事情.<br>例句:この調子では今日は聴衆が3千人を越えそうだ<br>译文:看样子今天听众要超过三千人.<br>例句:このぶんなら新しいのを買う必要はなさそうだ<br>译文:若是这样,就用不着买新的.<br>（4）〔動作・作用の実現の可能性が大である,…しそうだ〕好象就要hǎoxiàng jiùyào,似乎sìhu就要;［必ず］快要kuàiyào,将要jiāngyào.<br>例句:雨が降りそうだ<br>译文:好象就要下雨似的.";
        String ss4 = "kiwadoi<br>（1）［かんいっぱつ］间不容发jiān bù róng fà『成』;［もう少しで］差（一）点儿chà（yī）diǎnr;［危険な］危险万分wēixiǎn wànfēn.<br>例句:きわどいところで助かる<br>译文:得救于千钧一发qiān jūn yī fà之际;险些丧命.<br>例句:きわどいところで汽車にまにあう<br>译文:差一点儿没赶上火车;勉勉强强赶上火车.<br>例句:きわどい芸当をやる<br>译文:搞冒险的勾当gòudàng.<br>例句:きわどい商売<br>译文:冒险的生意.<br>例句:きわどい勝負<br>译文:非常紧张的比赛.<br>例句:きわどいところで勝つ<br>译文:勉强取胜.<br>例句:きわどいところでひかれそうになる<br>译文:险些被车压上." +
                " 『語法』“差点儿”と“差点儿没”:<br>（1）話し手が実現を望まないときは,両語ともそれが実現しなかったことを意味する.“差点儿掉在河里＝差点儿没掉在河里”（もう少しで川に落ちるところだった）.<br>（2）話し手が実現を望むときは,“差点儿”はそれが実現しなかったこと,“差点儿没”は実現したことを意味する.“差点儿赶上了”（もう少しで間に合うところだった）“差点儿没赶上”（もう少しで間に合わないところだった）.<br>（2）〔わいせつ〕近于猥亵jìnyú wěixiè;近于下流xiàliú.<br>例句:きわどい話<br>译文:下流话.<br>例句:きわどい小説<br>译文:黄色小说.";
        String s1 = "rihita-sinndokai リヒターしんどかい<br>里氏震级Lǐshì zhènjí.<br>例句:地震は-震度階でマグニチュード5.2だった<br>译文:地震是里氏5.2级.";
        String s = "-sann ‐さん<br>（1）〔愛称・敬称〕……先生xiānsheng,女士nǚshì,同志tóngzhì;小Xiǎo……,老Lǎo…….<br>例句:劉さん<br>译文:刘Liú┏先生〔女士〕; 刘同志; 小刘; 老刘.<br>例句:宋文さん<br>译文:宋文Sòng Wén先生; 宋文同志.<br>例句:玉蘭さん<br>译文:玉兰女士; 玉兰.<br>例句:おまわりさん<br>译文:巡警xúnjǐng;警察jǐngchá.<br>例句:お医者さん<br>译文:医生; 大夫dàifu.<br>例句:ぞうさん<br>译文:大象.<br>例句:おさるさん<br>译文:小猴.<br>例句:おいもさん<br>译文:白薯báishǔ.⇒‐ちゃん<br>（2）〔ていねい語〕<br>例句:ご苦労さん<br>译文:辛苦了;受累shòulèi了;劳驾láojià了.<br>例句:ごちそうさん<br>译文:谢谢;谢谢您的款待.<br>例句:お早うさん<br>译文:您早; 早啊.";
        String str = "a<br>《感》<br>（1）〔呼びかけ〕喂wèi!<br>例句:あ,君,ちょっと来て<br>译文:喂!你来一下.<br>（2）〔はい〕是shì.<br>例句:あ,わかりました<br>译文:是!明白了.<br>（3）〔思わず発する語〕呀yā!哎呀āiyā!<br>例句:あ,しまった<br>译文:哎呀,坏了〔糟了zāo le〕.<br>例句:あ,いたっ<br>译文:哎呀!好疼.<br>例句:あ,帽子を忘れた<br>译文:哎呀,把帽子忘了.<br>例句:ああしたやりかた<br>译文:那种作法.ああ言えばこう言う 强词夺理qiǎng cí duó lǐ『成』;你说东他偏说西.<br>例句:自然を愛する<br>译文:爱好自然.<br>例句:花を愛する<br>译文:爱〔喜欢〕花.";
        String str1 = "ami あみ<br>（1）网wǎng;［金網・鉄条網］铁丝网tiěsīwǎng.<br>例句:網にかかった魚<br>译文:落网之鱼.<br>例句:網をかける<br>译文:挂网.<br>例句:網を打つ<br>译文:撒sā网.<br>（2）〔束縛するための〕［捕獲用の］罗网luówǎng;［法のあみ］法网fǎwǎng.<br>例句:法の網にかかる<br>译文:落入法网.<br>例句:みずから網にかかる<br>译文:自投罗网.<br>網をはる<br>（1）下网（捕鱼）;张网（捕鸟）.<br>例句:網をはって魚がかかるのを待っている<br>译文:下网等候鱼落进来.<br>（2）〔手配する〕布置bùzhì捕捉bǔzhuō（逃犯）.<br>例句:警察は犯人を追って全国に網をはった<br>译文:公安人员追踪zhuīzōng罪犯在全国布置了捉拿的罗网.";
        String str4 = "hana はな<br>鼻（子）bí（zi）.<br>例句:わし鼻<br>译文:鹰钩yīnggōu鼻子.<br>例句:しし鼻<br>译文:狮子shīzi鼻;扁biǎn鼻子.<br>例句:だんご鼻（あぐらをかいた鼻）<br>译文:蒜头suàntóu鼻子.<br>例句:上を向いた鼻<br>译文:朝cháo天鼻子.<br>例句:鼻がよくきく<br>译文:鼻子灵líng.<br>例句:鼻がつまる<br>译文:鼻子不通气.<br>例句:鼻の先で笑う<br>译文:冷笑;讥笑jīxiào.<br>鼻が高い 得意扬扬『成』.<br>例句:子どもの成績がよくて鼻が高い<br>译文:因孩子的学习好而感到骄傲jiāo''ào.<br>鼻がへこむ 丢丑diūchǒu;丢脸.<br>鼻が曲がる 恶臭èchòu扑鼻.<br>鼻であしらう 嗤之以鼻chī zhī yǐ bí『成』;冷淡对待.<br>例句:わたしの忠告を鼻であしらった<br>译文:他对我的劝告完全不当一回事.<br>鼻にかける 自高自大『成』;炫耀xuànyào;自豪zìháo.<br>例句:成績がいいのを鼻にかける<br>译文:炫耀自己成绩好.<br>鼻につく 讨厌;腻烦nìfan.";
        String str5 = "ka<br>Ⅰ《並立助詞》<br>（1）〔ひとつを選ぶ〕或huò,或者huòzhě;还是háishi.<br>例句:わたしか弟がお伺いいたしましょう<br>译文:由我或者我弟弟拜访bàifǎng您去.<br>例句:答えはイエスかノーかふたつにひとつだ<br>译文:回答只有一个,“是”还是“不是”.<br>例句:君がくるかぼくが行くかだ<br>译文:是你来或是我去.<br>（2）〔か＋疑問詞＋か〕或huò,或者huòzhě;［…かどうか］是不是shì bùshì,是否shìfǒu.<br>例句:紹介状かなにかありませんか<br>译文:有介绍信什么的没有?<br>例句:李さんかだれかと相談しよう<br>译文:同老李或者谁商量商量吧.<br>例句:あした休みかどうか,まだわからない<br>译文:明天是否放假fàngjià还不知道.<br>例句:この万年筆は山本さんのかどうか,ご存じですか<br>译文:你知道这只钢笔是不是山本的?<br>例句:できるかどうか,あとで電話でお知らせしましょう<br>译文:成不成,以后用电话通知你吧.<br>（3）〔…するかしないかのうちに〕刚gāng……就jiù.<br>例句:試合が始まるか始まらないかに雨が降り出した<br>译文:比赛刚一开始就下起雨来了.<br>例句:発車するかしないかのうちに彼は居眠りをはじめた<br>译文:刚要开车他就打起瞌睡来了.<br>Ⅱ《副助詞》<br>（1）〔疑問の語とともに用いて不確かさを表す〕［いつか］什么时候shénme shíhou;［いくつか］几jǐ,多少duōshao;［だれか］谁sheí;［どこか］哪里nǎli;［なにか］什么shénme.<br>例句:なにか欲しいものはないか<br>译文:你没有什么想要（买）的吗?<br>例句:どこかで会った<br>译文:在哪里遇见过.";
//        WordBean wordBean1 = processWord(str, "あ");
//        WordBean wordBean2 = processWord(str1, "あみ");
//        WordBean wordBean3 = processWord(str4, "鼻");
//        WordBean wordBean4 = processWord(str5, "か");
//        WordBean wordBean5 = processWord(s, "-さん");
//        WordBean wordBean6 = processWord(s1, "-震度階");
        processWord(ss, "");
        processWord(ss1, "");
        processWord(ss2, "");
        processWord(ss3, "");
        processWord(ss4, "");
    }


    public static boolean isContainChinese(String str) {

        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    public static boolean isContainII(String str) {

        Pattern p = Pattern.compile("[ⅠⅡⅢⅣⅤⅥⅦⅧⅨⅩⅪⅫ]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    public static WordBean processWord(String str4, String word){
        WordBean wordBean = new WordBean();
        if(str4.contains("『語法』") || //词级
                str4.contains("『注意』") ||//词级
                str4.contains("<br>『比較』") ||//词级
                str4.contains("『参考』")) {//词级
            int i = 0;
            if (str4.contains("『語法』")) {
                i = str4.indexOf("『語法』");
                wordBean.setPg(str4.substring(i).substring(4));
            }
            if (str4.contains("『注意』")) {
                i = str4.indexOf("『注意』");
                wordBean.setN(str4.substring(i).substring(4));
            }
            if (str4.contains("<br>『比較』")) {
                i = str4.indexOf("<br>『比較』");
                wordBean.setCp(str4.substring(i).substring(8));
            }
            if (str4.contains("『参考』")) {
                i = str4.indexOf("『参考』");
                wordBean.setR(str4.substring(i).substring(4));
            }
            str4 = str4.substring(0, i);
        }
        if(word.matches("[a-z]")){
            str4 = str4.replaceAll(word, "『单词』");
        }
        String[] arr = str4.replaceAll("[à-ǜ]", "").split("<br>");
        StringBuilder stringBuilder = new StringBuilder();
        String lastStr = null;
        String curStr = null;
        for (int i = 0; i < arr.length; i++) {
            if(isContainChinese(arr[i])) {
                curStr = arr[i].replaceAll("[a-z]", "").replaceAll("()", "").replaceAll("┏", "").replaceAll("（）", "").replaceAll("''", "").replaceAll("   ", "");
            }else{
                curStr = arr[i];
            }
            if(arr.length > i + 1) {
                lastStr = arr[i + 1];
            }
            if(i < 2 || lastStr == null || lastStr.length() == 0){
                stringBuilder.append(curStr).append("\n");
            }else{
                if(isContainII(lastStr) || lastStr.startsWith("《") || lastStr.startsWith("译文:") || lastStr.startsWith("例句:") || lastStr.startsWith("(") || lastStr.startsWith("（")){
                    stringBuilder.append(curStr).append("\n");
                }else{
                    stringBuilder.append(curStr).append("\n");
                    stringBuilder.append("\n");
                }
            }
        }

//        System.out.println(stringBuilder.toString());
//        System.out.println("=============================================\n");
        String result = stringBuilder.toString();
        if(word.matches("[a-z]")){
            result = result.replaceAll("『单词』",word);
        }
        String[] arrWord = result.split("\n\n");
        for (int i = 0; i < arrWord.length; i++) {
            if(i == 0){//main word
                processOneWord(arrWord[i], true, word, wordBean);
            }else{ //usually match
                WordBean wordBean1 = new WordBean();
                processOneWord(arrWord[i], false, word, wordBean1);
                wordBean.getU().add(wordBean1);
            }
        }

        return wordBean;
    }

    public static void processOneWord(String wordStr, boolean isMainWord, String ocurrWord, WordBean wordBean){
        String arr[] = wordStr.split("\n");
        String curStr = null;
        WordProperty wordProperty = null;
        WordAnalaysisBean wordAnalaysisBean = null;
        SampleSentenceBean sampleSentenceBean = null;
        for (int i = 0; i < arr.length; i++) {
            if(i == 0){
                if(isMainWord) {
                    wordBean.setP(arr[i]);
                }else{
                    String[] array = arr[i].split(" ");
                    wordBean.setW(array[0]);
                    if(array.length > 1) {
                        wordBean.setE(array[1]);
                    }
                }
            }else{
                if(arr[i].startsWith("《") || isContainII(arr[i])){//词性
                    wordProperty = new WordProperty();
                    wordBean.getO().add(wordProperty);
                    wordProperty.setP(arr[i]);
                }else if(arr[i].startsWith("（")){//多层释义
                    int proSize = wordBean.getO().size();
                    if(proSize > 0){
                        wordProperty = wordBean.getO().get(proSize - 1);
                        wordAnalaysisBean = new WordAnalaysisBean();
                        wordProperty.getS().add(wordAnalaysisBean);
                        wordAnalaysisBean.setC(arr[i]);
                    }else{
                        wordProperty = new WordProperty();
                        wordBean.getO().add(wordProperty);
                        wordAnalaysisBean = new WordAnalaysisBean();
                        wordProperty.getS().add(wordAnalaysisBean);
                        wordAnalaysisBean.setC(arr[i]);
                    }
                }else if(arr[i].startsWith("例句:")){
                    sampleSentenceBean = new SampleSentenceBean();
                    sampleSentenceBean.setJ(arr[i].substring(3));
                }else if(arr[i].startsWith("译文:")){
                    if(wordAnalaysisBean != null && sampleSentenceBean != null){
                        extractSample(arr[i], wordAnalaysisBean, sampleSentenceBean, ocurrWord);
                        sampleSentenceBean = null;
                    }
                }else{
                    wordProperty = new WordProperty();
                    wordBean.getO().add(wordProperty);
                    wordAnalaysisBean = new WordAnalaysisBean();
                    wordProperty.getS().add(wordAnalaysisBean);
                    wordAnalaysisBean.setC(arr[i]);
                }
            }
        }
    }

    public static void extractSample(String s, WordAnalaysisBean wordAnalaysisBean, SampleSentenceBean sampleSentenceBean, String word) {
        int idx = s.indexOf(".");
        if(idx != -1 && idx != s.length() - 1){
            String c = s.substring(0, idx + 1);
            if(c.contains(":")) {
                sampleSentenceBean.setC(c.substring(3));
            }else{
                sampleSentenceBean.setC(c);
            }
            wordAnalaysisBean.getS().add(sampleSentenceBean);
            String nextSampleStr = s.substring(idx + 1).trim();
            if(nextSampleStr.startsWith("⇒")){
                sampleSentenceBean.setJ(sampleSentenceBean.getJ() + "(" + nextSampleStr.substring(1) + ")");
            }else if(nextSampleStr.contains("▼") ||
                    nextSampleStr.contains("『比較』")){
                if(nextSampleStr.contains("『比較』")){
//                    int i = nextSampleStr.length() - 1;
//                    if(nextSampleStr.contains("⇒")){
//                        i = nextSampleStr.indexOf("⇒");
//                        sampleSentenceBean.setJ(sampleSentenceBean.getJ() + "(" + nextSampleStr.substring(i).substring(1) + ")");
//                    }
                    wordAnalaysisBean.setCp(nextSampleStr.substring(4));
                }else if(nextSampleStr.contains("▼")){
//                    int i = nextSampleStr.length() - 1;
//                    if(nextSampleStr.contains("⇒")){
//                        i = nextSampleStr.indexOf("⇒");
//                        sampleSentenceBean.setJ(sampleSentenceBean.getJ() + "(" + nextSampleStr.substring(i).substring(1) + ")");
//                    }
                    sampleSentenceBean.setM(nextSampleStr.substring(1));
                }
            }else{
                int i = nextSampleStr.indexOf(" ");
                if (i != -1) {
                    sampleSentenceBean = new SampleSentenceBean();
                    String j = nextSampleStr.substring(0, i);
                    if (j.contains(":")) {
                        sampleSentenceBean.setJ(j.substring(3));
                    } else {
                        sampleSentenceBean.setJ(j);
                    }
                    extractSample(nextSampleStr.substring(i + 1), wordAnalaysisBean, sampleSentenceBean, word);
                    System.out.println("word : " + word + "-> " + s);
                } else {
                    if(s.contains(":")) {
                        sampleSentenceBean.setC(s.substring(3));
                    }else{
                        sampleSentenceBean.setC(s);
                    }
                }
            }
        }else{
            if(s.contains(":")) {
                sampleSentenceBean.setC(s.substring(3));
            }else{
                sampleSentenceBean.setC(s);
            }
            wordAnalaysisBean.getS().add(sampleSentenceBean);
        }
    }
}
