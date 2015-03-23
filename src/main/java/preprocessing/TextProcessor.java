package preprocessing;

import common.Config;
import data.Website;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Pair;
import jvnsegmenter.CRFSegmenter;
import org.apache.commons.codec.language.bm.Lang;
import org.apache.lucene.util.Version;
import stopword.StopwordAnnotator;
import utils.NumberUtils;
import vn.hus.nlp.tokenizer.VietTokenizer;
import vn.hus.nlp.tokenizer.segmenter.Segmenter;

import java.io.*;
import java.net.MalformedURLException;
import java.util.*;

/**
 * Provide some utilities in content of a website
 */
public class TextProcessor {

    private CRFSegmenter segmenter;

    private static final String customStopWordEng = "s,h,pm,am,hour,month,day,year,minute,second,a,an,able,about,above,abroad,according,accordingly,across,actually,adj,after,afterwards,again,against,ago,ahead,ain't,all,allow,allows,almost,alone,along,alongside,already,also,although,always,am,amid,amidst,among,amongst,an,and,another,any,anybody,anyhow,anyone,anything,anyway,anyways,anywhere,apart,appear,appreciate,appropriate,are,aren't,around,as,a's,aside,ask,asking,associated,at,available,away,awfully,back,backward,backwards,be,became,because,become,becomes,becoming,been,before,beforehand,begin,behind,being,believe,below,beside,besides,best,better,between,beyond,both,brief,but,by,came,can,cannot,cant,can't,caption,cause,causes,certain,certainly,changes,clearly,c'mon,co,co.,com,come,comes,concerning,consequently,consider,considering,contain,containing,contains,corresponding,could,couldn't,course,c's,currently,dare,daren't,definitely,described,despite,did,didn't,different,directly,do,does,doesn't,doing,done,don't,down,downwards,during,each,edu,eg,eight,eighty,either,else,elsewhere,end,ending,enough,entirely,especially,et,etc,even,ever,evermore,every,everybody,everyone,everything,everywhere,ex,exactly,example,except,fairly,far,farther,few,fewer,fifth,first,five,followed,following,follows,for,forever,former,formerly,forth,forward,found,four,from,further,furthermore,get,gets,getting,given,gives,go,goes,going,gone,got,gotten,greetings,had,hadn't,half,happens,hardly,has,hasn't,have,haven't,having,he,he'd,he'll,hello,help,hence,her,here,hereafter,hereby,herein,here's,hereupon,hers,herself,he's,hi,him,himself,his,hither,hopefully,how,howbeit,however,hundred,i'd,ie,if,ignored,i'll,i'm,immediate,in,inasmuch,inc,inc.,indeed,indicate,indicated,indicates,inner,inside,insofar,instead,into,inward,is,isn't,it,it'd,it'll,its,it's,itself,i've,just,k,keep,keeps,kept,know,known,knows,last,lately,later,latter,latterly,least,less,lest,let,let's,like,liked,likely,likewise,little,look,looking,looks,low,lower,ltd,made,mainly,make,makes,many,may,maybe,mayn't,me,mean,meantime,meanwhile,merely,might,mightn't,mine,minus,miss,more,moreover,most,mostly,mr,mrs,much,must,mustn't,my,myself,name,namely,nd,near,nearly,necessary,need,needn't,needs,neither,never,neverf,neverless,nevertheless,new,next,nine,ninety,no,nobody,non,none,nonetheless,noone,no-one,nor,normally,not,nothing,notwithstanding,novel,now,nowhere,obviously,of,off,often,oh,ok,okay,old,on,once,one,ones,one's,only,onto,opposite,or,other,others,otherwise,ought,oughtn't,our,ours,ourselves,out,outside,over,overall,own,particular,particularly,past,per,perhaps,placed,please,plus,possible,presumably,probably,provided,provides,que,quite,qv,rather,rd,re,really,reasonably,recent,recently,regarding,regardless,regards,relatively,respectively,right,round,said,same,saw,say,saying,says,second,secondly,see,seeing,seem,seemed,seeming,seems,seen,self,selves,sensible,sent,serious,seriously,seven,several,shall,shan't,she,she'd,she'll,she's,should,shouldn't,since,six,so,some,somebody,someday,somehow,someone,something,sometime,sometimes,somewhat,somewhere,soon,sorry,specified,specify,specifying,still,sub,such,sup,sure,take,taken,taking,tell,tends,th,than,thank,thanks,thanx,that,that'll,thats,that's,that've,the,their,theirs,them,themselves,then,thence,there,thereafter,thereby,there'd,therefore,therein,there'll,there're,theres,there's,thereupon,there've,these,they,they'd,they'll,they're,they've,thing,things,think,third,thirty,this,thorough,thoroughly,those,though,three,through,throughout,thru,thus,till,to,together,too,took,toward,towards,tried,tries,truly,try,trying,t's,twice,two,un,under,underneath,undoing,unfortunately,unless,unlike,unlikely,until,unto,up,upon,upwards,us,use,used,useful,uses,using,usually,v,value,various,versus,very,via,viz,vs,want,wants,was,wasn't,way,we,we'd,welcome,well,we'll,went,were,we're,weren't,we've,what,whatever,what'll,what's,what've,when,whence,whenever,where,whereafter,whereas,whereby,wherein,where's,whereupon,wherever,whether,which,whichever,while,whilst,whither,who,who'd,whoever,whole,who'll,whom,whomever,who's,whose,why,will,willing,wish,with,within,without,wonder,won't,would,wouldn't,yes,yet,you,you'd,you'll,your,you're,yours,yourself,yourselves,you've,zero,the,Jannuary,Jan,Febuary,Feb";
    private static final String customStopWordViet = "a_ha,a_lô,à_ơi,á,à,á_à,ạ,ạ_ơi,ai,ai_ai,ai_nấy,ái,ái_chà,ái_dà,alô,amen,áng,ào,ắt,ắt_hẳn,ắt_là,âu_là,ầu_ơ,ấy,bài,bản,bao_giờ,bao_lâu,bao_nả,bao_nhiêu,bay_biến,bằng,bằng_ấy,bằng_không,bằng_nấy,bắt_đầu_từ,bập_bà_bập_bõm,bập_bõm,bất_chợt,bất_cứ,bất_đồ,bất_giác,bất_kể,bất_kì,bất_kỳ,bất_luận,bất_nhược,bất_quá,bất_thình_lình,bất_tử,bây_bẩy,bây_chừ,bây_giờ,bây_giờ,bây_nhiêu,bấy,bấy_giờ,bấy_chầy,bấy_chừ,bấy_giờ,bấy_lâu,bấy_lâu_nay,bấy_nay,bấy_nhiêu,bèn,béng,bển,bệt,biết_bao,biết_bao_nhiêu,biết_chừng_nào,biết_đâu,biết_đâu_chừng,biết_đâu_đấy,biết_mấy,bộ,bội_phần,bông,bỗng,bỗng_chốc,bỗng_dưng,bỗng_đâu,bỗng_không,bỗng_nhiên,bỏ_mẹ,bớ,bởi,bởi_chưng,bởi_nhưng,bởi_thế,bởi_vậy,bởi_vì,bức,cả,cả_thảy     cái,các,cả_thảy,cả_thể,càng,căn,căn_cắt,cật_lực,cật_sức,cây,cha_,cha_chả,chành_chạnh,chao_ôi,chắc,chắc_hẳn,chăn_chắn,chăng,chẳng_lẽ,chẳng_những,chẳng_nữa,chẳng_phải,chậc,chầm_chập,chết_nỗi,chết_tiệt,chết_thật,chí_chết,chỉn,chính,chính_là,chính_thị,chỉ,chỉ_do,chỉ_là,chỉ_tại,chỉ_vì,chiếc,cho_đến,cho_đến_khi,cho_nên,cho_tới,cho_tới_khi,choa,chốc_chốc,chớ,chớ_chi,chợt,chú,chu_cha,chú_mày,chú_mình,chui_cha,chùn_chùn,chùn_chũn,chủn,chung_cục,chung_qui,chung_quy,chung_quy_lại,chúng_mình,chúng_ta,chúng_tôi,chứ,chứ_lị,có_chăng_là,có_dễ,có_vẻ,cóc_khô,coi_bộ,coi_mòi,con,còn,cô,cô_mình,cổ_lai,công_nhiên,cơ,cơ_chừng,cơ_hồ,cơ_mà,cơn,cu_cậu,của,cùng,cùng_cực,cùng_nhau,cùng_với,cũng,cũng_như,cũng_vậy,cũng_vậy_thôi,cứ,cứ_việc,cực_kì     cực_kỳ,cực_lực,cuộc,cuốn,dào,dạ,dần_dà,dần_dần,dầu_sao,dẫu,dẫu_sao,dễ_sợ,dễ_thường,do,do_vì,do_đó,do_vậy,dở_chừng,dù_cho,dù_rằng,duy,dữ,dưới,đã,đại_để,đại_loại,đại_nhân,đại_phàm,đang,đáng_lẽ,đáng_lí,đáng_lý,đành_đạch,đánh_đùng,đáo_để,nấy,nên_chi,nền,nếu,nếu_như,ngay,ngay_cả,ngay_lập_tức,ngay_lúc,ngay_khi,ngay_từ,ngay_tức_khắc,ngày_càng,ngày_ngày,ngày_xưa,ngày_xửa,ngăn_ngắt,nghe_chừng,nghe_đâu,nghen,nghiễm_nhiên,nghỉm,ngõ_hầu,ngoải,ngoài,ngôi,ngọn,ngọt,ngộ_nhỡ,ngươi,nhau,nhân_dịp,nhân_tiện,nhất,nhất_đán,nhất_định,nhất_loạt,nhất_luật,nhất_mực,nhất_nhất,nhất_quyết,nhất_sinh,nhất_tâm,nhất_tề,nhất_thiết,nhé,nhỉ,nhiên_hậu,nhiệt_liệt,nhón_nhén,nhỡ_ra,nhung_nhăng,như,như_chơi,như_không,như_quả,như_thể,như_tuồng,như_vậy,nhưng     nhưng_mà,những,những_ai,những_như,nhược_bằng,nó,nóc,nọ,nổi,nớ,nữa,nức_nở,oai_oái,oái,ô_hay,ô_hô,ô_kê,ô_kìa,ồ,ôi_chao,ôi_thôi,ối_dào,ối_giời,ối_giời_ơi,ôkê,ổng,ơ,ơ_hay,ơ_kìa,ờ,ớ,ơi,phải,phải_chi,phải_chăng,phăn_phắt,phắt,phè,phỉ_phui,pho,phóc,phỏng,phỏng_như,phót,phốc,phụt,phương_chi,phứt,qua_quít,qua_quýt,quả,quả_đúng,quả_làquả_tang,quả_thật,quả_tình,quả_vậy,quá,quá_chừng,quá_độ,quá_đỗi,quá_lắm,quá_sá,quá_thể,quá_trời,quá_ư,quá_xá,quý_hồ,quyển,quyết,quyết_nhiên,ra,ra_phết,ra_trò,ráo,ráo_trọi,rày,răng,rằng,rằng_là,rất,rất_chi_là,rất_đỗi,rất_mực,ren_rén,rén,rích,riệt,riu_ríu,rón_rén,rồi,rốt_cục,rốt_cuộc,rút_cục,rứa,sa_sả     sạch,sao,sau_chót,sau_cùng,sau_cuối,sau_đó,sắp,sất,sẽ,sì,song_le,số_là,sốt_sột,sở_dĩ,suýt,sự,tà_tà,tại,tại_vì,tấm,tấn,tự_vì,tanh,tăm_tắp,tắp,tắp_lự,tất_cả,tất_tần_tật,tất_tật,tất_thảy,tênh,tha_hồ,thà,thà_là,thà_rằng,thái_quá,than_ôi,thanh,thành_ra,thành_thử,thảo_hèn,thảo_nào,thậm,thậm_chí,thật_lực,thật_vậy,thật_ra,thẩy,thế,thế_à,thế_là,thế_mà,thế_nào,thế_nên,thế_ra,thế_thì,thếch,thi_thoảng,thì,thình_lình,thỉnh_thoảng,thoạt,thoạt_nhiên,thoắt,thỏm,thọt,thốc,thốc_tháo,thộc,thôi,thốt,thốt_nhiên,thuần,thục_mạng,thúng_thắng,thửa,thực_ra,thực_vậy,thương_ôi,tiện_thể,tiếp_đó,tiếp_theo,tít_mù,tỏ_ra,tỏ_vẻ,tò_te,toà,toé_khói,toẹt,tọt,tốc_tả,tôi,tối_ư,tông_tốc,tột     tràn_cung_mây,trên,trển,trệt,trếu_tráo,trệu_trạo,trong,trỏng,trời_đất_ơi,trước,trước_đây,trước_đó,trước_kia,trước_nay,trước_tiên,trừ_phi,tù_tì,tuần_tự,tuốt_luốt,tuốt_tuồn_tuột,tuốt_tuột,tuy,tuy_nhiên,tuy_rằng,tuy_thế,tuy_vậy,tuyệt_nhiên,từng,tức_thì,tức_tốc,tựu_trung,ủa,úi,úi_chà,úi_dào,ư,ứ_hự,ứ_ừ,ử,ừ,và,vả_chăng,vả_lại,vạn_nhất,văng_tê,vẫn,vâng,vậy,vậy_là,vậy_thì,veo,veo_veo,vèo,về,vì,vì_chưng,vì_thế,vì_vậy,ví_bằng,ví_dù,ví_phỏng,ví_thử,vị_tất,vô_hình_trung,vô_kể,vô_luận,vô_vàn,vốn_dĩ,với,với_lại,vở,vung_tàn_tán,vung_tán_tàn,vung_thiên_địa,vụt,vừa_mới,xa_xả,xăm_xăm,xăm_xắm,xăm_xúi,xềnh_xệch,xệp,xiết_bao,xoành_xoạch,xoẳn,xoét,xoẹt,xon_xón,xuất_kì_bất_ý,xuất_kỳ_bất_ý,xuể,xuống,ý,ý_chừng,ý_da,có,nhận,rằng,cao,nhà,quá,riêng,gì,muốn,rồi,số,thấy,hay,lên,lần,nào,qua,bằng,điều,biết,lớn,khác,vừa,nếu,thời gian,họ,từng,đây,tháng,trước,chính,cả,việc,chưa,do,nói,ra,nên,đều,đi,tới,tôi,có thể,cùng,vì,làm,lại,mới,ngày,đó,vẫn,mình,chỉ,thì,đang,còn,bị,mà,năm,nhất,hơn,sau,ông,rất,anh,phải,như,trên,tại,theo,khi,nhưng,vào,đến,nhiều,người,từ,sẽ,ở,cũng,không,về,để,này,những,một,các,cho,được,với,có,trong,đã,là,và,của,thực sự,ở trên,tất cả,dưới,hầu hết,luôn,giữa,bất kỳ,hỏi,bạn,cô,tôi,tớ,cậu,bác,chú,dì,thím,cậu,mợ,ông,bà,em,thường,ai,cảm ơn,";

