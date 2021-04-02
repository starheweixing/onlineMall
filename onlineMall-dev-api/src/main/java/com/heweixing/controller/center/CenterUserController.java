package com.heweixing.controller.center;

import com.heweixing.controller.BaseController;
import com.heweixing.pojo.Users;
import com.heweixing.pojo.bo.center.CenterUserBO;
import com.heweixing.resource.FileUpload;
import com.heweixing.service.center.CenterUserService;
import com.heweixing.utils.CookieUtils;
import com.heweixing.utils.DateUtil;
import com.heweixing.utils.IMOOCJSONResult;
import com.heweixing.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Api(value = "用户信息接口", tags = "用户信息相关的api接口")
@RestController
@RequestMapping("/userInfo")
public class CenterUserController extends BaseController {

    @Autowired
    private FileUpload fileUpload;

    @Autowired
    private CenterUserService centerUserService;

    @ApiOperation(value = "获取用户信息", notes = "获取用户信息", httpMethod = "POST")
    @PostMapping("/update")
    public IMOOCJSONResult update(
            @ApiParam(name = "userId", value = "用户id", required = true)
                    String userId,
            @RequestBody @Valid CenterUserBO centerUserBO,
            BindingResult result,
            HttpServletRequest request,
            HttpServletResponse response) {
        //判断BindingResult是否保存错误的验证信息,如果有，则直接return
        if (result.hasErrors()) {
            Map<String, String> errorMap = getErrors(result);
            return IMOOCJSONResult.errorMap(errorMap);
        }

        Users userResult = centerUserService.updateUserInfo(userId, centerUserBO);
        userResult = setNullProperty(userResult);
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(userResult), true);   //更新cookie
        //TODO 后续要改，增加令牌token，会整合redis，分布式会话
        return IMOOCJSONResult.ok();
    }


    private Map<String, String> getErrors(BindingResult result) {
        Map<String, String> map = new HashMap<>();
        List<FieldError> errorList = result.getFieldErrors();
        for (FieldError error : errorList) {
            String field = error.getField();
            String defaultMessage = error.getDefaultMessage();
            map.put(field, defaultMessage);
        }
        return map;
    }

    public Users setNullProperty(Users userResult) {
        userResult.setPassword(null);
        userResult.setCreatedTime(null);
        userResult.setMobile(null);
        userResult.setEmail(null);
        userResult.setBirthday(null);
        userResult.setUpdatedTime(null);
        return userResult;
    }


    @ApiOperation(value = "用户头像修改", notes = "用户头像修改", httpMethod = "POST")
    @PostMapping("/uploadFace")
    public IMOOCJSONResult uploadFace(
            @ApiParam(name = "userId", value = "用户id", required = true)
                    String userId,
            @ApiParam(name = "file", value = "用户头像", required = true)
                    MultipartFile file,
            HttpServletRequest request,
            HttpServletResponse response) {


        //定义一个头像保存的地址
//        String fileSpace = IMAGE_USER_FACE_LOCATION;
        String fileSpace = fileUpload.getImageUserFaceLocation();
        // 在路径上为每一个用户增加一个userId,用于区分不同用户上传
        String uploadPathPrefix = File.separator + userId;

        //开始上传文件
        if (file != null) {
            FileOutputStream fileOutputStream = null;
            try {
                String fileName = file.getOriginalFilename();
                if (StringUtils.isNoneBlank(fileName)) {
                    // 文件重命名 imooc-face.png
                    String[] fileNameArr = fileName.split("\\.");
                    // 获取文件的后缀名
                    String suffix = fileNameArr[fileNameArr.length - 1];

                    if(!suffix.equalsIgnoreCase("png") &&
                            !suffix.equalsIgnoreCase("jpg") &&
                            !suffix.equalsIgnoreCase("jpeg")){
                        return IMOOCJSONResult.errorMsg("图片格式不正确");
                    }



                    //文件名重组,覆盖式上传 ,增量式可以是拼接当前时间     //face-{userId}.png
                    String newFileName = "face" + userId + "." + suffix;
                    //上传头像最终保存的地址
                    String finalFacePath = fileSpace + uploadPathPrefix + File.separator + newFileName;

                    //用于提供给web服务访问的地址
                    uploadPathPrefix += ("/" + newFileName);

                    File outFile = new File(finalFacePath);
                    if (outFile.getParentFile() != null) {
                        //创建文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    //文件输出保存的目录
                    fileOutputStream = new FileOutputStream(outFile);
                    InputStream inputStream = file.getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            return IMOOCJSONResult.errorMsg("文件不能为空");
        }
        //获取图片服务器地址
        String imageServerUrl = fileUpload.getImageServerUrl();

        //由于浏览器可能存在缓存的情况，所以在这里，我们需要加上时间戳来保证更新后的图片及时刷新
        String finalUserFaceUrl = imageServerUrl + uploadPathPrefix + "?t=" + DateUtil.getCurrentDateString(DateUtil.DATE_PATTERN);

        //更新用户头像到数据库
        Users userResult = centerUserService.updateUserFace(userId, finalUserFaceUrl);

        userResult = setNullProperty(userResult);
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(userResult), true);   //更新cookie

        //TODO 后续要改，增加令牌token，会整合redis，分布式会话

        return IMOOCJSONResult.ok();
    }


}
