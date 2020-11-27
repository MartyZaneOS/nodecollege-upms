package com.nodecollege.cloud.client;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.fdfs.ThumbImageConfig;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadCallback;
import com.github.tobato.fastdfs.exception.FdfsUnsupportStorePathException;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.nodecollege.cloud.common.exception.NCException;
import com.nodecollege.cloud.common.utils.NCUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * fastDFS客户端
 */
@Slf4j
@Component
public class FastDFSClient {

    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private ThumbImageConfig thumbImageConfig;

    /**
     * 上传文件
     *
     * @param file 文件对象
     * @return 文件访问地址
     */
    public String uploadFile(MultipartFile file) {
        StorePath storePath = null;
        try {
            storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(),
                    FilenameUtils.getExtension(file.getOriginalFilename()), null);
        } catch (IOException e) {
            log.error("上传文件失败！", e);
            throw new NCException("-1", "io异常");
        }
        // 不带分组的路径
        return storePath.getPath();
    }

    /**
     * 上传文件并生成缩略图
     *
     * @param file 文件
     * @return 文件访问地址
     */
    public String uploadFileThumb(MultipartFile file) {
        StorePath storePath = null;
        try {
            storePath = storageClient.uploadImageAndCrtThumbImage(file.getInputStream(), file.getSize(),
                    FilenameUtils.getExtension(file.getOriginalFilename()), null);
        } catch (IOException e) {
            log.error("上传文件失败！", e);
            throw new NCException("-1", "io异常");
        }
        // 不带分组的路径
        log.info("缩略图 {}", thumbImageConfig.getThumbImagePath(storePath.getPath()));
        return storePath.getPath();
    }

    /**
     * 获取缩略图路径
     *
     * @param path 不带分组的路径
     * @return 缩略图路径
     */
    public String getThumbPath(String path) {
        return thumbImageConfig.getThumbImagePath(path);
    }

    /**
     * 将一段字符串生成一个文件上传
     *
     * @param base64        文件内容
     * @param fileExtension 文件后缀
     */
    public String uploadBase64(String base64, String fileExtension) {
        byte[] buff = base64.getBytes(Charset.forName("UTF-8"));
        ByteArrayInputStream stream = new ByteArrayInputStream(buff);
        StorePath storePath = storageClient.uploadFile(stream, buff.length, fileExtension, null);
        return storePath.getPath();
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件访问地址
     */
    public void deleteFile(String fileUrl) {
        if (StringUtils.isEmpty(fileUrl)) {
            return;
        }
        try {
            storageClient.deleteFile(fileUrl);
        } catch (FdfsUnsupportStorePathException e) {
            log.error("删除文件出错！", e);
            throw new NCException("-1", "删除文件出错！");
        }
    }

    /**
     * 下载文件
     *
     * @param groupName
     * @param path
     */
    public String downloadFile(String groupName, String path) {
        NCUtils.nullOrEmptyThrow(groupName, "-1", "参数缺失！");
        NCUtils.nullOrEmptyThrow(path, "-1", "参数缺失！");
        String base64 = "";
        try {
            base64 = storageClient.downloadFile("ncimg", path, new DownloadCallback<String>() {
                @Override
                public String recv(InputStream ins) throws IOException {
                    ByteArrayOutputStream result = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = ins.read(buffer)) != -1) {
                        result.write(buffer, 0, length);
                    }
                    return result.toString(StandardCharsets.UTF_8.name());
                }
            });
        } catch (FdfsUnsupportStorePathException e) {
            log.error("下载文件出错！", e);
            throw new NCException("-1", "下载文件出错！");
        }
        return base64;
    }
}
