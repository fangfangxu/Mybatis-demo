package record04;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;
import record04.dao.UserMapper;
import record04.pojo.OrdersExt;

import java.io.InputStream;

public class TestRecord04 {
    private SqlSessionFactory sqlSessionFactory;


    @Before
    public void init(){
       String location="record04/SqlMapConfig.xml";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(location);
        sqlSessionFactory=new SqlSessionFactoryBuilder().build(ins);
    }



    @Test
    public void findOrdersAndUserRstMap(){
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        OrdersExt ordersExt = mapper.findOrdersAndUserRstMap();
        System.out.println(ordersExt);
    }
}
