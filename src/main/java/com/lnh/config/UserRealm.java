package com.lnh.config;

import com.lnh.pojo.User;
import com.lnh.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

//自定义的UserRealm extends AuthorizingRealm
public class UserRealm  extends AuthorizingRealm {

    @Autowired
    UserService userService;
    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("执行了授权");

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        info.addStringPermission("user:add");

        //拿到当前登录的这个对象
        Subject subject = SecurityUtils.getSubject();
        User currentUser = (User)subject.getPrincipal(); //拿到User对象

        //设置当前用户的权限
        info.addStringPermission(currentUser.getPerms());

        return info;
    }

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        System.out.println("执行了认证");

        UsernamePasswordToken userToken=(UsernamePasswordToken) token;
        //连接真实的数据库
        User user=userService.queryUserByName(userToken.getUsername());

        if(user==null){ //没有这个人
            return null; //UnknownAccountException
        }

        Subject currentSubject = SecurityUtils.getSubject();
        Session session = currentSubject.getSession();
        session.setAttribute("loginUser",user);

        //可以加密： MD5：e10adc3949ba59abbe56e057f20f883e         MD5盐值加密：e10adc3949ba59abbe56e057f20f883eusername
        //密码认证，shiro做，加密了
        return new SimpleAuthenticationInfo(user,user.getPwd(),"");
    }
}
