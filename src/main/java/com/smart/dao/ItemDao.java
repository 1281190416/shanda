package com.smart.dao;

import com.smart.domain.Item;
import com.smart.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dlw on 2017/7/18.
 */
@Repository
public class ItemDao {

    private JdbcTemplate jdbcTemplate;


    //执行替换查找返回list的统一操作, 只能在内部调用
    private List<Item> mulQuery(String sql, Object[] obj) {
        final List<Item> resultList = new LinkedList<Item>();
        jdbcTemplate.query(sql,obj,
                new RowCallbackHandler() {
                    public void processRow(ResultSet rs) throws SQLException {
                        Item item = new Item();
                        item.setId(rs.getString("id"));
                        item.setLayer(rs.getInt("layer"));
                        item.setDescription(rs.getString("description"));
                        item.setName(rs.getString("name"));
                        resultList.add(item);
                    }
                });
        return resultList;
    }

    /**获取某个层次的所有条目信息
     *
     * @param user, 用户
     * @param layer, 层
     * @return
     */
    public List<Item> findByLayer(final User user, final int layer) {
        String sql = " SELECT * FROM t_"+ user.getUserName() + "_prod WHERE layer=?";
        return mulQuery(sql, new Object[]{layer});
        //String sqlStr = " SELECT prodName,prodDetailTable FROM"+ prodTableName + "WHERE prodName =? ";
        //使用反射实现的多条查询返回
        /*final List<Item> resultList = new LinkedList<Item>();
        jdbcTemplate.query(sqlStr,new Object[]{layer},
                new RowCallbackHandler() {
                    public void processRow(ResultSet rs) throws SQLException {
                        Item item = new Item();
                        item.setId(rs.getString("id"));
                        item.setLayer(rs.getInt("layer"));
                        item.setDescription(rs.getString("description"));
                        item.setName(rs.getString("name"));
                        resultList.add(item);
                    }
                });
        return resultList;*/
    }

    /**
     * 获取某个节点id所有的子条目
     */
    public List<Item> findDirectSubByParentId(final User user, final int pid) {
        String sql = " SELECT * FROM t_"+ user.getUserName() + "_prod WHERE id like ?% and layer="+ (pid+1);
        return mulQuery(sql, new Object[]{pid});
    }

    public List<Item> findDirectSubByParentName(final User user, final String name) {
        String sql= " SELECT * FROM t_"+ user.getUserName() +
                "_prod WHERE name="+name;
        int pid = jdbcTemplate.queryForObject(sql,new Object[]{name}, Integer.class);
        //String sql = " SELECT * FROM t_"+ user.getUserName() + "_prod WHERE id like ?% and layer="+ (pid+1);
        return findDirectSubByParentId(user, pid);
    }


    //增加一个新的产品,根据行程和
    public void insertItem(final User user, Item item){
        String sql = "INSERT INTO t_"+ user.getUserName() + "_prod VALUES(?,?,?,?)";
        Object[] args = {item.getId(),item.getLayer(),item.getName(),item.getDescription()};
        jdbcTemplate.update(sql, args);
    }

    /**
     * 根据id来删除当前节点和子节点
     * @param user 用户
     * @param pid pid
     */
    public void removeItem(final User user, final int pid){
        String sql = "DELETE * FROM t_"+ user.getUserName() + "WHERE id like" + pid +"%";
        jdbcTemplate.update(sql);
    }



    /*
        使用Autowired将Spring容器中的jdbcTemplate Bean注入进来
     */
    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}



