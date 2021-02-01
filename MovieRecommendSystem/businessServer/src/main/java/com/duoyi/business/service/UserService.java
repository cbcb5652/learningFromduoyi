package com.duoyi.business.service;

import com.duoyi.business.model.domain.User;
import com.duoyi.business.request.LoginUserRequest;
import com.duoyi.business.request.RegisterUserReqeuest;
import com.duoyi.business.request.UpdateUserGenresRequest;
import com.duoyi.business.utils.Constant;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 对于用户具体处理业务服务的服务类
 */
@Service
public class UserService {

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private ObjectMapper objectMapper;

    private MongoCollection<Document> userCollection;

    // 获取用户user表连接
    private MongoCollection<Document> getUserCollection(){
        if (null == userCollection){
            this.userCollection = mongoClient.getDatabase(Constant.MONGO_DATABASE).getCollection(Constant.MONGO_USER_COLLECTION);
        }
        return this.userCollection;
    }

    // 将User转换成一个Document
    private Document userToDocument(User user){
        try {
            Document document = Document.parse(objectMapper.writeValueAsString(user));
           return document;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 将Document装换为User
    private User documentToUser(Document document){
        try {
            User user = objectMapper.readValue(JSON.serialize(document),User.class);
            return user;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 用于提供注册用户的服务
     * @param reqeuest
     * @return
     */
    public boolean registerUser(RegisterUserReqeuest reqeuest){

        // 判断是否有相同的用户名已经注册
        if (getUserCollection().find(new Document("username",reqeuest.getUsername())).first() != null){
            return false;
        }


        // 创建一个用户
        User user = new User();
        user.setUsername(reqeuest.getUsername());
        user.setPassword(reqeuest.getPassword());
        user.setFirst(true);

        // 插入一个用户
        Document document = userToDocument(user);
        if (null == document){
            return false;
        }
        getUserCollection().insertOne(document);
        return true;
    }


    /**
     * 用于提供用户的登录
     * @param request
     * @return
     */
    public boolean loginUser(LoginUserRequest request){

        // 需要找到这个用户
        Document document = (Document) getUserCollection().find(new Document("username", request.getUsername()));
        if (null == document){
            return false;
        }
        User user = documentToUser(document);
        // 验证密码
        if (null == user){
            return false;
        }
        return user.getPassword().equals(request.getPassword());
    }


    /**
     * 更新用户第一次登录 选择的电影类别
     */
    public void updateUserGenres(UpdateUserGenresRequest request){
        getUserCollection().updateOne(new Document("username",request.getUsername()),new Document().append("$set",new Document("$genres",request.getGenres())));
        getUserCollection().updateOne(new Document("username",request.getUsername()),new Document().append("$set",new Document("first",false)));
    }



}