    public static enum Language {
        VIETNAM, ENGLISH
    }

    Properties props;


    StanfordCoreNLP pipeLine;


    /**
     * Detect of a website if it is vietnamese or english website
     * @param text
     * @return
     */
    public static Language detectLanguage(String text){

        String specialChars="àảãáạăằẳẵắặâầẩẫấậèẻẽéẹêềểễếệìỉĩíịòỏõóọôồổỗốộơờởỡớợùủũúụưừửữứựỳỷỹýỵđÀẢÃÁẠĂẰẲẴẮẶÂẦẨẪẤẬÈẺẼÉẸÊỀỂỄẾỆÌỈĨÍỊÒỎÕÓỌÔỒỔỖỐỘƠỜỞỠỚỢÙỦŨÚỤƯĐ";
        boolean check=false;

        int count=0;
        for (int i=0; i<text.length(); i++){
            if (specialChars.contains(String.valueOf(text.charAt(i)))){
                count=count+1;
            }
        }

        System.out.println (Double.valueOf(count)/text.length()*100);
        if (Double.valueOf(count)/text.length()<0.01){
            return  Language.ENGLISH;
        } else {
            return Language.VIETNAM;
        }
    }




    public TextProcessor(String modelDirectory) {
        this.segmenter = new CRFSegmenter(modelDirectory+"/jvnsegmenter");

        props=new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");

        pipeLine= new StanfordCoreNLP(
                props,
                false
        );
    }





