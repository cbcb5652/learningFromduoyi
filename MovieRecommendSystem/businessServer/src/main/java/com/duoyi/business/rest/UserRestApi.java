package com.duoyi.business.rest;


import com.duoyi.business.request.RegisterUserReqeuest;
import com.duoyi.business.request.UpdateUserGenresRequest;
import com.duoyi.business.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 用于处理User相关的操作
 */
@Controller
@RequestMapping("/rest/users")
public class UserRestApi {

    @Autowired
    private UserService userService;

    /**
     * 需要提供用户注册功能
     * url:        /rest/users/register?username=abc&password-abc
     *
     * @param username
     * @param password
     * @param model
     * @return TODO: 密码加密，邮箱格式验证
     */
    @RequestMapping(path = "/register", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model registerUser(@RequestParam("username") String username, @RequestParam("password") String password, Model model) {
        model.addAttribute("success", userService.registerUser(new RegisterUserReqeuest(username, password)));
        return model;
    }

    /**
     * 需要提供用户登录功能
     *
     * @param username
     * @param password
     * @param model
     * @return TODO: 这个登录验证有点问题
     */
    @RequestMapping(path = "/login", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model loginUser(@RequestParam("username") String username, @RequestParam("password") String password, Model model) {
        model.addAttribute("success", userService.registerUser(new RegisterUserReqeuest(username, password)));
        return model;
    }

    /**
     * 添加用户偏爱的影片类别
     *
     * @param username
     * @param genres
     * @return
     */
    @RequestMapping(path = "/genres", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public void addGenres(@RequestParam("username") String username, @RequestParam("genres") String genres) {

        List<String> genresList = Arrays.asList( genres.split("\\|"));
        userService.updateUserGenres(new UpdateUserGenresRequest(username, genresList));
    }

}
