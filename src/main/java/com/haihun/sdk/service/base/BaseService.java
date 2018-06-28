package com.haihun.sdk.service.base;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.haihun.comm.base.BaseMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;

/**
 * service抽象类
 *
 * @author kaiser·von·d
 * @version 2018/4/19
 */
public abstract class BaseService<T> {

    @Autowired
    private BaseMapper<T> mapper;

    /**
     * 查询所有
     */
    public List<T> findAll() {
        return mapper.selectAll();
    }

    /**
     * 根据id查询
     */
    public T findById(Serializable id) {
        return (T) mapper.selectByPrimaryKey(id);
    }

    /**
     * 根据条件查询
     *
     * @param t 查询条件
     * @return 数据列表
     */
    public List<T> findListByWhere(T t) {
        return mapper.select(t);
    }

    /**
     * 根据条件分页查询
     *
     * @param page 当前页号
     * @param rows 页大小
     * @param t    查询条件
     * @return 分页信息对象
     */
    public PageInfo<T> findByPage(Integer page, Integer rows, T t) {
        /**设置分页*/
        PageHelper.startPage(page, rows);
        List<T> list = mapper.select(t);
        return new PageInfo<T>(list);
    }

    /**
     * 全新增
     */
    public Integer save(T t) {
        return mapper.insert(t);
    }

    /**
     * 选择性新增
     */
    public Integer saveSelective(T t) {
        return mapper.insertSelective(t);
    }

    /**
     * 全更新
     */
    public Integer update(T t) {
        return mapper.updateByPrimaryKey(t);
    }

    /**
     * 选择性更新
     */
    public Integer updateSelective(T t) {
        return mapper.updateByPrimaryKeySelective(t);
    }

    /**
     * 根据id删除
     */
    public Integer deleteById(Serializable id) {
        return mapper.deleteByPrimaryKey(id);
    }



}
