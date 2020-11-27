package com.nodecollege.cloud.common.utils;

public class NCStringUtils {

    public static final char UNDERLINE = '_';

    public static void main(String[] args) {

        /*驼峰转下划线*/
        String str = "   itemName    bbbbCsss     ,  \n" +
                "    category     ,\n" +
                "\tbarCode\t \n" +
                "\tvolume\t  \n" +
                "\tlength\t  \n" +
                "\twidth\t \n" +
                "\theight\t \n" +
                "\tunitPrice\t \n" +
                "\tshelfLife\t \n" +
                "\tshelfLifeUnit\t  /\n" +
                "\t ）";

        /*下划线转驼峰*/
        String str2 = " ITEM_NAME         ,  \n" +
                "    CATEGORY     ,\n" +
                "\tBAR_CODE\t \n" +
                "\tVOLUME\t  \n" +
                "\tLENGTH\t  \n" +
                "\tWIDTH\t \n" +
                "\tHEIGHT\t \n" +
                "\tUNIT_PRICE\t \n" +
                "\tSHELF_LIFE\t \n" +
                "\tSHELF_LIFE_UNIT\t";

        /**
         * 测试
         * */
        /* charType=2 表示大写, 其他情况都是小写*/
        //  下划线大写:ABC_DEF
        String STR_ABC = camelToUnderline(str, 2);
        //  下划线小写:abc_def
        String str_abc = camelToUnderline(str, 1);
        System.out.println("驼峰转化成下划线大写 :" + STR_ABC);
        System.out.println("驼峰转化成下划线小写 :" + str_abc);

        //  下划线转驼峰:abcDef
        String strAbc = underlineToCamel(str2);
        System.out.println("下划线化成驼峰 :" + strAbc);

    }


    //驼峰转下划线
    public static String camelToUnderline(String param, Integer charType) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        param = param.replaceAll(" ", "");
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(UNDERLINE);
            }
            if (charType == 2) {
                //统一都转大写
                sb.append(Character.toUpperCase(c));
            } else {
                //统一都转小写
                sb.append(Character.toLowerCase(c));
            }


        }
        return sb.toString();
    }

    //下划线转驼峰
    public static String underlineToCamel(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        // "_" 后转大写标志,默认字符前面没有"_"
        Boolean flag = false;
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (c == UNDERLINE) {
                flag = true;
                continue;
            } else {
                if (flag == true) {
                    //表示当前字符前面是"_" ,当前字符转大写
                    sb.append(Character.toUpperCase(param.charAt(i)));
                    flag = false;
                } else {
                    sb.append(Character.toLowerCase(param.charAt(i)));
                }
            }
        }
        return sb.toString();
    }


}