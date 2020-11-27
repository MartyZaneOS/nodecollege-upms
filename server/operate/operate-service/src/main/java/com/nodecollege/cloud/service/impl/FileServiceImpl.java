package com.nodecollege.cloud.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nodecollege.cloud.client.FastDFSClient;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateFile;
import com.nodecollege.cloud.common.utils.NCUtils;
import com.nodecollege.cloud.dao.mapper.OperateFileMapper;
import com.nodecollege.cloud.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

/**
 * 文件处理
 *
 * @author LC
 * @date 2020/3/21 15:28
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Value("${fdfsGroup:/ncimg/}")
    private String fdfsGroup;

    @Autowired
    private FastDFSClient fastDFSClient;

    @Autowired
    private OperateFileMapper fileMapper;

    /**
     * 上传文件
     *
     * @param file     文件
     * @param operateFile 文件信息
     */
    @Override
    public OperateFile uploadImg(MultipartFile file, OperateFile operateFile) {
        NCUtils.nullOrEmptyThrow(file, new NCException("-1", "参数缺失！"));
        checkFile(operateFile);
        String filePath = fastDFSClient.uploadFile(file);
        operateFile.setFilePath(fdfsGroup + filePath);
        return insertFile(operateFile);
    }

    /**
     * 上传文件并生成缩略图
     *
     * @param file     文件
     * @param operateFile 文件信息
     */
    @Override
    public OperateFile uploadImgThumb(MultipartFile file, OperateFile operateFile) {
        NCUtils.nullOrEmptyThrow(file, new NCException("-1", "参数缺失！"));
        checkFile(operateFile);
        String filePath = fastDFSClient.uploadFileThumb(file);
        operateFile.setFilePath(fdfsGroup + filePath);
        return insertFile(operateFile);
    }

    /**
     * 上传文件
     *
     * @param base64   文件
     * @param operateFile 文件信息
     */
    @Override
    public OperateFile uploadFile(String base64, OperateFile operateFile) {
        NCUtils.nullOrEmptyThrow(base64, new NCException("-1", "参数缺失！"));
        checkFile(operateFile);
        String fileExtension = "txt";
        String fileName = operateFile.getFileName();
        if (NCUtils.isNotNullOrNotEmpty(fileName) && fileName.lastIndexOf(".") > 0) {
            fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        String filePath = fastDFSClient.uploadBase64(base64, fileExtension);
        operateFile.setFilePath(fdfsGroup + filePath);
        return insertFile(operateFile);
    }

    /**
     * 查询文件列表
     *
     * @param queryVO
     * @return
     */
    @Override
    public List<OperateFile> getFileList(QueryVO<OperateFile> queryVO) {
        return fileMapper.selectListByMap(queryVO.toMap());
    }

    /**
     * 校验文件信息
     *
     * @param operateFile
     */
    private void checkFile(OperateFile operateFile) {
        NCUtils.nullOrEmptyThrow(operateFile, new NCException("-1", "参数缺失！"));
        NCUtils.nullOrEmptyThrow(operateFile.getUserId(), new NCException("-1", "用户id不能为空！"));
        NCUtils.nullOrEmptyThrow(operateFile.getFileType(), new NCException("-1", "文件类型不能为空！"));
        NCUtils.nullOrEmptyThrow(operateFile.getFileName(), new NCException("-1", "文件名称不能为空！"));
    }

    /**
     * 插入记录
     *
     * @param operateFile
     * @return
     */
    private OperateFile insertFile(OperateFile operateFile) {
        operateFile.setCreateTime(new Date());
        if (NCUtils.isNullOrEmpty(operateFile.getFileName())) {
            operateFile.setFileName(operateFile.getFilePath());
        }
        fileMapper.insertSelective(operateFile);
        return operateFile;
    }

    /**
     * 删除文件
     *
     * @param operateFile 文件信息
     */
    @Override
    public void delFile(OperateFile operateFile) {
        NCUtils.nullOrEmptyThrow(operateFile, new NCException("", "参数缺失！"));
        NCUtils.nullOrEmptyThrow(operateFile.getFileId(), new NCException("", "文件主键不能为空！"));
        if (NCUtils.isNullOrEmpty(operateFile.getUserId()) && NCUtils.isNullOrEmpty(operateFile.getTenantId())) {
            throw new NCException("-1", "用户id或者租户id不能为空！");
        }
        OperateFile ex = fileMapper.selectByPrimaryKey(operateFile.getFileId());
        if (ex == null) {
            // 文件不存在
            return;
        }
        if (NCUtils.isNotNullOrNotEmpty(ex.getTenantId())) {
            if (!ex.getTenantId().equals(operateFile.getTenantId())) {
                throw new NCException("-1", "无权删除该文件！");
            }
        } else {
            if (!ex.getUserId().equals(operateFile.getUserId())) {
                throw new NCException("-1", "只能删除自己的文件数据！");
            }
        }
        fileMapper.deleteByPrimaryKey(operateFile.getFileId());
        fastDFSClient.deleteFile(ex.getFilePath());
    }

    /**
     * 下载文件
     */
    public String downloadFile(String filePath) {
        String path = filePath.substring(fdfsGroup.length());
        return fastDFSClient.downloadFile(fdfsGroup, path);
    }
}
