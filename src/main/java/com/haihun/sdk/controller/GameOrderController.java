package com.haihun.sdk.controller;

import com.haihun.annotation.ResponseEncrypt;
import com.haihun.comm.util.ConstFuncUtil;
import com.haihun.comm.util.ValidateUtil;
import com.haihun.sdk.pojo.GameOrder;
import com.haihun.sdk.service.GameOrderService;
import com.haihun.sdk.vo.result.OrderListResult;
import com.haihun.sdk.vo.result.OrderResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 订单相关控制器
 *
 * @author kaiser·von·d
 * @version 2018/4/20
 */
@Api(tags = "order")
@Controller
@RequestMapping("/order")
public class GameOrderController {


    @Autowired
    private GameOrderService orderService;


    /**
     * 根据订单id查询订单
     *
     * @param
     * @return
     */
    @PostMapping("/queryOrderByOrderId")
    @ApiOperation(value = "根据订单号查询订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单号"),
    })
    @ResponseBody
    public OrderResult queryOrderByOrderId(@RequestParam("orderId") String orderId) {
        try {
            return orderService.queryOrderByOrderId(orderId);
        } catch (Exception e) {
            return ConstFuncUtil.buildErrorResult(e, OrderResult.class);
        }
    }


    @PostMapping("/list")
    @ApiOperation(value = "查询所有订单", notes = "不加密")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "授权token", paramType = "form"),
            @ApiImplicitParam(name = "page", value = "当前页", paramType = "form"),
            @ApiImplicitParam(name = "limit", value = "页大小", paramType = "form"),
    })
    @ResponseBody
    public OrderListResult list(@RequestParam("token") String token,
                                @RequestParam(value = "page", defaultValue = "1") Integer page,
                                @RequestParam(value = "limit", defaultValue = "20") Integer limit) {
        try {
            return orderService.list(page, limit, token);
        } catch (Exception e) {
            return ConstFuncUtil.buildErrorResult(e, OrderListResult.class);
        }

    }


    @PostMapping(value = "/create")
    @ApiOperation(value = "创建订单", notes = "请求加密/响应加密")
    @ResponseEncrypt
    @ResponseBody
    public OrderResult createOrder(@RequestBody GameOrder order, HttpServletRequest request, HttpServletResponse response) {
        try {
            order.setSpbillCreateIp(getIP(request));
            OrderResult result = ValidateUtil.validate(order, OrderResult.class);
            if (ConstFuncUtil.checkResult(result)) return result;
            //保存订单
            orderService.createOrder(order, result);
            return result;
        } catch (Exception e) {
            return ConstFuncUtil.buildErrorResult(e, OrderResult.class);
        }
    }


    @ApiOperation(value = "创建支付宝网页订单", notes = "不加密")
    @PostMapping("/createWapOrder")
    public String createAalipayWapOrder(GameOrder order, HttpServletRequest request, Model model, HttpServletResponse response) {
        try {
            order.setPayChannel("2");
            OrderResult result = ValidateUtil.validate(order, OrderResult.class);
            if (ConstFuncUtil.checkResult(result)) return null;
            order.setSpbillCreateIp(getIP(request));
            String id = order.getUserName() == null ? "" : order.getUserName().replace("hh", "");
            order.setUserName(id);
            orderService.createOrder(order, result);
            if ("0".equalsIgnoreCase(order.getPaymentType())) {
                model.addAttribute("form", result.getData());
                return "alipay";
            } else if ("1".equalsIgnoreCase(order.getPaymentType())) {
//                response.sendRedirect("https://wxpay.wxutil.com/mch/pay/h5.v2.php");
                response.sendRedirect(result.getData());
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取真实IP
     *
     * @return ip地址
     */
    public String getIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && "".trim().equals(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && "".trim().equals(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }
}
