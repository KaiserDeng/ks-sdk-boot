package com.haihun.sdk.controller;

import com.haihun.comm.util.ConstFuncUtil;
import com.haihun.sdk.pojo.Game;
import com.haihun.sdk.service.DataAnalyService;
import com.haihun.sdk.service.GameService;
import com.haihun.sdk.vo.args.CompAnalyArgs;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kaiser·von·d
 * @version 2018/6/5
 */
@Api(tags = "analy")
@Controller
@RequestMapping("/analy")
public class DataAnalyController {

    @Autowired
    private GameService gameService;

    @Autowired
    private DataAnalyService dataAnalyService;

    @ApiOperation(value = "跳转到主页")
    @GetMapping("/index")
    public String index(HttpServletRequest request) {
        List<Game> games = gameService.findAll();
        ArrayList<Map<String, String>> list = new ArrayList<>();
        games.forEach(x->{
            list.add(new HashMap<String, String>() {{
                put("name", x.getGameName());
                put("url", x.getAppId());
            }});
        });
        request.setAttribute("gameList", list);
        return "index";
    }

    @ResponseBody
    @PostMapping("/getChannelIdByAppId")
    public Object getChannel(@RequestParam("appId") String appId) {
        try {
            return gameService.findChInfoByAppId(appId);
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    @ApiOperation(value = "跳转到登录页面")
    @GetMapping("/login")
    public String login() {
        return "login";
    }


    @ResponseBody
    @PostMapping("/all")
    @ApiOperation(value = "综合分析")
    public Object all(CompAnalyArgs compAnalyArgs) {
        try {
            compAnalyArgs.setPage((compAnalyArgs.getPage() - 1) * compAnalyArgs.getLimit());
            compAnalyArgs.setLimit(compAnalyArgs.getLimit());
            return dataAnalyService.analyAll(compAnalyArgs);
        } catch (Exception e) {
            return ConstFuncUtil.buildErrorResult(e);
        }
    }

    @ApiOperation(value = "跳转到主页")
    @GetMapping("/orderIndex")
    public String orderIndex(Model model) {
//        PayTypeEnum.values()
        model.addAttribute("paymentType","1");
        return "order-index";
    }

}
