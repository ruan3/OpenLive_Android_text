package io.agora.model;

import java.io.Serializable;
import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * File Name:
 * Author:      ruan
 * Write Dates: 2017/7/4
 * Description:
 */

public class MyUser extends BmobUser {

    private static final long serialVersionUID = 1L;
    private Integer age;
    private Integer num;
    private Boolean sex;

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    public List<String> getHobby() {
        return hobby;
    }

    public void setHobby(List<String> hobby) {
        this.hobby = hobby;
    }

    private List<String> hobby;//对应服务端Array类型：String类型的集合

    @Override
    public String toString() {
        return "MyUser{" +
                "age=" + age +
                ", num=" + num +
                ", sex=" + sex +
                "获取服务器缓存："+getSessionToken()+
                "获取邮箱认证："+getEmailVerified()+
                "获取用户id:"+getObjectId()+
                '}';
    }
}
