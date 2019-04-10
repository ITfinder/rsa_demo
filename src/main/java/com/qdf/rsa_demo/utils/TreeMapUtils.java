package com.qdf.rsa_demo.utils;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class TreeMapUtils {
    public static Map<String, Object> sortMapByKey(Map<String, Object> map)
    {
        if (map == null || map.isEmpty())
        {
            return null;
        }

        Map<String, Object> sortMap = new TreeMap<String, Object>(
                new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o1.compareTo(o2);
                    }

                });

        sortMap.putAll(map);

        return sortMap;
    }

    public static void main(String[] args) throws IOException {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("yas", "a");
        map.put("ae", "b");
        map.put("sd", "c");
        map.put("aes", "d");
        map.put("aee", "e");
        map = sortMapByKey(map);
        for(Entry<String, Object> entry:map.entrySet()){
            System.out.println("key:"+entry.getKey()+"--value:"+entry.getValue());
        }
//		String string2 = JSON.json(map);
//		System.out.println(string2);
    }

}