    /**
     * Split the text to many words - English
     *
     * @param content
     * @return
     */
    public static String tokenizeEnglish(String content) {
        PTBTokenizer ptbt = new PTBTokenizer(new StringReader(content),
                new CoreLabelTokenFactory(), "");

        String out = "";
        for (CoreLabel label; ptbt.hasNext(); ) {
            label = (CoreLabel) ptbt.next();

            if (label.word().length()>=3){
                out = out + label.word() + " ";
            }
        }
        return out;
    }


    /**
     * Split the text to many words - Vietnamese
     *
     * @param content
     * @return
     */
    public String tokenizeVietnamese(String content) {
        String[] sentences=content.split(".,");

        String result="";
        for (String string:sentences){
            result=result+" "+segmenter.segmenting(string);
        }
        return result;

    }


    /**
     *
     * @param content
     * @param language
     * @return
     */
    public  String tokenize(String content, Language language){
        if (language==Language.ENGLISH){
            return tokenizeEnglish(content);
        } else if (language==Language.VIETNAM){
            return tokenizeVietnamese(content);
        }

        return tokenizeVietnamese(content);
    }



    /**
     * Convert all words existed in the text to its lemma
     *
     * @param text
     * @return
     */
    public String lemmaEnglish(String text) {

        Annotation document = this.pipeLine.process(text);

        String output = "";
        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);

