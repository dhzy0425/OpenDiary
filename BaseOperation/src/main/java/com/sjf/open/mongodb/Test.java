package com.sjf.open.mongodb;

import com.beust.jcommander.internal.Lists;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by xiaosi on 16-8-23.
 */
public class Test {
    private static Logger logger = LoggerFactory.getLogger(Test.class);
    private static String dataBaseName = "test";
    private static final String ip = "localhost";
    private static final int port = 27017;

    private static Block<Document> printBlock = new Block<Document>() {
        public void apply(final Document document) {
            logger.info("-------- printBlock {}",document.toJson());
        }
    };

    /**
     * 连接数据库
     * @param dataBaseName
     * @return
     */
    public static MongoDatabase getDatabase(String dataBaseName){
        // 连接到 mongodb 服务
        MongoClient mongoClient = new MongoClient(ip, port);
        // 连接到数据库
        MongoDatabase mongoDatabase = mongoClient.getDatabase(dataBaseName);
        logger.info("Connect to database successfully");
        return mongoDatabase;
    }

    /**
     * 创建集合
     * @param collectionName
     * @param database
     */
    public static void createCollection(String collectionName, MongoDatabase database){
        database.createCollection(collectionName);
    }

    /**
     * 获取集合
     * @param collectionName
     * @param database
     * @return
     */
    public static MongoCollection<Document> getCollection(String collectionName, MongoDatabase database){
        MongoCollection<Document> collection = database.getCollection(collectionName);
        return collection;
    }

    /**
     * 插入文档
     * @param collectionName
     * @param database
     */
    public static void insertDocument(String collectionName, MongoDatabase database){
        Document document = new Document("title", "MongoDB").
                append("author", "Chodorow").
                append("year", "2012-9-24").
                append("price", 38.99);


        Document document2 = new Document("title", "MongoDB实战").
                append("author", "丁雪丰").
                append("year", "2012-10").
                append("price", 59.0);

        List<Document> documentList = Lists.newArrayList();
        documentList.add(document);
        documentList.add(document2);

        // 获取集合
        MongoCollection<Document> collection = getCollection(collectionName, database);
        // 插入集合中
        collection.insertMany(documentList);
    }

    /**
     * 检索集合中所有文档
     * @param collection
     */
    public static void findAll(MongoCollection<Document> collection){
        FindIterable<Document> findIterable = collection.find();
        MongoCursor<Document> mongoCursor = findIterable.iterator();

        while(mongoCursor.hasNext()){
            logger.info("--------- document {}", mongoCursor.next());
        }
    }

    /**
     * 检索第一个文档
     * @param collection
     */
    public static void findFirst(MongoCollection<Document> collection){
        Document document = collection.find().first();
        logger.info("---------- findFirst {}", document.toJson());
    }

    /**
     * 使用检索过滤器获取文档
     * @param collection
     */
    public static void findByFilter(MongoCollection<Document> collection){
        collection.find(Filters.eq("author","丁雪丰")).forEach(printBlock);
        collection.find(Filters.gt("price",39)).forEach(printBlock);
        collection.find(Filters.and(Filters.gt("price",39), Filters.lt("price", 40))).forEach(printBlock);
    }

    /**
     * 更新文档
     * @param collection
     */
    public static void update(MongoCollection<Document> collection){
        //更新文档   将文档中price=59.0的文档修改为price=52.0
        collection.updateMany(Filters.eq("price", 59.0), new Document("$set",new Document("price", 52.0)));
    }

    /**
     * 删除文档
     * @param collection
     */
    public static void delete(MongoCollection collection){
        //删除符合条件的第一个文档
        DeleteResult deleteResult = collection.deleteOne(Filters.eq("price", 38.99));
        logger.info("-------- delete {}", deleteResult.toString());
        //删除所有符合条件的文档
        //DeleteResult deleteResult = collection.deleteMany (Filters.eq("price", 52.0));
    }

    public static void main(String[] args) {
        MongoDatabase mongoDatabase = getDatabase(dataBaseName);
        //createCollection("Book", mongoDatabase);
        //getCollection("Book", mongoDatabase);
        //insertDocument("Book", mongoDatabase);
        MongoCollection collection = getCollection("Book", mongoDatabase);
        findAll(collection);
        //update(collection);
        delete(collection);
        //findAll(collection);
        //findFirst(collection);
        //findByFilter(collection);
    }
}
