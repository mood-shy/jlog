package com.jd.platform.jlog.common.utils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

}
