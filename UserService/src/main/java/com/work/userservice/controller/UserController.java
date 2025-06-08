package com.work.userservice.controller;

import com.work.commonconfig.exception.MyException;
import com.work.userservice.dto.UserGetDTO;
import com.work.userservice.dto.UserLoginDTO;
import com.work.userservice.entity.Users;
import com.work.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    // 用户信息展示
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String token) {
        try {
            UserGetDTO userInfo = userService.getUserById(token);
            return ResponseEntity.ok(userInfo);
        } catch (MyException e) {
            // 捕获 MyException，自定义返回状态码和消息
            return ResponseEntity.status(e.getCode()).body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器内部错误");
        }
    }

    // 用户登录
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        try {
            String id = loginData.get("id");
            String password = loginData.get("password");
            UserLoginDTO loginResult = userService.login(id, password);
            return ResponseEntity.ok(loginResult);
        } catch (MyException e) {
            return ResponseEntity.status(e.getCode()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器内部错误");
        }
    }

    // 用户注册
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Users users) {
        try {
            String userId = userService.register(users);
            Map<String, String> res = new HashMap<>();
            res.put("message", "注册成功");
            res.put("userId", userId);
            return ResponseEntity.ok(res);
        } catch (MyException e) {
            return ResponseEntity.status(e.getCode()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器内部错误");
        }
    }

    // 用户修改密码
    @PostMapping("/updatePassword")
    public ResponseEntity<?> updatePassword(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> passwordData) {
        try {
            String oldPassword = passwordData.get("oldPassword");
            String newPassword = passwordData.get("newPassword");
            userService.updatePassword(token, oldPassword, newPassword);
            return ResponseEntity.ok("密码修改成功");
        } catch (MyException e) {
            return ResponseEntity.status(e.getCode()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器内部错误");
        }
    }

    /**
     * 根据角色统计人数
     * 例如请求：GET /users/count?role=1
     */
    @GetMapping("/count")
    public ResponseEntity<?> countPerson(@RequestParam Integer role) {
        try {
            Integer count = userService.countPerson(role);
            return ResponseEntity.ok(count);
        } catch (MyException e) {
            return ResponseEntity.status(e.getCode()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器内部错误");
        }
    }
}
