package com.imooc.test.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

/**
 * Hbase 操作工具类
 */
public class HbaseUtils {
    private static HBaseAdmin admin = null;
    private static Configuration conf = null;

    /**
     * 私有化构造方法
     */
    private HbaseUtils(){
        conf = new Configuration();
        //TODO:全局变量读入
        conf.set("hbase.zookeeper.quorum", "make.spark.com:2181");
        conf.set("hbase.rootdir", "hdfs://make.spark.com:8020/hbase");

        try {
            admin = new HBaseAdmin(conf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 实现的单例的工具类
     */
    private static HbaseUtils instance = null;

    public  static synchronized HbaseUtils getInstance (){
        if (instance == null){
            instance = new HbaseUtils();
        }
        return instance;
    }

    /**
     * 得到一个HTable的示例对象
     * @param tablename
     * @return
     */
    public  HTable gettable (String tablename){

        HTable table  = null;
        try {
             table = new HTable(conf,tablename) ;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return table ;
    }

    /**
     * 数据的插入
     * @param tablename     表名
     * @param rowkey        rowkey
     * @param cf            列簇
     * @param column        列
     * @param value         列的值
     */
    public  void put (String tablename, String rowkey, String cf, String column, String value){
        //得到一个表的示例
        HTable table = gettable(tablename);
        //对那一个rowkey进行插入
        Put put = new Put(Bytes.toBytes(rowkey));

        //put的具体数据
        put.add(Bytes.toBytes(cf),Bytes.toBytes(column),Bytes.toBytes(value));

        try {
            table.put(put);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
