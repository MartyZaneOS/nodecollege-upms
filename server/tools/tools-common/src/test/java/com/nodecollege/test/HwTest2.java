package com.nodecollege.test;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * @author LC
 * @date 2020/12/21 21:54
 */
public class HwTest2 {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        List<Integer> list = new ArrayList<>();
        while (in.hasNextInt()) {// 注意，如果输入是多个测试用例，请通过while循环处理多个测试用例
            int a = in.nextInt();
            list.add(a);
        }
        Collections.sort(list, (a, b) -> b - a);
        System.out.println(JSONObject.toJSONString(list));

        // 边界 list.size()层
        int start = 0;
        int end = list.size();
        while (start < end) {

        }

    }

    private static Integer test(List<Integer> list, int start, int end) {
        Integer bottom = list.get(start);
        while (start < end) {
            if (list.get(start + 1) + list.get(end) > bottom) {
                // 第二块和最后一块拼起来大于第一块
                return -1;
            }
            if (list.get(start + 1) + list.get(end) == bottom) {
                start++;
                end--;
                continue;
            }
            if (list.get(start + 1) + list.get(end) < bottom) {

            }
        }
        return 1;
    }
}
