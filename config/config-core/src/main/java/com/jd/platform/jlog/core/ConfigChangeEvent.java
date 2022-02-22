package com.jd.platform.jlog.core;


/**
 * @author didi
 */
public class ConfigChangeEvent {

    private String key;
    private String oldValue;
    private String newValue;
    private String namespace;
    private ConfigChangeType changeType;
    private static final String DEFAULT_NAMESPACE = "DEFAULT";


    public ConfigChangeEvent(){

    }

    public ConfigChangeEvent(String key, String newValue) {
        this(key, DEFAULT_NAMESPACE, null, newValue, ConfigChangeType.MODIFY);
    }

    public ConfigChangeEvent(String key, String namespace, String oldValue, String newValue,
                             ConfigChangeType type) {
        this.key = key;
        this.namespace = namespace;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.changeType = type;
    }

    /**
     * Gets data id.
     *
     * @return the data id
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets data id.
     *
     * @param key key
     */
    public ConfigChangeEvent setKey(String key) {
        this.key = key;
        return this;
    }

    /**
     * Gets old value.
     *
     * @return the old value
     */
    public String getOldValue() {
        return oldValue;
    }

    /**
     * Sets old value.
     *
     * @param oldValue the old value
     */
    public ConfigChangeEvent setOldValue(String oldValue) {
        this.oldValue = oldValue;
        return this;
    }

    /**
     * Gets new value.
     *
     * @return the new value
     */
    public String getNewValue() {
        return newValue;
    }

    /**
     * Sets new value.
     *
     * @param newValue the new value
     */
    public ConfigChangeEvent setNewValue(String newValue) {
        this.newValue = newValue;
        return this;
    }

    /**
     * Gets change type.
     *
     * @return the change type
     */
    public ConfigChangeType getChangeType() {
        return changeType;
    }

    /**
     * Sets change type.
     *
     * @param changeType the change type
     */
    public ConfigChangeEvent setChangeType(ConfigChangeType changeType) {
        this.changeType = changeType;
        return this;
    }

    /**
     * Gets namespace.
     *
     * @return the namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Sets namespace.
     *
     * @param namespace the namespace
     */
    public ConfigChangeEvent setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    @Override
    public String toString() {
        return "ConfigChangeEvent{" +
                "key='" + key + '\'' +
                ", oldValue='" + oldValue + '\'' +
                ", newValue='" + newValue + '\'' +
                ", namespace='" + namespace + '\'' +
                ", changeType=" + changeType +
                '}';
    }
}
