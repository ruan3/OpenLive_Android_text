package io.agora.model;

import java.io.Serializable;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

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

    public String getAuthIconUrl() {
        return authIconUrl;
    }

    public void setAuthIconUrl(String authIconUrl) {
        this.authIconUrl = authIconUrl;
    }

    private String authIconUrl;

    public BmobFile getHead_icon() {
        return head_icon;
    }

    public void setHead_icon(BmobFile head_icon) {
        this.head_icon = head_icon;
    }

    private BmobFile head_icon;

    public String getUser_id_name() {
        return user_id_name;
    }

    public void setUser_id_name(String user_id_name) {
        this.user_id_name = user_id_name;
    }

    private String user_id_name;

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
