package com.smart.dao;

import com.smart.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

/*
    定义一个操作User的DAO Bean
 */
@Repository
public class UserDao {
    private JdbcTemplate jdbcTemplate;

    private final static String MATCH_COUNT_SQL = " SELECT count(*) FROM t_user  " +
            " WHERE user_name =? and password=? ";
    private final static String UPDATE_LOGIN_INFO_SQL = " UPDATE t_user SET " +
            " last_visit=?,last_ip=?,credits=?  WHERE user_id =?";

    public int getMatchCount(String userName, String password) {

        return jdbcTemplate.queryForObject(MATCH_COUNT_SQL, new Object[]{userName, password}, Integer.class);
    }

    /*
        根据用户名称返回用户信息
     */
    public User findUserByUserName(final String userName) {
        String sqlStr = " SELECT user_id,user_name,credits "
                + " FROM t_user WHERE user_name =? ";
        final User user = new User();
        jdbcTemplate.query(sqlStr, new Object[]{userName},
                new RowCallbackHandler() {
                    public void processRow(ResultSet rs) throws SQLException {
                        user.setUserId(rs.getInt("user_id"));
                        user.setUserName(userName);
                        user.setCredits(rs.getInt("credits"));
                    }
                });
        return user;
    }

    /*
        查找用户名称是否存在
     */
    public boolean findUserName(final String userName){
        String sql = "SELECT user_id FROM t_user where user_name =\"" + userName +"\"";
        try {
            Integer id = jdbcTemplate.queryForObject(sql, Integer.class);
            if (id != null) return true;
        }
        catch(EmptyResultDataAccessException e){
            return false;
        }
        return false;
    }

    /*
        更新登录日志
     */
    public void updateLoginInfo(User user) {
        jdbcTemplate.update(UPDATE_LOGIN_INFO_SQL, new Object[]{user.getLastVisit(),
                user.getLastIp(), user.getCredits(), user.getUserId()});
    }

    /**
     * 为每个用户创建一张单独的数据表
     * @param user 用户
     */
    public void createUserProdTable(User user){
        String sql = "create table t_" + user.getUserName() + "_prod" +
            "(id varchar(20) not null primary key,"+
            "layer int not null,"+
            "name varchar(6) not null,"+
            "description varchar(30));";
        jdbcTemplate.update(sql);
    }

    public void insertUser(User user){
        String sql = "INSERT INTO t_user values(?,?,?,?,?,?)";
        Object[] args = {user.getUserId(),user.getUserName(),user.getCredits(),user.getPassword(),user.getLastVisit(),user.getLastIp()};
        jdbcTemplate.update(sql, args);
    }


    /*
        使用Autowired将Spring容器中的jdbcTemplate Bean注入进来
     */
    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
