package com.heweixing.service;

import com.heweixing.pojo.Stu;

public interface StuService {

    public Stu getStuInfo(Integer id);

    public void saveStu();

    public void updateStu(Integer id);

    public void deleteStu(Integer id);

}
