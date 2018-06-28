package com.haihun.sdk.controller;

import com.google.common.collect.Lists;
import com.haihun.sdk.pojo.Job;
import com.haihun.sdk.pojo.JobDetail;
import com.haihun.sdk.service.QuartzJobDetailService;
import com.haihun.sdk.vo.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.quartz.JobKey;
import org.quartz.core.jmx.JobDataMapSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * quartz api
 *
 * @author: lvhao
 * @since: 2016-6-23 20:18
 */

@Api(tags = "job")
@Controller
@RequestMapping("/jobManage")
public class QuartzJobDetailController {

    @Autowired
    private QuartzJobDetailService quartzJobDetailService;

    @ApiOperation(value = "获取任务列表")
    @GetMapping("/list")
    @ResponseBody
    public Object list(@RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "limit", defaultValue = "20") Integer limit) {
        List<JobDetail> jobDetailDOs = quartzJobDetailService.queryJobList();
        ArrayList<Job> rowData = Lists.newArrayList();
        jobDetailDOs.forEach(x -> {
            rowData.add(x.getJobDO());
        });

        HashMap<String, Object> map = new HashMap<>();
        map.put("count", rowData.size());
        page -= 1;
        int end = limit + page;

        map.put("data", rowData.subList(page, end >= rowData.size() ? rowData.size() : end));
        map.put("code", 0);
        map.put("msg", "");
        return map;
    }

    @ApiOperation("查询指定jobKey jobDetail")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "group", value = "组名", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "name", value = "名称", required = true, dataType = "String", paramType = "path")
    })
    @GetMapping("/{group}/{name}")
    @ResponseBody
    public JobDetail queryByJobKey(@PathVariable String name, @PathVariable String group) {
        JobKey jobKey = new JobKey(name, group);
        JobDetail jobDetailDO = quartzJobDetailService.queryByKey(jobKey);
        return jobDetailDO;
    }

    @ApiOperation("添加任务Job")
    @PostMapping("/add")
    @ResponseBody
    public Result add(@RequestBody JobDetail jobDetailDO) {
        boolean result = quartzJobDetailService.add(jobDetailDO);
        return new Result();
    }

    @ApiOperation("批量删除Job")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobKeyGroups", value = "批量删除的任务")
    })
    @DeleteMapping
    public ResponseEntity<Boolean> delete(@RequestBody Map<String, List<String>> jobKeyGroups) {
        List<JobKey> jobKeys = Lists.newArrayList();
        jobKeyGroups.forEach((k, v) ->
                v.forEach(name -> {
                    JobKey jobKey = new JobKey(name, k);
                    jobKeys.add(jobKey);
                })
        );
        boolean result = quartzJobDetailService.remove(jobKeys);
        return ResponseEntity.ok().body(result);
    }

    @ApiOperation("立即触发任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "group", value = "组名", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "name", value = "任务名", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "jobData", value = "额外数据", required = true, dataType = "Map<String,Object>", paramType = "body")
    })
    @PostMapping("/{group}/{name}")
    public ResponseEntity<Boolean> triggerNow(@PathVariable String group,
                                              @PathVariable String name,
                                              @RequestBody Map<String, Object> jobData) {
        JobKey jobKey = new JobKey(name, group);
        boolean result = quartzJobDetailService.triggerNow(
                jobKey,
                JobDataMapSupport.newJobDataMap(jobData)
        );
        return ResponseEntity.ok().body(result);
    }


    @GetMapping("/quartzIndex")
    public String index() {
        return "quartzIndex";
    }
}
