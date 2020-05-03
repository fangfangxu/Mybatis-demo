package record02;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;
import record02.dao.EmployeeMapper;
import record02.pojo.Employee;

import java.io.InputStream;

public class TestRecord02 {
    private SqlSessionFactory sqlSessionFactory;

    @Before
    public void init() throws Exception {
        //加载全局配置文件
        String location = "record02/SqlMapConfig.xml";
        InputStream resourceAsStream =
                this.getClass().getClassLoader().getResourceAsStream(location);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
    }


    @Test
    public void testFindEmployeeBySn() {
//        EmployeeMapper employeeDao = new EmployeeDaoImpl(sqlSessionFactory);
//        Employee employee = employeeDao.findEmployeeBySn("10002");
//        System.out.println(employee);
        //(1)创建EmployeeMapper对象：只有接口没有实现类、交给Mybatis创建
        //(2)调用EmployeeMapper对象的API
        SqlSession sqlSession = sqlSessionFactory.openSession();
        EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
        Employee employee = mapper.findEmployeeBySn("10003");
       System.out.println(employee);

    }
}
