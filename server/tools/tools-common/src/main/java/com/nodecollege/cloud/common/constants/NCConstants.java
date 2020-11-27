package com.nodecollege.cloud.common.constants;

import org.springframework.util.StringUtils;

/**
 * @author LC
 * @date 2019/11/8 21:34
 */
public class NCConstants {

    public final static Long LONG_NEGATIVE_1 = -1L;
    public final static Long LONG_0 = 0L;
    public final static Long LONG_1 = 1L;
    public final static Long LONG_30 = 30L;
    public final static Long LONG_1800 = 1800L;
    public final static String STRING_1 = "1";
    public final static String STRING_0 = "0";
    public final static String HYPHEN = "-";
    public final static String EMPTY_STRING = "";
    public final static String NULL_STRING = "null";
    public final static String UNKNOWN = "unknown";
    public final static Integer INT_NEGATIVE_1 = -1;
    public final static Integer INT_0 = 0;
    public final static Integer INT_1 = 1;
    public final static Integer INT_2 = 2;
    public final static Integer INT_3 = 3;
    public final static Integer INT_4 = 4;
    public final static Integer INT_5 = 5;
    public final static Integer INT_6 = 6;
    public final static Integer INT_7 = 7;
    public final static Integer INT_8 = 8;
    public final static Integer INT_9 = 9;
    public final static Integer INT_24 = 24;
    public final static Integer INT_32 = 32;
    public final static Integer INT_100 = 100;
    public final static Integer INT_1024 = 1024;

    public final static String THREAD_ID = "threadId";
    public final static String NC_THREAD_ID = "NC_THREAD_ID";
    public final static String THREAD_ID2 = "threadId2";
    public final static String NC_THREAD_ID2 = "NC_THREAD_ID2";
    public final static String REQUEST_ID = "requestId";
    public final static String NC_REQUEST_ID = "NC_REQUEST_ID";
    /**
     * 管理员登陆信息 token：UpmsAdmin
     */
    public static final String ADMIN_LOGIN_INFO = "admin:login:info:";
    /**
     * 用户登陆信息 token: UpmsUser
     */
    public static final String USER_LOGIN_INFO = "user:login:info:";
    /**
     * 升序
     */
    public final static String ASC = "ASC";
    /**
     * 降序
     **/
    public final static String DESC = "DESC";

    /**
     * 编码
     */
    public interface CHARSET {
        String UTF_8 = "utf-8";
    }

    /**
     * 路径分隔符
     */
    public static final String SLASH = "/";

    /**
     * 系统变量名称
     */
    public static final String SYSTEM_PROPERTY_OS_NAME = "os.name";


    /**
     * session使用
     */
    public interface USER_SESSION {

        String SESSION_NAMESPACE = "user_token";

        /**
         * redis中存储的key前缀-admin
         */
        String SESSION_ADMIN_PREFIX = SESSION_NAMESPACE + ":admin:";

        /**
         * redis中存储的key前缀-app
         */
        String SESSION_APP_PREFIX = SESSION_NAMESPACE + ":app:";

        /**
         * redis中存储的key前缀-api
         */
        String SESSION_API_PREFIX = SESSION_NAMESPACE + ":api:";

        /**
         * 权限初始化加载
         */
        String ROLE_AUTHORITY = SESSION_ADMIN_PREFIX + "authority:";

    }

    /**
     * 图片验证码
     */
    public static final String REGISTER_IMG_CODE = "imgcode:";
    /**
     * 手机验证码
     */
    public static final String REGISTER_PHONE_CODE = "phonecode:";
    /**
     * header使用的token规则
     */
    public static final String HEADER_ACCESS_TOKEN = "access-token";


    public interface FILE {
        String PARAM_FILE = "file";
        /**
         * 单个上传文档的最大大小，默认设置为10M。
         */
        int UPLOAD_FILE_MAX_SIZE = 26214400;

        int FILE_MAX_SIZE = 20971520;

