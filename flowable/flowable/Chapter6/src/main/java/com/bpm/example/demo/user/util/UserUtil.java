package com.bpm.example.demo.user.util;

import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.IdentityService;
import org.flowable.idm.api.Picture;
import org.flowable.idm.api.User;
import org.flowable.idm.api.UserQuery;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
public class UserUtil {

    private IdentityService identityService;

    public UserUtil(IdentityService identityService) {
        this.identityService = identityService;
    }

    /**
     * 新建用户
     * @param id                用户编号
     * @param lastName          用户姓氏
     * @param firstName         用户名字
     * @param displayName       用户显示名
     * @param email             用户电子邮箱
     * @param password          用户密码
     */
    public void addUser(String id, String lastName, String firstName, String displayName, String email, String password) {
        //调用IdentityService的newUser方法创建User实例
        User newUser = identityService.newUser(id);
        //调用setter方法为创建的User实例设置属性值
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setDisplayName(displayName);
        newUser.setEmail(email);
        newUser.setPassword(password);
        //调用IdentityService的saveUser方法将User实例保存到数据库中
        identityService.saveUser(newUser);
    }

    /**
     * 执行UserQuery的list方法
     * @param userQuery
     */
    public void executeList(UserQuery userQuery) {
        List<User> users = userQuery.list();
        for (User user : users) {
            log.info("用户编号：{}，姓名：{}，显示名：{}，邮箱：{}", user.getId(), user.getLastName() + user.getFirstName(), user.getDisplayName(), user.getEmail());
        }
    }

    /**
     * 执行UserQuery的listPage方法
     * @param userQuery
     * @param firstResult
     * @param maxResults
     */
    public void executeListPage(UserQuery userQuery, int firstResult, int maxResults) {
        List<User> users = userQuery.listPage(firstResult, maxResults);
        for (User user : users) {
            log.info("用户编号：{}，姓名：{}，显示名：{}，邮箱：{}", user.getId(), user.getLastName() + user.getFirstName(), user.getDisplayName(), user.getEmail());
        }
    }

    /**
     * 执行UserQuery的count方法
     * @param userQuery
     * @return
     */
    public void executeCount(UserQuery userQuery) {
        long userNum = userQuery.count();
        log.info("用户数为：{}", userNum);
    }

    /**
     * 执行UserQuery的singleResult方法
     * @param userQuery
     */
    public User executeSingleResult(UserQuery userQuery) {
        User user = userQuery.singleResult();
        return user;
    }

    /**
     * 修改用户信息
     * @param id               用户编号
     * @param newLastName      新用户姓氏
     * @param newFirstName     新用户名字
     * @param newFirstName     新用户显示名
     * @param newEmail         新用户电子邮箱
     * @param newPassword      新用户密码
     */
    public void updateUser(String id, String newLastName, String newFirstName, String newDisplayName, String newEmail, String newPassword) {
        //查询用户信息
        User user = executeSingleResult(identityService.createUserQuery().userId(id));
        //调用setter方法为创建的User实例设置属性值
        user.setFirstName(newFirstName);
        user.setLastName(newLastName);
        user.setDisplayName(newDisplayName);
        user.setEmail(newEmail);
        user.setPassword(newPassword);
        //调用IdentityService的saveUser方法将User实例保存到数据库中
        identityService.saveUser(user);
    }

    /**
     * 删除用户
     * @param id                用户编号
     */
    public void deleteUser(String id) {
        identityService.deleteUser(id);
    }

    /**
     *  为用户设置图片
     * @param userId             用户编号
     * @param userPictureFile   用户图片File
     */
    public void setPictureForUser(String userId, File userPictureFile) {
        try {
            FileInputStream fileInputStream = new FileInputStream(userPictureFile);
            BufferedImage bufferedImage = ImageIO.read(fileInputStream);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", outputStream);
            //将图片转换为byte数组
            byte[] pictureArray = outputStream.toByteArray();
            //创建用户图片的Picture实例
            Picture userPicture = new Picture(pictureArray, "the picture of user:" + userId);
            //为用户设置图片
            identityService.setUserPicture(userId, userPicture);
        } catch(IOException e) {
            log.error(e.getMessage());
        }
    }
}