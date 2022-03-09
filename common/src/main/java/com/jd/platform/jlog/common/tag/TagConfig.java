package com.jd.platform.jlog.common.tag;

import java.io.Serializable;
import java.util.List;

import static com.jd.platform.jlog.common.tag.CollectMode.COMPRESS_ALL;
import static com.jd.platform.jlog.common.tag.CollectMode.EXTRACT_ALL;

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
     * 提取策略
     */
    private long extract = EXTRACT_ALL;

    /**
     * 压缩策略
     */
    private long compress = COMPRESS_ALL;


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

    public long getExtract() {
        return extract;
    }

    public void setExtract(long extract) {
        this.extract = extract;
    }

    public long getCompress() {
        return compress;
    }

    public void setCompress(long compress) {
        this.compress = compress;
    }

    public static final class Builder {
        private List<String> reqTags;
        private List<String> logTags;
        private String regex;
        private String delimiter;
        private String join;
        private long extract;
        private long compress;

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

        public Builder extract(long extract) {
            this.extract = extract;
            return this;
        }

        public Builder compress(long compress) {
            this.compress = compress;
            return this;
        }

        public TagConfig build() {
            TagConfig tagConfig = new TagConfig();
            tagConfig.setReqTags(reqTags);
            tagConfig.setLogTags(logTags);
            tagConfig.setRegex(regex);
            tagConfig.setDelimiter(delimiter);
            tagConfig.setJoin(join);
            tagConfig.setExtract(extract);
            tagConfig.setCompress(compress);
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
                ", extract='" + extract + '\'' +
                ", compress='" + compress + '\'' +
                '}';
    }
}
