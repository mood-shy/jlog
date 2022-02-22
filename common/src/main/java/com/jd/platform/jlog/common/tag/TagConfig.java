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

    private List<String> reqTags;

    private List<String> logTags;

    private String regex;

    private String delimiter = "|";

    private String join = "=";

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


    public static final class Builder {
        private List<String> reqTags;
        private List<String> logTags;
        private String regex;
        private String delimiter = "|";
        private String join = "=";

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

        public TagConfig build() {
            TagConfig tagConfig = new TagConfig();
            tagConfig.setReqTags(reqTags);
            tagConfig.setLogTags(logTags);
            tagConfig.setRegex(regex);
            tagConfig.setDelimiter(delimiter);
            tagConfig.setJoin(join);
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
                '}';
    }
}
