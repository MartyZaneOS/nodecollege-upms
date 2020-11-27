package com.nodecollege.cloud.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.nodecollege.cloud.common.annotation.ApiAnnotation;
import com.nodecollege.cloud.common.model.NCResult;
import com.nodecollege.cloud.common.model.QueryVO;
import com.nodecollege.cloud.common.model.po.OperateFile;
import com.nodecollege.cloud.common.utils.Base64Util;
import com.nodecollege.cloud.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * @author LC
 * @date 2020/5/28 22:05
 */
@RestController
public class UploadFileController {

    @Autowired
    private FileService fileService;

    @ApiAnnotation(modularName = "文件管理", description = "上传图片", accessSource = 2)
    @PostMapping("/getFileList")
    public NCResult<OperateFile> getFileList(@RequestBody QueryVO<OperateFile> queryVO) {
        Page page = PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
        List<OperateFile> list = fileService.getFileList(queryVO);
        return NCResult.ok(list, page.getTotal());
    }

    @ApiAnnotation(modularName = "文件管理", description = "上传图片", accessSource = 2)
    @PostMapping("/delFile")
    public NCResult<OperateFile> delFile(@RequestBody OperateFile operateFile) {
        fileService.delFile(operateFile);
        return NCResult.ok();
    }

    @ApiAnnotation(modularName = "文件管理", description = "上传图片", accessSource = 2)
    @PostMapping("/uploadImg")
    public NCResult<OperateFile> uploadImg(@RequestBody OperateFile operateFile) {
        MultipartFile avatarFile = Base64Util.base64ToMultipart(operateFile.getFileBase64());
        return NCResult.ok(fileService.uploadImg(avatarFile, operateFile));
    }

    @ApiAnnotation(modularName = "用户中心", description = "上传图片，自动生成缩略图", accessSource = 2)
    @PostMapping("/uploadImgThumb")
    public NCResult<OperateFile> uploadImgThumb(@RequestBody OperateFile operateFile) {
        MultipartFile avatarFile = Base64Util.base64ToMultipart(operateFile.getFileBase64());
        return NCResult.ok(fileService.uploadImgThumb(avatarFile, operateFile));
    }

    @ApiAnnotation(modularName = "文件管理", description = "上传文件", accessSource = 2)
    @PostMapping("/uploadFile")
    public NCResult<OperateFile> uploadFile(@RequestBody OperateFile operateFile) {
        return NCResult.ok(fileService.uploadFile(operateFile.getFileBase64(), operateFile));
    }

    @ApiAnnotation(modularName = "文件管理", description = "下载文件")
    @PostMapping("/downloadFile")
    public NCResult<String> downloadFile(@RequestBody OperateFile operateFile) {
        String html = fileService.downloadFile(operateFile.getFilePath());
        return NCResult.ok(Arrays.asList(html));
    }
}
