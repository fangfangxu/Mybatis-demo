package record03;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;
import record03.dao.EmployeeMapper;
import record03.pojo.Employee;

import java.io.InputStream;

public class TestRecord03 {
    private SqlSessionFactory sqlSessionFactory;

    @Before
    public void init() throws Exception {
        //加载全局配置文件
        String location = "record03/SqlMapConfig.xml";
        InputStream resourceAsStream =
                this.getClass().getClassLoader().getResourceAsStream(location);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
    }


    @Test
    public void testFindEmployeeBySn() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
        Employee employee = mapper.findEmployeeBySn("10001");
       System.out.println(employee);

    }
}
