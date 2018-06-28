package com.haihun.sdk.mapper;

import com.haihun.sdk.vo.args.CompAnalyArgs;

import java.util.Map;

/**
 * @author kaiser·von·d
 * @version 2018/6/7
 */
public interface DataAnalyMapper {


   Map<String, Object> analyAll(CompAnalyArgs args);

}
