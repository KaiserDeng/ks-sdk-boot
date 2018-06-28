package com.haihun.sdk.service;

import com.haihun.sdk.mapper.DataAnalyMapper;
import com.haihun.sdk.vo.args.CompAnalyArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author kaiser·von·d
 * @version 2018/6/7
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class DataAnalyService {

    @Autowired
    private DataAnalyMapper mapper;


    /**
     * 综合分析
     *
     * @param args 参数
     * @return 分析列表
     */
    public Map<String, Object> analyAll(CompAnalyArgs args) {
        try {
            SimpleDateFormat formart = new SimpleDateFormat("yyyy-MM-dd");
            Date beginDate = formart.parse(args.getBeginTime());
            Date endDate = formart.parse(args.getEndTime());

            long beginTime = beginDate.getTime();
            List<Map<String, Object>> rowData = new ArrayList<>();
            long day = (formart.parse(args.getEndTime()).getTime() - beginTime) / (24 * 60 * 60 * 1000);
            beginTime = endDate.getTime();
            String startTime = args.getEndTime();

            for (long i = day; i >= 0; i--) {
                args.setBeginTime(startTime);
                rowData.add(mapper.analyAll(args));
                beginTime -= (24 * 60 * 60 * 1000);
                startTime = DateFormatUtils.format(beginTime, "yyyy-MM-dd");
            }

            HashMap<String, Object> map = new HashMap<>();
            map.put("count", rowData.size());
            int end = args.getLimit() + args.getPage();

            map.put("data", rowData.subList(args.getPage(), end >= rowData.size() ? rowData.size() : end));
            map.put("code", 0);
            map.put("msg", "");
            return map;
//            return mapper.analyAll(args);
        } catch (Exception e) {
            log.error("analy all data error detail : {} ", e.getMessage());
            throw new RuntimeException("analy all data error detail :" + e.getMessage());
        }
    }

}
