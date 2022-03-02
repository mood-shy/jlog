package com.jd.platform.jlog.common.utils;

import com.alibaba.fastjson.JSON;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author tangbohu
 * @version 1.0.0
 * @ClassName CollectionUtil.java
 * @Description TODO
 * @createTime 2022年02月21日 17:14:00
 */
public class CollectionUtil {

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return collection != null && collection.isEmpty();
    }


    public static boolean isEmpty(Map<?, ?> map) {
        return !isNotEmpty(map);
    }


    public static boolean isNotEmpty(Map<?, ?> map) {
        return map != null && !map.isEmpty();
    }


    public static <T> boolean equals(List<T> c1, List<T> c2) {
        if (c1 == null && c2 == null) {
            return true;
        } else if (c1 != null && c2 != null) {
            if (c1.size() != c2.size()) {
                return false;
            } else {
                for(int i = 0; i < c1.size(); ++i) {
                    if (!Objects.equals(c1.get(i), c2.get(i))) {
                        return false;
                    }
                }

                return true;
            }
        } else {
            return false;
        }
    }

    public static <K, V> boolean equals(Map<K, V> c1, Map<K, V> c2) {
        if (c1 == null && c2 == null) {
            return true;
        } else if (c1 != null && c2 != null) {
            if (c1.size() != c2.size()) {
                return false;
            } else {
                Iterator var2 = c1.entrySet().iterator();

                Object v1;
                Object v2;
                do {
                    if (!var2.hasNext()) {
                        return true;
                    }

                    Map.Entry<K, V> entry = (Map.Entry)var2.next();
                    K k = entry.getKey();
                    v1 = entry.getValue();
                    v2 = c2.get(k);
                } while(Objects.equals(v1, v2));

                return false;
            }
        } else {
            return false;
        }
    }





    public static <K, V> Set<String> diffKeys(Map<K, V> m1, Map<K, V> m2){

        Set<String> diff = new HashSet<>(1);

        for (Map.Entry<K,V> kvEntry : m1.entrySet()) {
            V val = m2.get(kvEntry.getKey());
            if(!kvEntry.getValue().equals(val)){
                diff.add(kvEntry.getKey().toString());
            }
        }

        for (Map.Entry<K,V> kvEntry : m2.entrySet()) {
            V val = m1.get(kvEntry.getKey());
            if(!kvEntry.getValue().equals(val)){
                diff.add(kvEntry.getKey().toString());
            }
        }
        return diff;
    }




    public static <K, V> HashMap<K,V> diffMap(Map<K, V> m1, Map<K, V> m2){

        HashMap<K, V> diff = new HashMap<>(1);

        for (Map.Entry<K,V> kvEntry : m1.entrySet()) {
            V val = m2.get(kvEntry.getKey());
            if(!kvEntry.getValue().equals(val)){
                diff.put(kvEntry.getKey(), kvEntry.getValue());
            }
        }

        for (Map.Entry<K,V> kvEntry : m2.entrySet()) {
            V val = m1.get(kvEntry.getKey());
            if(!kvEntry.getValue().equals(val)){
                diff.put(kvEntry.getKey(), kvEntry.getValue());
            }
        }
        return diff;
    }


    public static void main(String[] args) {
        HashMap<String, Integer> m1 = new HashMap<>();
        m1.put("t1",1);
        m1.put("t2",2);

        HashMap<String, Integer> m2 = new HashMap<>();
        m2.put("t2",2);
        m2.put("t3",3);

    }
}
