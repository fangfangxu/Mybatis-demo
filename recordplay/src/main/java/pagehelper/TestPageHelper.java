package pagehelper;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;
import pagehelper.dao.UserMapper;
import pagehelper.pojo.User;


import java.io.InputStream;
import java.util.List;

public class TestPageHelper {
    private SqlSessionFactory sqlSessionFactory;


    @Before
    public void init() {
        String location = "pagehelper/SqlMapConfig.xml";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(location);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(ins);
    }

    @Test
    public void test() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        PageHelper.startPage(1,2);
        List<User> users = userMapper.selectUsers();
        PageInfo<User>  page = new PageInfo<User> (users);
        System.out.println(page.getTotal());//总共条数
        System.out.println(page.getPageSize());//每页大小
        System.out.println(page.getPageNum());//当前页
        List<User> rst=page.getList();
        System.out.println(rst);
    }
}
