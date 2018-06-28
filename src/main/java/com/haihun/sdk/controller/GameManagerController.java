package com.haihun.sdk.controller;

import com.alibaba.fastjson.JSONObject;
import com.haihun.comm.util.ConstFuncUtil;
import com.haihun.sdk.pojo.Game;
import com.haihun.sdk.service.GameService;
import com.haihun.sdk.vo.result.Result;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kaiser·von·d
 * @version 2018/4/24
 */
@Api(tags = "gameManager")
@Slf4j
@RestController
@RequestMapping("/gameManager")
public class GameManagerController {


    // 允许上传的格式
    private static final String[] IMAGE_TYPE = new String[]{".bmp", ".jpg", ".jpeg", ".gif", ".png"};

    @Resource(name = "pathMap")
    private Map<String, String> pathMap;

    @Autowired
    private GameService service;

    @PostMapping(value = "/add")
    @ApiOperation(value = "新增游戏", notes = "暂不加密")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gameName", value = "游戏名称", paramType = "form"),
            @ApiImplicitParam(name = "payCallBackUrl", value = "回调地址", paramType = "form"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "新增游戏成功！"),
            @ApiResponse(code = 500, message = "系统错误！"),
    })
    public Result addGame(@RequestParam("gameName") String gameName,
                          @RequestParam(value = "payCallBackUrl", required = false) String payCallBackUrl) {
        try {
            return service.add(gameName, payCallBackUrl);
        } catch (Exception e) {
            return ConstFuncUtil.buildErrorResult(e);
        }
    }


    @PostMapping("/updateGame")
    @ApiOperation(value = "修改游戏")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId", value = "应用ID", paramType = "form"),
            @ApiImplicitParam(name = "gameName", value = "游戏名称", paramType = "form"),
            @ApiImplicitParam(name = "payCallBackUrl", value = "回调地址", paramType = "form"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "更新成功！"),
            @ApiResponse(code = 400, message = "找不到该游戏！"),
            @ApiResponse(code = 500, message = "系统错误！"),
    })
    public Result updateGame(@RequestParam("appId") String appId, @RequestParam("gameName") String gameName, @RequestParam("payCallBackUrl") String payCallBackUrl) {
        try {
            return service.updatGame(appId, gameName, payCallBackUrl);
        } catch (Exception e) {
            return ConstFuncUtil.buildErrorResult(e);
        }

    }


    @ApiOperation("上传游戏图标")
    @PostMapping(value = "/uploadIcon")
    @ApiImplicitParam(name = "appId", value = "应用ID", paramType = "form")
    @ApiResponse(code = 400, message = "没有找到该游戏！")
    public Result uploadIcon(@RequestParam("icon") MultipartFile uploadFile, @RequestParam("appId") String appId) throws Exception {
        Result result = new Result();
        try { // 校验图片格式
            boolean isLegal = false;
            for (String type : IMAGE_TYPE) {
                if (StringUtils.endsWithIgnoreCase(uploadFile.getOriginalFilename(), type)) {
                    isLegal = true;
                    break;
                }
            }
            // 状态
            if (!isLegal) {
                result.warn("This file format is not correct！");
                return result;
            }

            // 封装Result对象，并且将文件的byte数组放置到result对象中
            JSONObject fileUploadResult = new JSONObject();


            // 文件新路径
            String filePath = getFilePath(uploadFile.getOriginalFilename());

            if (log.isDebugEnabled()) {
                log.debug("Pic file upload .[{}] to [{}] .", uploadFile.getOriginalFilename(), filePath);
            }

            // 生成图片的引用地址
            String picUrl = StringUtils.replace(StringUtils.substringAfter(filePath, pathMap.get("repositoryPath")), "\\", "/");
            fileUploadResult.put("url", pathMap.get("imageBaseUrl") + picUrl);

            File newFile = new File(filePath);

            // 将上传的文件写到新路径
            uploadFile.transferTo(newFile);

            // 校验图片是否合法
            isLegal = false;

            BufferedImage image = ImageIO.read(newFile);
            if (image != null) {
                fileUploadResult.put("width", image.getWidth() + "");
                fileUploadResult.put("height", image.getHeight() + "");
                isLegal = true;
            }


            if (!isLegal) {
                // 不合法，将磁盘上的文件删除
                newFile.delete();
                result.warn("Illegal pictures！");
                return result;
            }

            // 保存修改
            Integer upResult = service.updateSelective(new Game() {{
                setAppId(appId);
                setGameIcon(picUrl);
            }});
            if (!upResult.equals(1)) {
                newFile.delete();
                result.notFound("not found game!");
            } else {
                result.setData(fileUploadResult.toJSONString());
            }

            return result;
        } catch (Exception e) {
            return ConstFuncUtil.buildErrorResult(e);
        }
    }


    @PostMapping("/list")
    @ApiOperation(value = "获取游戏列表")
    public Object list(@RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "rows", defaultValue = "30") Integer rows) {
        try {
            return service.findGameByPage(page, rows);
        } catch (Exception e) {
            return ConstFuncUtil.buildErrorResult(e);
        }
    }

    @PostMapping("/queryGameByName")
    @ApiOperation(value = "根据游戏名称查询游戏")
    public Object queryGameByName(@RequestParam("gameName") String gameName) {
        try {
            return service.queryGameByName(gameName);

        } catch (Exception e) {
            return ConstFuncUtil.buildErrorResult(e);
        }

    }


    /**
     * 生成新的文件名（yyyy/MM/dd/DateTime&randomNum.xxx）
     *
     * @param sourceFileName
     * @return
     */
    private String getFilePath(String sourceFileName) {
        String baseFolder = pathMap.get("repositoryPath") + File.separator + "images";
        Date nowDate = new Date();
        // yyyy/MM/dd
        String fileFolder = baseFolder + File.separator + new DateTime(nowDate).toString("yyyy") + File.separator + new DateTime(nowDate).toString("MM") + File.separator
                + new DateTime(nowDate).toString("dd");
        File file = new File(fileFolder);
        if (!file.exists()) {
            // 如果目录不存在，则创建目录
            file.mkdirs();
        }
        fileFolder = file.getAbsolutePath();
        // 生成新的文件名
        String fileName = new DateTime(nowDate).toString("yyyyMMddhhmmssSSSS") + RandomUtils.nextInt(100, 9999) + "." + StringUtils.substringAfterLast(sourceFileName, ".");

        return fileFolder + File.separator + fileName;
    }

    @ConfigurationProperties(prefix = "iconpath")
    @Bean(name = "pathMap")
    public Map<String, String> getPathMap() {
        HashMap<String, String> pathMap = new HashMap<>();
        return pathMap;
    }


}
