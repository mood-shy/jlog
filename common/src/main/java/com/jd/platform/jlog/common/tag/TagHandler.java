package com.jd.platform.jlog.common.tag;

import com.alibaba.fastjson.JSON;
import com.jd.platform.jlog.common.utils.CollectionUtil;
import com.jd.platform.jlog.common.utils.ConfigUtil;
import com.jd.platform.jlog.common.utils.StringUtil;
import com.sun.istack.internal.NotNull;
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
 * @createTime 2022年02月12日 21:28:00
 */
public class TagHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TagHandler.class);

    private Set<String> reqTags;

    private boolean extractReq;

    private Set<String> logTags;

    private boolean extractLog;

    private String delimiter = "|";

    private int delimiterLen = delimiter.length();

    private String join = "=";

    private Pattern pattern;

    private static volatile TagHandler INSTANCE = null;

    /**
     * 构建标签处理器
     * @param tagConfig 配置类
     */
    public static void build(TagConfig tagConfig) {

        if(!tagConfig.getExtractReq() && !tagConfig.getExtractLog()){
            return;
        }

        TagHandler handler =  new TagHandler();

        handler.extractReq = tagConfig.getExtractReq();
        handler.extractLog = tagConfig.getExtractLog();
        handler.reqTags = new HashSet<>(tagConfig.getReqTags());
        handler.logTags = new HashSet<>(tagConfig.getLogTags());

        String regex = tagConfig.getRegex();
        if(StringUtil.isNotEmpty(regex)){
            handler.pattern = Pattern.compile(regex);
        }else{
            String escapeDelimiter = ConfigUtil.escapeExprSpecialWord(tagConfig.getDelimiter());
            String str = String.format("(%s[\\w\\W]*?%s)", escapeDelimiter, escapeDelimiter);
            handler.pattern = Pattern.compile(str);
        }
        handler.delimiter = tagConfig.getDelimiter();
        handler.delimiterLen = tagConfig.getDelimiter().length();
        handler.join = tagConfig.getJoin();
        INSTANCE = handler;
        LOGGER.info("构建标签处理器单例完成:{}",INSTANCE.toString());
    }


    /**
     * 提取请求参数里的标签
     * @param params 参数
     * @param ext 额外附加的,如ip等
     * @return tags
     */
    public static Map<String, Object> extractReqTag(Map<String, String[]> params, @NotNull Map<String, Object> ext) {

        if(INSTANCE == null || !INSTANCE.extractReq){ return null; }

        Map<String, Object> requestMap = new HashMap<>(INSTANCE.reqTags.size());
        for (String tag : INSTANCE.reqTags) {
            Object val = ext.get(tag);
            if(val != null){
                requestMap.put(tag, val);
                continue;
            }

            if(CollectionUtil.isNotEmpty(params) && params.get(tag) != null){
                requestMap.put(tag, params.get(tag)[0]);
            }
        }

        return requestMap;
    }


    /**
     * 提取普通日志中的标签
     * @param content 内容
     * @return tags
     */
    public static Map<String, Object> extractLogTag(String content) {
        if(INSTANCE == null || !INSTANCE.extractLog || content.length() < 1){
            return null;
        }

        Map<String,Object> tagMap = new HashMap<>(3);
        Matcher m = INSTANCE.pattern.matcher(content);
        while (m.find()) {
            String str = m.group().substring(INSTANCE.delimiterLen, m.group().length() - INSTANCE.delimiterLen);
            if(str.contains(INSTANCE.join)){
                String[] arr = str.split(INSTANCE.join);
                if(INSTANCE.logTags.contains(arr[0])){
                    tagMap.put(arr[0], arr[1]);
                }
            }else if(str.length() < TAG_NORMAL_KEY_MAX_LEN){
                tagMap.put(TAG_NORMAL_KEY, str);
            }else{
                if (RANDOM.nextInt(50) == 1) {
                    LOGGER.info("Some logs lack tags and are larger than 20 in length. Therefore, they are simply stored");
                }
            }
        }
        return tagMap;
    }



    /**
     * 刷新标签处理器 加锁是为了防止极端情况下, 先到的config1覆盖后到的config2
     * @param tagConfig 新的配置
     */
    public synchronized static void refresh(TagConfig tagConfig) {
        INSTANCE = null;
        build(tagConfig);
    }


    @Override
    public String toString() {
        return "TagHandler{" +
                "reqTags=" + reqTags +
                ", logTags=" + logTags +
                ", delimiter='" + delimiter + '\'' +
                ", delimiterLen=" + delimiterLen +
                ", join='" + join + '\'' +
                '}';
    }

    // static Pattern BRACKET_PATTERN = Pattern.compile("(\\|[\\w\\W]*?\\|)");
    public static List<String> extractTest(String content) {

        List<String> list = new ArrayList<>();
        Matcher m = BRACKET_PATTERN.matcher(content);
        while (m.find()) {
            list.add(m.group().substring(1, m.group().length() - 1));
            //   list.add(m.group().substring(2, m.group().length() - 2));

        }
        return list;
    }


    //static Pattern BRACKET_PATTERN = Pattern.compile("(\\{\\{[\\w\\W]*?\\}\\})");
   // static Pattern BRACKET_PATTERN = Pattern.compile("(\\|\\|[\\w\\W]*?\\|\\|)");
    static Pattern BRACKET_PATTERN = Pattern.compile("(\\|[\\w\\W]*?\\|)");

    static String str1 = "||a=1||b=2||qwewe";
    static String str2 = "||a=1||b=2||qwewe||";
    static String str3 = "||a=1||eee||b=2";
    static String str4 = "||a=1||eee||b=2||";


    public static void main(String[] args) {
       System.out.println("msgByRegular1==> "+JSON.toJSONString(extractTest(str1)));
        System.out.println("msgByRegular2==> "+JSON.toJSONString(extractTest(str2)));
        System.out.println("msgByRegular3==> "+JSON.toJSONString(extractTest(str3)));
        System.out.println("msgByRegular4==> "+JSON.toJSONString(extractTest(str4)));
    }
}
