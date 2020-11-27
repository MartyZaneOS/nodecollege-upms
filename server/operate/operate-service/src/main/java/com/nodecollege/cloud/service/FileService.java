package com.nodecollege.cloud.service;

import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author LC
 * @date 2020/3/21 15:16
 */
public interface FileService {

    /**
     * 上传图片
     *
     * @param file     文件
     * @param operateFile 文件信息
     * @return 文件信息
     */
    OperateFile uploadImg(MultipartFile file, OperateFile operateFile);

    /**
     * 上传图片 生成缩略图
     *
     * @param file     文件
     * @param operateFile 文件信息
     * @return 文件信息
     */
    OperateFile uploadImgThumb(MultipartFile file, OperateFile operateFile);

    /**
     * 上传文件
     * @param base64
     * @param operateFile
     * @return
     */
    OperateFile uploadFile(String base64, OperateFile operateFile);

    /**
     * 获取文件列表
     */
    List<OperateFile> getFileList(QueryVO<OperateFile> queryVO);

    /**
     * 删除文件
     *
     * @param operateFile  文件信息
     */
    void delFile(OperateFile operateFile);

    /**
     * 下载文件
     */
    String downloadFile(String path);
}
