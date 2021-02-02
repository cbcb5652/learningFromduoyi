package com.duoyi.business.rest;

import com.duoyi.business.model.core.Movie;
import com.duoyi.business.model.core.Rating;
import com.duoyi.business.model.core.Tag;
import com.duoyi.business.model.core.User;
import com.duoyi.business.model.recom.Recommendation;
import com.duoyi.business.request.*;
import com.duoyi.business.service.*;
import com.duoyi.business.utils.Constant;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 用户处理电影相关的功能
 */
@Controller
@RequestMapping("/rest/movies")
public class MovieRestApi {

    @Autowired
    private RecommenderService recommenderService;

    @Autowired
    private UserService userService;

    @Autowired
    private MovieService movieService;

    @Autowired
    private TagService tagService;

    @Autowired
    private RatingService ratingService;

    private Logger logger = LoggerFactory.getLogger(MovieRestApi.class);

    /**
     * 首页
     * 提供获取实时推荐信息的接口[混合推荐]     需要考虑冷启动的问题
     */
    @RequestMapping(path = "/stream", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getRealtimeRecommendations(@RequestParam("username") String username, @RequestParam("num") int num, Model model) {
        User user = userService.findUserByUsername(username);
        List<Recommendation> recommendations = recommenderService.getStreamRecsMovies(new GetStreamRecsRequest(user.getUid(), num));

        //解决冷启动问题
        if (recommendations.size() == 0) {
            Random random = new Random();
            recommendations = recommenderService.getGenresTopMovies(new GetGenresTopMoviesRequest(user.getGenres().get(random.nextInt(user.getGenres().size())), num));
        }

        List<Integer> ids = new ArrayList<>();
        for (Recommendation recommendation : recommendations) {
            ids.add(recommendation.getMid());
        }
        List<Movie> result = movieService.getMoviesByMids(ids);
        model.addAttribute("success", true);
        model.addAttribute("movies", result);
        return model;
    }

    //提供获取离线推荐信息的接口
    @RequestMapping(path = "/offline", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getOfflineRecommendations(@RequestParam("username") String username, @RequestParam("num") int num, Model model) {
        User user = userService.findUserByUsername(username);
        List<Recommendation> recommendations = recommenderService.getUserCFMovies(new GetUserCFRequest(user.getUid(), num));

        //解决冷启动问题
        if (recommendations.size() == 0) {
            Random random = new Random();
            recommendations = recommenderService.getGenresTopMovies(new GetGenresTopMoviesRequest(user.getGenres().get(random.nextInt(user.getGenres().size())), num));
        }

        List<Integer> ids = new ArrayList<>();
        for (Recommendation recommendation : recommendations) {
            ids.add(recommendation.getMid());
        }
        List<Movie> result = movieService.getMoviesByMids(ids);
        model.addAttribute("success", true);
        model.addAttribute("movies", result);
        return model;
    }

    //提供获取热门推荐信息的接口
    @RequestMapping(path = "/host", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getHotRecommendations(@RequestParam("num") int num, Model model) {
        model.addAttribute("success", true);
        model.addAttribute("movies", recommenderService.getHotRecommendations(new GetHotRecommendationRequest(num)));
        return model;
    }

    // 提供获取优质电影的信息的接口
    @RequestMapping(path = "/rate", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getRateMoreRecommendations(@RequestParam("num") int num, Model model) {
        model.addAttribute("success", true);
        model.addAttribute("movies", recommenderService.getRateMoreMovies(new GetRateMoreMoviesRequest(num)));
        return model;
    }

    // 获取最新电影的信息的接口
    @RequestMapping(path = "/new", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getNewRecommendations(@RequestParam("num") int num, Model model) {
        model.addAttribute("success", true);
        model.addAttribute("movies", recommenderService.getNewMovies(new GetNewMoviesRequest(num)));
        return model;
    }

    /**
     * 模糊检索
     */

    // 提供基于名称或者描述的模糊检索功能
    @RequestMapping(path = "/query", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getFuzzySearchMovies(@RequestParam("query") String query, @RequestParam("num") int num, Model model) {
        model.addAttribute("success", true);
        model.addAttribute("movies", recommenderService.getFuzzyMovies(new GetFuzzySearchMoviesRequest(query, num)));
        return model;
    }

    /**
     * 电影的详细页面
     */

    // 获取电影的信息
    @RequestMapping(path = "/info/{mid}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getMovieInfo(@PathVariable("mid") int mid, Model model) {
        model.addAttribute("success", true);
        model.addAttribute("movies", movieService.findMovieInfo(mid));
        return model;
    }

    //需要提供能够给电影打标签的功能
    @RequestMapping(path = "/addtag/{mid}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model addMyTags(@PathVariable("mid") int mid, @RequestParam("username") String username
            , @RequestParam("tagname") String tagname, Model model) {
        User user = userService.findUserByUsername(username);
        Tag tag = new Tag(user.getUid(),mid,tagname,System.currentTimeMillis()/1000L);
        tagService.addTagToMovie(tag);
        return null;
    }

    /**
     * 需要获取能够获取电影的所有标签信息
     */
    @RequestMapping(path = "/tags/{mid}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getMovieTags(@PathVariable("mid") int mid, Model model) {
        model.addAttribute("success", true);
        model.addAttribute("movies", tagService.getMovieTags(mid));
        return model;
    }

    // 需要能够获取电影相似的电影推荐
    @RequestMapping(path = "/same/{mid}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getSimMoviesRecommendation(@PathVariable("mid")int mid, @RequestParam("num")int num, Model model) {
        model.addAttribute("success", true);
        model.addAttribute("movies", recommenderService.getHybridRecommendations(new GetHybridRecommendationRequest(0.5,mid,num)));
        return model;
    }

    // 需要能够提供给电影打分的功能
    @RequestMapping(path = "/rate/{mid}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model rateMovie(@RequestParam("username") String username,@PathVariable("mid") int mid, @RequestParam("score") Double score, Model model) {
        User user = userService.findUserByUsername(username);
        Rating rating = new Rating(user.getUid(),mid,score,System.currentTimeMillis()/1000L);
        ratingService.rateToMovie(rating);

        // 输出埋点日志
        logger.info(Constant.USER_RATING_LOG_PREFIX+rating.getUid() + "|" + rating.getMid() + "|" + rating.getScore() + "|" + rating.getTimestamp());

        return null;
    }

    /**
     * 电影的类别页面
     */
    // 需要提供能够提供影片类别的查找
    @RequestMapping(path = "/genres", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getGenresMovies(@RequestParam("genres") String genres,@RequestParam("num")int num, Model model) {
        model.addAttribute("success", true);
        model.addAttribute("movies", recommenderService.getGenresMovies(new GetGenresMoviesRequest(genres,num)));
        return model;
    }

    /**
     * 用户空间页面
     */
    // 需要提供用户的所有评分记录
    public Model getUserRatings(String username, Model model) {
        return null;
    }

    // 需要能够获取图标数据
    public Model getUserChart(String username, Model model) {
        return null;
    }

}
