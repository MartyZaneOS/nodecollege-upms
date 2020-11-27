package com.nodecollege.cloud.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * excel 工具类
 * maven
 * <dependency>
 * <groupId>org.apache.poi</groupId>
 * <artifactId>poi</artifactId>
 * <version>3.9</version>
 * </dependency>
 * <dependency>
 * <groupId>org.apache.poi</groupId>
 * <artifactId>poi-ooxml</artifactId>
 * <version>3.9</version>
 * </dependency>
 *
 * @Author LC
 * @Date 2019/3/28 9:37
 */
public class ExcelUtils {
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

    /**
     * excel导出功能
     *
     * @param excelData      excel数据 ? 需要有get set 方法
     * @param excelTemplates excel模板
     * @param sheetName      sheet名称
     * @param excelVersion   excel版本
     * @return
     */
    public static Workbook export(List<?> excelData, List<ExcelTemplate> excelTemplates, String sheetName, String excelVersion) {
        logger.info("ExcelUtils.export 开始!");
        if (excelData != null && excelData.size() != 0 && excelData.get(0) instanceof Map) {
            throw new RuntimeException("请使用带get()、set()方法的实体类!!!");
        }
        Long start = System.currentTimeMillis();
        Workbook workbook = null;
        // 判断生成Excel的版本,默认生成.xls
        if (excelVersion == null || ExcelConstants.XLS.equals(excelVersion) || ExcelConstants.V2003.equals(excelVersion)) {
            workbook = new HSSFWorkbook();
        } else if (ExcelConstants.XLSX.equals(excelVersion) || ExcelConstants.V2007.equals(excelVersion)) {
            workbook = new XSSFWorkbook();
        }
        // 判断模版参数是否为空，为空返回一个空Execl工作簿
        if (excelTemplates == null || excelTemplates.isEmpty()) {
            return workbook;
        }

        // 根据orderNumber进行排序
        Collections.sort(excelTemplates);

        // 创建Sheet页
        Sheet sheet = workbook.createSheet(sheetName);

        // 创建导出Sheet页面第一行(主题行)
        Row topicRow = sheet.createRow(0);
        topicRow.setHeight((short) 600);
        CellStyle topicStyle = workbook.createCellStyle();
        topicStyle.setAlignment(HorizontalAlignment.CENTER);
        topicStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        // 设置粗体
        font.setBold(true);
        font.setColor(Font.COLOR_NORMAL);
        topicStyle.setFont(font);
        long l2 = System.currentTimeMillis();
        for (int i = 0; i < excelTemplates.size(); i++) {
            ExcelTemplate topic = excelTemplates.get(i);
            Cell cell = topicRow.createCell(i);
            cell.setCellValue(topic.getTitle());
            cell.setCellStyle(topicStyle);
            sheet.setColumnWidth(i, 256 * (int) (topic.getWidth() + 0.72));
        }
        logger.info("ExcelUtils.export 创建表头耗时【{}】毫秒", System.currentTimeMillis() - start);

        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.CENTER);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 自动换行样式
        dataStyle.setWrapText(true);
        long l3 = System.currentTimeMillis();
        // i: Execl行数
        for (int i = 0; i < excelData.size(); i++) {
            Row row = sheet.createRow(i + 1);
            BeanWrapperImpl wrapper = new BeanWrapperImpl(excelData.get(i));
            // j: Execl列数
            for (int j = 0; j < excelTemplates.size(); j++) {
                ExcelTemplate template = excelTemplates.get(j);
                String fieldName = template.getKey();
                // 判断是否是基础列
                Cell cell = row.createCell(j);
                Object fieldValue = null;
                try {
                    fieldValue = wrapper.getPropertyValue(fieldName);
                } catch (Exception e) {
                    // 这个Execption什么也不需要做
                }
                if (fieldValue != null) {
                    cell.setCellValue(fieldValue.toString());
                } else {
                    cell.setCellValue("");
                }
                cell.setCellStyle(dataStyle);
            }
        }
        // 模板说明
        Sheet describeSheet = workbook.createSheet("模板说明");
        Row topRow = describeSheet.createRow(0);
        Cell title0 = topRow.createCell(0);
        title0.setCellValue("字段名称");
        Cell must0 = topRow.createCell(1);
        must0.setCellValue("是否必填");
        Cell describe0 = topRow.createCell(2);
        describe0.setCellValue("字段描述");
        describeSheet.setColumnWidth(0, 256 * (int) (20 + 0.72));
        describeSheet.setColumnWidth(1, 256 * (int) (10 + 0.72));
        describeSheet.setColumnWidth(2, 256 * (int) (200 + 0.72));
        for (int i = 0; i < excelTemplates.size(); i++) {
            ExcelTemplate template = excelTemplates.get(i);
            Row row = describeSheet.createRow(i + 1);
            Cell title = row.createCell(0);
            title.setCellValue(template.getTitle());
            Cell must = row.createCell(1);
            must.setCellValue(template.getMust());
            Cell describe = row.createCell(2);
            describe.setCellValue(template.getDescribe());
        }
        logger.info("ExcelUtils.export 结束，耗时【{}】毫秒", System.currentTimeMillis() - start);
        return workbook;
    }

    /**
     * 导入excel数据
     *
     * @param workbook       excel 数据
     * @param excelTemplates excel 模板
     * @return 输出 List
     */
    public static <T> List<T> importObject(Workbook workbook, List<ExcelTemplate> excelTemplates, Class<T> tClass) {
        logger.info("ExcelUtils.importObject 开始！");
        Long start = System.currentTimeMillis();

        Sheet sheet = workbook.getSheetAt(0);

        if (sheet.getLastRowNum() <= 1) {
            throw new RuntimeException("excel 模板为空");
        }
        if (sheet.getLastRowNum() > 10001) {
            throw new RuntimeException("excel 模板导入数据不能大于10000条！");
        }

        // 清除excel模板的orderNumber字段，防止出现错误匹配
        for (ExcelTemplate excelTemplate : excelTemplates) {
            excelTemplate.setOrderNumber(null);
        }

        // excel表头和 excel模板title字段进行匹配，设置orderNumber
        Row row0 = sheet.getRow(0);
        // 匹配数
        int pps = 0;
        for (int i = 0; i <= row0.getLastCellNum(); i++) {
            String value = row0.getCell(i) + "".trim();
            for (ExcelTemplate excelTemplate : excelTemplates) {
                if (StringUtils.isEmpty(excelTemplate.getTitle())) {
                    throw new RuntimeException("excel 模板title属性不能为空！");
                }
                if (value.equals(excelTemplate.getTitle())) {
                    excelTemplate.setOrderNumber(i);
                    pps++;
                }
            }
        }
        logger.info("ExcelUtils.importObject excel表头和 excel模板title字段进行匹配，excel列数：{}，excel模板列数：{}，匹配数：{}，花费时间【{}】"
                , row0.getLastCellNum() + 1, excelTemplates.size(), pps, System.currentTimeMillis() - start);


        List<JSONObject> list = new ArrayList<>(sheet.getLastRowNum());
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            JSONObject jsonObject = new JSONObject();
            for (ExcelTemplate excelTemplate : excelTemplates) {
                if (excelTemplate.getOrderNumber() != null) {
                    jsonObject.put(excelTemplate.getKey(), row.getCell(excelTemplate.getOrderNumber()));
                }
            }
            list.add(jsonObject);
        }

        List<T> users = new ArrayList<>(list.size());
        for (JSONObject jsonObject : list) {
            T user = JSON.toJavaObject(jsonObject, tClass);
            users.add(user);
        }
        logger.info("ExcelUtils.importObject 结束，共花费【{}】毫秒！", System.currentTimeMillis() - start);
        return users;
    }

    // 使用样例
    public static void main(String[] args) throws IOException {

        List<ExcelTemplate> excelTemplates = new ArrayList<>();
        excelTemplates.add(new ExcelTemplate(1, 20, true, "姓名", "name", "姓名描述"));
        excelTemplates.add(new ExcelTemplate(2, 20, true, "电话", "phone", "电话描述"));
        excelTemplates.add(new ExcelTemplate(3, 20, true, "邮箱", "email", "邮箱描述"));
        excelTemplates.add(new ExcelTemplate(4, 20, true, "年龄", "age", "年龄描述"));
        excelTemplates.add(new ExcelTemplate(5, 20, true, "其他", "other", "其他描述"));

        // 有效数据
        User user1 = new User();
        user1.setName("张三");
        user1.setAge("28");
        user1.setPhone("12345");

        User user2 = new User();
        user2.setName("李四");
        user2.setAge("26");
        user2.setPhone("67890");

        List<User> excelData = new ArrayList<>();
        excelData.add(user1);
        excelData.add(user2);

        // 导出功能
        Workbook workbook = ExcelUtils.export(excelData, excelTemplates, "花名册", null);

        File file = new File("d:\\花名册.xls");
        FileOutputStream fos = new FileOutputStream(file);
        workbook.write(fos);
        fos.flush();
        fos.close();

        // 导入功能 得到list数据
        List<User> userLists = ExcelUtils.importObject(workbook, excelTemplates, User.class);

        logger.info(JSONObject.toJSONString(userLists, true));
    }

    /**
     * 导入时根据 title 字段与 excel 表格的表格头进行匹配
     * 导出时根据 orderNumber 字段作为列的排序依据
     * excel 模板
     */
    @Data
    public static class ExcelTemplate implements Comparable<ExcelTemplate> {
        /*
         * 字段名称
         * 导入时此字段和excel表头字段一一对应
         * 导出时此字段时excel表头描述
         */
        private String title;

        /*
         * 字段key
         * 映射实体key
         */
        private String key;

        /*
         * 序号
         * 导入时此字段无效，会被清空
         * 导出时作为列的排序依据
         */
        private Integer orderNumber;

        private Integer width;

        /**
         * 是否必填
         */
        private Boolean must;
        /**
         * 描述
         */
        private String describe;

        public ExcelTemplate() {
        }

        public ExcelTemplate(Integer order, Integer width, Boolean must, String title, String key, String describe) {
            this.title = title;
            this.key = key;
            this.orderNumber = order;
            this.width = width;
            this.must = must;
            this.describe = describe;
        }

        @Override
        public int compareTo(ExcelTemplate o) {
            if (this.getOrderNumber() >= o.getOrderNumber()) {
                return 1;
            }
            return -1;
        }
    }

    /**
     * excel常量
     */
    static class ExcelConstants {
        // 2003版Excel
        public static final String V2003 = "2003";
        // 2007版Excel
        public static final String V2007 = "2007";
        // 2003版Excel，文件后缀为.xls
        public static final String XLS = ".xls";
        // 2007版Excel，文件后缀为.xlsx
        public static final String XLSX = ".xlsx";
    }

    // 测试业务实体
    @Data
    static class User {
        private String name;
        private String age;
        private String phone;
        private String email;
        private String other;
    }

    /**
     * 描述：根据文件后缀，自适应上传文件的版本
     *
     * @param inStr,fileName
     * @return
     * @throws Exception
     */
    public static Workbook getWorkbook(InputStream inStr, String fileName) throws Exception {
        Workbook wb = null;
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        if (ExcelConstants.XLS.equals(fileType)) {
            wb = new HSSFWorkbook(inStr);  //2003-
        } else if (ExcelConstants.XLSX.equals(fileType)) {
            wb = new XSSFWorkbook(inStr);  //2007+
        } else {
            throw new Exception("解析的文件格式有误！");
        }
        return wb;
    }

    /**
     * 解析数据，得到list数据
     *
     * @param workbook
     * @param excelTemplates
     * @return
     */
    public static List<JSONObject> importObjectByOrderNumber(Workbook workbook, List<ExcelTemplate> excelTemplates) {
        logger.info("ExcelUtils.importObject 开始！");
        Long start = System.currentTimeMillis();

        Sheet sheet = workbook.getSheetAt(0);

        if (sheet.getLastRowNum() < 1) {
            throw new RuntimeException("excel 模板为空");
        }
        if (sheet.getLastRowNum() > 10001) {
            throw new RuntimeException("excel 模板导入数据不能大于10000条！");
        }

        List<JSONObject> list = new ArrayList<>(sheet.getLastRowNum());
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            JSONObject jsonObject = new JSONObject();
            for (int j = 0; j < excelTemplates.size(); j++) {
                jsonObject.put(excelTemplates.get(j).getKey(), getCellValue(row.getCell(j)));
            }
            list.add(jsonObject);
        }
        logger.info("ExcelUtils.importObject 结束，共花费【{}】毫秒！", System.currentTimeMillis() - start);
        return list;
    }

    public static String getCellValue(Cell cell) {
        String cellValue = "";
        if (cell == null) {
            return cellValue;
        }
        // 判断数据的类型
        switch (cell.getCellType()) {//CELL_TYPE_NUMERIC
            case NUMERIC: // 数字
                //short s = cell.getCellStyle().getDataFormat();
                if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
                    SimpleDateFormat sdf = null;
                    // 验证short值
                    if (cell.getCellStyle().getDataFormat() == 14) {
                        sdf = new SimpleDateFormat("yyyy/MM/dd");
                    } else if (cell.getCellStyle().getDataFormat() == 21) {
                        sdf = new SimpleDateFormat("HH:mm:ss");
                    } else if (cell.getCellStyle().getDataFormat() == 22) {
                        sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    } else {
                        throw new RuntimeException("日期格式错误!!!");
                    }
                    Date date = cell.getDateCellValue();
                    cellValue = sdf.format(date);
                } else {
//                    cellValue = new DecimalFormat("#,#0.000000").format(cell.getNumericCellValue());
                    // 返回数值类型的值
                    Object inputValue = null;// 单元格值
                    Long longVal = Math.round(cell.getNumericCellValue());
                    Double doubleVal = cell.getNumericCellValue();
                    if (Double.parseDouble(longVal + ".0") == doubleVal) {   //判断是否含有小数位.0
                        inputValue = longVal;
                    } else {
                        inputValue = doubleVal;
                    }
                    DecimalFormat df = new DecimalFormat("#.####");    //格式化为四位小数，按自己需求选择；
                    cellValue = df.format(inputValue);
                }
                break;
            case STRING: // 字符串
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            case BOOLEAN: // Boolean
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case FORMULA: // 公式
                cellValue = String.valueOf(cell.getCellFormula());
                break;
            case BLANK: // 空值
                cellValue = null;
                break;
            case ERROR: // 故障
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        return cellValue;
    }
}
