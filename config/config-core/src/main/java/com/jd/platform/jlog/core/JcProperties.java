package com.jd.platform.jlog.core;

import com.alibaba.fastjson.JSON;
import com.jd.platform.jlog.common.utils.ConfigUtil;
import com.jd.platform.jlog.common.utils.FastJsonUtils;
import com.jd.platform.jlog.common.utils.StringUtil;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.jd.platform.jlog.common.utils.ConfigUtil.lowerFirst;

/**
 * @author tangbohu
 * @version 1.0.0
 * @ClassName JcProperties.java
 * @Description TODO
 * @createTime 2022年03月07日 12:43:00
 */
public class JcProperties extends Properties {

    public JcProperties() {
    }

    public String getString(String key) {
        Object val = super.get(key);
        if(val == null){
            return null;
        }
        return String.valueOf(super.get(key));
    }

    public Long getLong(String key) {
        String val = getString(key);
        if(StringUtil.isEmpty(val)){
            return null;
        }
        return Long.valueOf(val);
    }

    public List<String> getStrList(String key) {
        String val = getString(key);
        if(StringUtil.isEmpty(val)){
            return null;
        }
        return FastJsonUtils.toList(val, String.class);
    }

    public <T> T getBean(String key, Class<T> clz) {
        T bean = FastJsonUtils.toBean(JSON.toJSONString(get(key)), clz);
        if(bean != null){
            return bean;
        }
        try {
            T instance = clz.newInstance();
            invoke(instance, this, "");
            return instance;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }


    private void invoke(Object model, JcProperties properties, String prefix) throws
            IllegalAccessException, ClassNotFoundException, InstantiationException, ParseException {

        Class<?> clz = model.getClass();
        Field[] fields = model.getClass().getDeclaredFields();
        for (Field field : fields) {
            String type = field.getGenericType().toString();
            field.setAccessible(true);

            String curObjName = ConfigUtil.camelToMidline(lowerFirst(clz.getSimpleName()));

            prefix = StringUtil.isEmpty(prefix) ? curObjName : prefix;
            String fillName = !curObjName.equals(prefix) ? prefix +"."+ curObjName + "." + field.getName() : curObjName + "." + field.getName();


            switch (type){
                case "class java.lang.String":
                    field.set(model, properties.getString(fillName)) ;
                    break;
                case "byte":
                    field.setByte(model, Byte.valueOf(properties.getString(fillName)));
                    break;
                case "short":
                    field.setShort(model, Short.valueOf(properties.getString(fillName)));
                    break;
                case "int":
                    field.setInt(model, properties.getLong(fillName).intValue()) ;
                    break;
                case "long":
                    field.setLong(model, properties.getLong(fillName));
                    break;
                case "double":
                    field.setDouble(model, Double.valueOf(properties.getString(fillName)));
                    break;
                case "float":
                    field.setFloat(model, Float.valueOf(properties.getString(fillName)));
                    break;
                case "boolean":
                    field.setBoolean(model, Boolean.parseBoolean(properties.getString(fillName)));
                    break;
                case "class java.util.Date":
                    Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(properties.getString(fillName));
                    field.set(model,date) ;
                    break;
                default:
                    String tn = field.getType().getTypeName();
                    if("java.util.List".equals(tn)){
                        String val = properties.getString(fillName);
                        field.set(model,FastJsonUtils.toList(val, String.class));
                    }else if("java.util.Map".equals(tn)){
                        String val = properties.getString(fillName);
                        field.set(model,FastJsonUtils.toMap(val));
                    }else if(field.getType().isArray()){
                        String val = properties.getString(fillName);
                        field.set(model,FastJsonUtils.toArray(val));
                    }else{
                        String[] ar = type.split(" ");
                        Object tinyObj = Class.forName(ar[1]).newInstance();
                        invoke(tinyObj, properties, prefix);
                        field.set(model,tinyObj);
                    }
            }
        }

    }

}
