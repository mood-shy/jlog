package com.jd.platform.jlog.common.tag;

import java.io.Serializable;
import java.util.List;

/**
 * @author tangbohu
 * @version 1.0.0
 * @ClassName TagConfig.java
 * @createTime 2022年02月13日 22:35:00
 */
public class TagConfig implements Serializable {

    /**
     * 入参的tag
     */
    private List<String> reqTags;

    /**
     * 普通日志的tag
     */
    private List<String> logTags;

    /**
     * 自定义正则 可为空
     */
    private String regex;

    /**
     * 分隔符 默认｜
     */
    private String delimiter = "|";

    /**
     * 连接符 默认=
     */
    private String join = "=";

    /**
     * 提取入参开关
     */
    private Boolean extractReq = true;

    /**
     * 提取普通log开关
     */
    private Boolean extractLog = true;


    public List<String> getReqTags() {
        return reqTags;
    }

    public void setReqTags(List<String> reqTags) {
        this.reqTags = reqTags;
    }

    public List<String> getLogTags() {
        return logTags;
    }

    public void setLogTags(List<String> logTags) {
        this.logTags = logTags;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getJoin() {
        return join;
    }

    public void setJoin(String join) {
        this.join = join;
    }

    public Boolean getExtractReq() {
        return extractReq;
    }

    public void setExtractReq(Boolean extractReq) {
        this.extractReq = extractReq;
    }

    public Boolean getExtractLog() {
        return extractLog;
    }

    public void setExtractLog(Boolean extractLog) {
        this.extractLog = extractLog;
    }

    public static final class Builder {
        private List<String> reqTags;
        private List<String> logTags;
        private String regex;
        private String delimiter;
        private String join;
        private Boolean extractReq;
        private Boolean extractLog;

        public Builder() {
        }

        public static Builder aTagConfig() {
            return new Builder();
        }

        public Builder reqTags(List<String> reqTags) {
            this.reqTags = reqTags;
            return this;
        }

        public Builder logTags(List<String> logTags) {
            this.logTags = logTags;
            return this;
        }

        public Builder regex(String regex) {
            this.regex = regex;
            return this;
        }

        public Builder delimiter(String delimiter) {
            this.delimiter = delimiter;
            return this;
        }

        public Builder join(String join) {
            this.join = join;
            return this;
        }

        public Builder extractReq(Boolean extractReq) {
            this.extractReq = extractReq;
            return this;
        }

        public Builder extractLog(Boolean extractLog) {
            this.extractLog = extractLog;
            return this;
        }

        public TagConfig build() {
            TagConfig tagConfig = new TagConfig();
            tagConfig.setReqTags(reqTags);
            tagConfig.setLogTags(logTags);
            tagConfig.setRegex(regex);
            tagConfig.setDelimiter(delimiter);
            tagConfig.setJoin(join);
            tagConfig.setExtractReq(extractReq);
            tagConfig.setExtractLog(extractLog);
            return tagConfig;
        }
    }

    @Override
    public String toString() {
        return "TagConfig{" +
                "reqTags=" + reqTags +
                ", logTags=" + logTags +
                ", regex='" + regex + '\'' +
                ", delimiter='" + delimiter + '\'' +
                ", join='" + join + '\'' +
                ", extractReq='" + extractReq + '\'' +
                ", extractLog='" + extractLog + '\'' +
                '}';
    }
}
