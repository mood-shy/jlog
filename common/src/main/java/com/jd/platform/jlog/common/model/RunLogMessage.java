package com.jd.platform.jlog.common.model;

/**
 * className：RunLogMessage
 * description：
 *
 * @author wuweifeng
 * @version 1.0.0
 */
public class RunLogMessage {
    /**
     * tracerId
     */
    private long tracerId;
    /**
     * 时间创建时间
     */
    private long createTime;
    /**
     * 日志内容
     */
    private String content;
    /**
     * info、error
     */
    private String logLevel;
    /**
     * 类名
     */
    private String className;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 线程名
     */
    private String threadName;

    public long getTracerId() {
        return tracerId;
    }

    public void setTracerId(long tracerId) {
        this.tracerId = tracerId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }
}
