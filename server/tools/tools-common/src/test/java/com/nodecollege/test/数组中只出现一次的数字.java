package com.nodecollege.test;

import com.alibaba.fastjson.JSONObject;

/**
 * @author LC
 * @date 2020/12/28 14:58
 */
public class 数组中只出现一次的数字 {
    public static void main(String[] args) {
        int[] s = new int[]{1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7};
        int res = 0;
        for (int i = 0; i < s.length; i++) {
            res = res ^ s[i];
            System.out.println("i= " + i + " res= " + res);
        }
        System.out.println(res);
        int[] num1 = new int[1];
        int[] num2 = new int[1];
        FindNumsAppearOnce_2(s, num1, num2);
        System.out.println(JSONObject.toJSONString(num1));
        System.out.println(JSONObject.toJSONString(num2));
    }

    public static void FindNumsAppearOnce_2(int [] array,int num1[] , int num2[]) {
        if(array == null || array.length == 0) {
            return;
        }
        if(array.length == 2) {
            num1[0] = array[0];
            num2[0] = array[1];
        }

        int bitResult = 0;
        for (int i = 0; i < array.length; i++) {
            bitResult ^= array[i];
        }
        int index = findFirst1(bitResult);
        for (int i = 0; i < array.length; i++) {
            if(isBit1(array[i], index)) {
                num1[0] ^= array[i];
            }
            else {
                num2[0] ^= array[i];
            }
        }

    }

    private static boolean isBit1(int target, int index) {
        return ((target >> index) & 1) == 1;
    }

    private static int findFirst1(int bitResult) {
        int index = 0;
        while(((bitResult & 1) == 0) && index < 32) {
            bitResult >>= 1;
            index++;
        }
        return index;
    }
}