                output = output + lemma + " ";
            }
        }

        return output;
    }


    /**
     * Remove all stop word, in text
     *
     * @param text
     * @param customStopWordList
     * @return
     */
    public static String removeStopWords(String text, String customStopWordList, Language language) {
        //setup coreNlp properties for stopwords. Note the custom stopword list and check for lemma property
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, stopword");

        props.setProperty("customAnnotatorClass.stopword", "stopword.StopwordAnnotator");
        props.setProperty(StopwordAnnotator.STOPWORDS_LIST, customStopWordList);
        //props.setProperty(StopwordAnnotator.CHECK_LEMMA, "true");

        //get the custom stopword set
        Set<?> stopWords = StopwordAnnotator.getStopWordList(Version.LUCENE_36, customStopWordList, true);

        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation document = new Annotation(text);
        pipeline.annotate(document);
        List<CoreLabel> tokens = document.get(CoreAnnotations.TokensAnnotation.class);

        String output = "";
        for (CoreLabel token : tokens) {
            //get the stopword annotation
            //Pair<Boolean, Boolean> stopword = token.get(StopwordAnnotator.class);

            String word = token.word().toLowerCase();

            boolean check = true;
            if (stopWords.contains(word)) {
                check = false;
            } else {
                // do nothing
            }

            if (language==Language.ENGLISH){
                String lemma = token.lemma().toLowerCase();
                if (stopWords.contains(lemma)) {
                    check = false;
                }
            }

            if (check) output = output + token.word() + " ";
        }

        return output;
    }



    /**
     * List all the different words in the text and number times of each word
     * @param text
     * @return
     */
    public static Map<String, Integer> countWords(String text) {

        HashMap<String, Integer> map = new HashMap<String, Integer>();
        String[] words = text.split(" ");

        for (int i = 0; i < words.length; i++) {
            String word = words[i].trim();

            if (map.containsKey(word)) {
                map.put(word, map.get(word) + 1);
            } else {
                map.put(word, 1);
            }
        }
        return map;
    }

    /**
     * Pre-processing an url
     * @param lang
     * @return
     */
    public String preProcessing(String text, Language lang) {

        text = text.toLowerCase();
        text = text.replaceAll("[0-9]", " ");


        System.out.println ("Tokenize word");
        text = tokenize(text, lang);
        text = text.replaceAll("[^\\wàảãáạăằẳẵắặâầẩẫấậèẻẽéẹêềểễếệìỉĩíịòỏõóọôồổỗốộơờởỡớợùủũúụưừửữứựỳỷỹýỵđÀẢÃÁẠĂẰẲẴẮẶÂẦẨẪẤẬÈẺẼÉẸÊỀỂỄẾỆÌỈĨÍỊÒỎÕÓỌÔỒỔỖỐỘƠỜỞỠỚỢÙỦŨÚỤƯĐ]", " ");

        System.out.println ("Lemma English");
        if (lang==Language.ENGLISH) {
            text = lemmaEnglish(text);
        }

        System.out.println ("Remove Stopwords");
        if (lang==Language.VIETNAM){
            text = removeStopWords(text, TextProcessor.customStopWordViet, Language.VIETNAM);
        } else {
            text = removeStopWords(text, TextProcessor.customStopWordEng, Language.ENGLISH);
        }



        return text;
    }

    /***
     *
     * @param urls
     * @param language
     */
    public void compute(List<String> urls, Language language) {
        List<String> words = new ArrayList<String>();
        Map<String, Integer>[] websites = new Map[urls.size()];
        //TextProcessor processor=new TextProcessor();



        // get the dictionary
        for (int i = 0; i < urls.size(); i++) {
            String url = urls.get(i);

            Website handler = Website.connect(url);

            if (handler == null) {
                websites[i] = null;
                System.out.println (url+" is missing");
            } else {

                String text = preProcessing(handler.getText(), language);

                List<String> links = new ArrayList<String>();


                // get links and host
                try {
                    links = handler.getLinkSameHost(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    System.out.println (url+" is not invalid");
                }


                for (String link : links) {
                    Website temp = Website.connect(link);
                    if (temp!= null) {
                        text = text + " " + preProcessing(temp.getText(), language);
                    } else {
                        System.out.println (link +" is missing");
                    }
                }

                websites[i] = countWords(text);

                Object[] keys = websites[i].keySet().toArray();

                for (int j = 0; j < keys.length; j++) {
                    String key = (String) keys[j];
                    if (!words.contains(key)) {
                        words.add(key);
                    }
                }
            }


        }


        //calculate matrix
        double matrix[][] = new double[urls.size()][words.size()];
        for (int i = 0; i < urls.size(); i++) {

            // set all element of a row=0
            Arrays.fill(matrix[i], 0);

            if (websites[i] == null) {
                Arrays.fill(matrix[i], 1.0 / words.size());
            } else {
                for (int j = 0; j < words.size(); j++) {
                    String word = words.get(j);
                    // if the word appear in the website
                    if (websites[i].containsKey(word)) {
                        matrix[i][j] = websites[i].get(word);
                    }
                }
            }

        }

        for (int i = 0; i < matrix.length; i++) {
            double sum = 0;
            for (int j = 0; j < matrix[i].length; j++) {
                sum = sum + matrix[i][j];
            }

            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = matrix[i][j] / sum;
            }

        }

        Map<String, Double> map = new HashMap<String, Double>();

        for (int i = 0; i < words.size(); i++) {

            double value = 0;

            for (int j = 0; j < matrix.length; j++) {
                value = value + matrix[j][i];
            }

            map.put(words.get(i), value);
        }


        List<String> listMax = NumberUtils.sort(map, 150);


        for (int i = 0; i < listMax.size(); i++) {
            System.out.println(listMax.get(i) + ": " + map.get(listMax.get(i)));


        }
    }


//    public static void main(String args[]){
//        TextProcessor textProcessor=new TextProcessor();
//        Website website=Website.connect("http://vnexpress.net");
//        System.out.println (website.getText());
//        System.out.println(textProcessor.tokenizeVietnamese(website.getText()));
//    }
}