        int FILE_SIGN_MAX_SIZE = 26214400;
        /**
         * 版式类型pdf
         */
        int LAYOUT_TYPE_PDF = 1;
        /**
         * 版式类型pdf
         */
        String LAYOUT_PDF_EXTENSION = FILE.SUFFIX_EXTENSION_PDF;
        /**
         * 版式类型ofd
         */
        int LAYOUT_TYPE_OFD = 2;
        /**
         * 版式类型PNG
         */
        int LAYOUT_TYPE_PNG = 3;
        /**
         * 版式类型JPG
         */
        int LAYOUT_TYPE_JPG = 4;
        /**
         * json类型
         */
        String MIME_TYPE_JSON = "application/json;charset=UTF-8";
        String MIME_TYPE_PDF = "application/pdf";
        String MIME_TYPE_OFD = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        String MIME_TYPE_PNG = "image/png";
        String MIME_TYPE_JPG_JPEG = "image/jpeg";
        /**
         * mime type：doc
         */
        String MIME_TYPE_DOC = "application/msword";
        /**
         * mime type：docx
         */
        String MIME_TYPE_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        /**
         * mime type：wps
         */
        String MIME_TYPE_WPS = "application/vnd.ms-works";
        /**
         * mime type：xls
         */
        String MIME_TYPE_XLS = "application/vnd.ms-excel";
        /**
         * mime type：xlsx
         */
        String MIME_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        /**
         * mime type：wps-xls(et),wps-xlsx(ett)
         */
        String MIME_TYPE_WPS_XLS = "application/octet-stream";
        /**
         * 普通文本文件
         */
        String MIME_TYPE_TEXT_PLAIN = "text/plain";
        /**
         * PDF文件
         */
        String MIME_TYPE_APPLICATION_PDF = "application/pdf";
        /**
         * OFD文件,也有可能是普通文本文件
         */
        String MIME_TYPE_APPLICATION_OCTET_STREAM = "application/octet-stream";
        /**
         * OFD文件，也有可能类型是这种格式
         */
        String MIME_TYPE_APPLICATION_OFD = "application/ofd";
        /**
         * OFD文件(win7/8)
         */
        String MIME_TYPE_APPLICATION_VND_OFD = "application/vnd.ofd";
        /**
         * OFD文件(ie10)
         */
        String MIME_TYPE_APPLICATION_IE10_OFD = "application/x-zip-compressed";
        String PATH_JOINT = "_";
        int PATH_RANDOM_NUMBER = 10000;
        /**
         * pdf文件类型
         */
        String SUFFIX_EXTENSION_PDF = ".pdf";
        /**
         * ODF文件类型
         */
        String SUFFIX_EXTENSION_OFD = ".ofd";
        /**
         * jpg文件类型
         */
        String SUFFIX_EXTENSION_JPG = ".jpg";
        /**
         * jpeg文件类型
         */
        String SUFFIX_EXTENSION_JPEP = ".jpeg";
        /**
         * svg文件类型
         */
        String SUFFIX_EXTENSION_SVG = ".svg";
        /**
         * png文件类型
         */
        String SUFFIX_EXTENSION_PNG = ".png";
        /**
         * doc文件类型
         */
        String SUFFIX_EXTENSION_DOC = ".doc";
        /**
         * docx文件类型
         */
        String SUFFIX_EXTENSION_DOCX = ".docx";
        /**
         * wps文件类型
         */
        String SUFFIX_EXTENSION_WPS = ".wps";
        /**
         * xls文件类型
         */
        String SUFFIX_EXTENSION_XLS = ".xls";
        /**
         * xlsx文件类型
         */
        String SUFFIX_EXTENSION_XLSX = ".xlsx";
        /**
         * xlsx-wps-et文件类型
         */
        String SUFFIX_EXTENSION_WPS_XLS_ET = ".et";
        /**
         * xlsx-wps-ett文件类型
         */
        String SUFFIX_EXTENSION_WPS_XLS_ETT = ".ett";
        /**
         * TXT文件类型
         */
        String SUFFIX_EXTENSION_TXT = ".txt";
        /**
         * html文件类型
         */
        String SUFFIX_EXTENSION_HTML = ".html";
        /**
         * ppt文件类型
         */
        String SUFFIX_EXTENSION_PPT = ".ppt";
        /**
         * pptx文件类型
         */
        String SUFFIX_EXTENSION_PPTX = ".pptx";
    }

    /**
     * 时区
     */
    public interface TIME_ZONE {
        /**
         * 东8区
         */
        String SHANGHAI = "GMT+8";
    }

    /**
     * 标点符号定义区
     *
     * @author zhaojy
     * @date 2019-03-22
     */
    public interface PUNCTUATION {
        /**
         * 逗号：英文版
         */
        String SYMBOL_COMMA_EN = ",";

        /**
         * 句号：英文
         */
        String SYMBOL_PERIOD_EN = ".";
    }

}
