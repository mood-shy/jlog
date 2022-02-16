package com.jd.platform.jlog.common.model;

import com.alibaba.fastjson.JSON;
import com.jd.platform.jlog.common.utils.ConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.jd.platform.jlog.common.constant.Constant.TAG_NORMAL_KEY;
import static com.jd.platform.jlog.common.constant.Constant.TAG_NORMAL_KEY_MAX_LEN;
import static com.jd.platform.jlog.common.utils.ConfigUtil.RANDOM;

/**
 * @author tangbohu
 * @version 1.0.0
 * @ClassName Tag.java
 * @Description TODO
 * @createTime 2022年02月12日 21:28:00
 */
public class TagHandler {

    private static Logger logger = LoggerFactory.getLogger(TagHandler.class);

    private Set<String> reqTags;

    private Set<String> logTags;

    private String regex;

    private String delimiter = "|";

    private int delimiterLen = delimiter.length();

    private String join = "=";

    private Pattern pattern;

    private static TagHandler INSTANCE = null;

    public static void buildTag(TagConfig tagConfig) {
        String regex = tagConfig.getRegex();
        TagHandler handler = new TagHandler();
        handler.reqTags = new HashSet<>(tagConfig.getReqTags());
        handler.logTags = new HashSet<>(tagConfig.getLogTags());
        if(regex != null && !"".equals(regex)){
            handler.regex = regex;
            handler.pattern = Pattern.compile(regex);
        }else{
            handler.delimiter = tagConfig.getDelimiter();
            handler.delimiterLen = tagConfig.getDelimiter().length();
            handler.join = tagConfig.getJoin();
            System.out.println("delimiter==>  "+tagConfig.getDelimiter());

            String escapeDelimiter = ConfigUtil.escapeExprSpecialWord(tagConfig.getDelimiter());
            System.out.println("## escapeDelimiter==>  "+escapeDelimiter);

            // String str = "("+ formatDelimiter +"[\\w\\W]*?"+ formatDelimiter +")";
            String str = String.format("(%s[\\w\\W]*?%s)", escapeDelimiter, escapeDelimiter);
            System.out.println("这里 实力话了"+str);

            handler.pattern = Pattern.compile(str);
        }
        INSTANCE = handler;
    }




    public static Map<String, Object> extract(String content) {
        if(INSTANCE == null){
            return null;
        }

        Map<String,Object> tagMap = new HashMap<>(3);
        Matcher m = INSTANCE.pattern.matcher(content);
        System.out.println("content==> "+content);

        while (m.find()) {
            String str = m.group().substring(INSTANCE.delimiterLen, m.group().length() - INSTANCE.delimiterLen);
            if(str.contains(INSTANCE.join)){
                String[] arr = str.split(INSTANCE.join);
                System.out.println("INSTANCE.logTags==> "+INSTANCE.logTags);
                if(INSTANCE.logTags.contains(arr[0])){
                    tagMap.put(arr[0], arr[1]);
                }
            }else if(str.length() < TAG_NORMAL_KEY_MAX_LEN){
                System.out.println("INSTANCE.TAG_NORMAL_KEY==> "+str);

                tagMap.put(TAG_NORMAL_KEY, str);
            }else{
                if (RANDOM.nextInt(50) == 1) {
                    logger.info("Some logs lack tags and are larger than 20 in length. Therefore, they are simply stored");
                }
            }
        }
        return tagMap;
    }




    // static Pattern BRACKET_PATTERN = Pattern.compile("(\\|[\\w\\W]*?\\|)");

    public static List<String> extractTest(String content) {

        List<String> list = new ArrayList<>();
        Matcher m = BRACKET_PATTERN.matcher(content);
        while (m.find()) {
            list.add(m.group().substring(1, m.group().length() - 1));
            //   list.add(m.group().substring(2, m.group().length() - 2));
           // list.add(m.group().substring(3, m.group().length()-3));
        }
        return list;
    }


    //static Pattern BRACKET_PATTERN = Pattern.compile("(\\{\\{[\\w\\W]*?\\}\\})");
   // static Pattern BRACKET_PATTERN = Pattern.compile("(\\|\\|[\\w\\W]*?\\|\\|)");
    static Pattern BRACKET_PATTERN = Pattern.compile("(\\|[\\w\\W]*?\\|)");

    static String str1 = "|订单完成|订单类型=1";
    static String str2 = "  ddddd";

 //   String reg=".*ll.*";  //判断字符串中是否含有ll
  //      System.out.println(str.matches(reg));

   /* static String str1 = "||a=1||b=2||qwewe";
    static String str2 = "||a=1||b=2||qwewe||";
    static String str3 = "||a=1||eee||b=2";
    static String str4 = "||a=1||eee||b=2||";*/


   // static String str3 = "|a=1|这是普通log|b=2";
   // static String str4 = "|a=1|这是普通log|b=2|";

    public static void main(String[] args) {
     //  System.out.println("msgByRegular1==> "+JSON.toJSONString(extractTest(str1)));
        System.out.println("msgByRegular2==> "+JSON.toJSONString(extractTest(str2)));
     //   System.out.println("msgByRegular3==> "+JSON.toJSONString(extractTest(str3)));
      //  System.out.println("msgByRegular4==> "+JSON.toJSONString(extractTest(str4)));
    }




    /* Map<String, Object> requestMap = new HashMap<>(16);
        requestMap.put("tracerId", "1");
        requestMap.put("pin", UUID.randomUUID());
        requestMap.put("appName", "myTest");
        requestMap.put("uuid", "uuid" + UUID.randomUUID());
        requestMap.put("client", "android");
        requestMap.put("clientVersion", "10.3.2");
        requestMap.put("ip", "127.0.0.1");
        requestMap.put("serverIp", "127.0.0.1");
        requestMap.put("uri", "test");
        tracerObject.add(requestMap);



 /* @Bean
    public IConfigCenter client() throws Exception {
        CenterConfig jConfig = new CenterConfig.Builder()
                .etcdServer(etcdServer)
                .nacosServer(nacosServer)
                .zkServer(zkServer)
                .build();
        ConfigCenterFactory.buildConfigCenter(jConfig);
        logger.info("jConfig : " + jConfig);
        //连接多个时，逗号分隔
        //return JdEtcdBuilder.build(etcdServer);
        return ConfigCenterFactory.getClient(jConfig.getCenter());
    }*/


}
