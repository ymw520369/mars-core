/**
 * Copyright Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 * <p>
 * 2017年2月27日
 */
package org.alan.mars.uid.impl;

import org.alan.mars.uid.UidDao;
import org.alan.mars.uid.UidTypeEnum;
import org.alan.mars.uid.Uid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * @author Alan
 * @since 1.0
 */
public class UidDaoImpl implements UidDao, CommandLineRunner {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public void run(String... args) throws Exception {
        initUid();
    }

    private void initUid() {
        for (UidTypeEnum key : UidTypeEnum.values()) {
            Criteria criteria = Criteria.where("name").is(key.name());
            Query query = new Query(criteria);
            Update update = new Update();
            update.setOnInsert("lastId", key.getBeginNum());
            FindAndModifyOptions options = new FindAndModifyOptions();
            options.upsert(true);
            options.returnNew(true);
            mongoTemplate.findAndModify(query, update, options, Uid.class);
        }
    }

    @Override
    public long getAndUpdateUid(UidTypeEnum key, int num) {
        if (num <= 0 || num == Integer.MAX_VALUE) {
            throw new RuntimeException("参数错误，key=" + key + ",num=" + num);
        }
        Criteria criteria = Criteria.where("name").is(key.name());
        Query query = new Query(criteria);
        Update update = new Update();
        update.inc("lastId", num);
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.upsert(true);
        options.returnNew(true);
        Uid uid = mongoTemplate.findAndModify(query, update, options, Uid.class);
        return uid.getLastId();
    }
}
