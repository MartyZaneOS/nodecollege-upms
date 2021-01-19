package com.nodecollege.test;

import com.alibaba.fastjson.JSONObject;
import org.json.JSONArray;

import java.util.*;

/**
 * @author LC
 * @date 2020/12/21 20:27
 */
public class HWTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int p = sc.nextInt();
        int x = sc.nextInt();
        System.out.println(p + "_" + x);
        Integer[][] list = new Integer[p][x];
        for(int i = 0; i < p; i++){
            for(int j = 0; j < x; j++){
                System.out.println("i j " + i + "_" + j);
                x = sc.nextInt();
                list[i][j] = x;
            }
            System.out.println(JSONObject.toJSONString(list[i]));
        }

//        String[] list = new String[]{"4,5","10,6,9,7,6","9,10,6,7,5","8,10,6,5,10","9,10,8,4,9"};
        main1(list, p, x);
    }

    public static void main1(Integer[][] args, Integer y, Integer x) {
        if (args.length != y || args.length < 3) {
            System.out.println(-1);
            return;
        }
        List<Map<String, Integer>> tempList = new ArrayList<>();
        for (int i = 0; i < x; i++) {
            Map<String, Integer> map = new HashMap<>();
            map.put("zf", 0);
            tempList.add(map);
        }
        for (int i = 1; i <= y; i++) {
            Integer[] hTemp = args[i];
            if (hTemp.length != x || hTemp.length < 3) {
                System.out.println(-1);
                return;
            }
            for (int j = 0; j < x; j++) {
                Integer value = hTemp[j];
                if (value > 10) {
                    System.out.println(-1);
                    return;
                }
                tempList.get(j).put("zf", tempList.get(i).get("zf") + value);
                tempList.get(j).put("wz", j);
                if (!tempList.get(j).containsKey(value + "")) {
                    tempList.get(j).put(value + "", 0);
                }
                tempList.get(j).put(value + "", tempList.get(j).get(value + "") + 1);
            }
        }

        Collections.sort(tempList, (a,b) -> {
            if (a.get("zf") == b.get("zf")) {
                return bj(a, b, 10);
            }
            return b.get("zf") - a.get("zf");
        });
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            res.append(tempList.get(i).get("wz") + 1 + ",");
        }
        System.out.println(res.toString().substring(0, res.length() - 1));
    }

    private static Integer bj(Map<String, Integer> a, Map<String, Integer> b, Integer num) {
        if (num == 0) {
            System.out.println(-1);
            return 0;
        }
        String key = num + "";
        if (a.containsKey(key) && b.containsKey(key)) {
            if (a.get(key) == b.get(key)) {
                return bj(a, b, --num);
            }
            return b.get(key) - a.get(key);
        }
        if (!a.containsKey(key) && !b.containsKey(key)) {
            return bj(a, b, --num);
        }
        if (a.containsKey(key)) {
            return 1;
        } else {
            return -1;
        }
    }
}
