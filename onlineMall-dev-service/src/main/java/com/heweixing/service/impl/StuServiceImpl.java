package com.heweixing.service.impl;

import com.heweixing.mapper.StuMapper;
import com.heweixing.pojo.Stu;
import com.heweixing.service.StuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StuServiceImpl implements StuService {
    @Autowired
    private StuMapper stuMapper;


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Stu getStuInfo(Integer id) {
        return stuMapper.selectByPrimaryKey(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveStu() {
        Stu stu = new Stu();
        stu.setAge(24);
        stu.setName("heweixing");
        stuMapper.insert(stu);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateStu(Integer id) {
        Stu stu = new Stu();
        stu.setId(id);
        stu.setAge(24);
        stu.setName("xingweihe");
        stuMapper.updateByPrimaryKey(stu);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteStu(Integer id) {
        stuMapper.deleteByPrimaryKey(id);
    }
}
