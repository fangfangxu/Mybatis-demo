package record01;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;
import record01.dao.EmployeeDao;
import record01.dao.EmployeeDaoImpl;
import record01.pojo.Employee;

import java.io.InputStream;

public class TestRecord {
    private SqlSessionFactory sqlSessionFactory;

    @Before
    public void init() throws Exception {
        //加载全局配置文件
        String location = "record01/SqlMapConfig.xml";
        InputStream resourceAsStream =
                this.getClass().getClassLoader().getResourceAsStream(location);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
    }


    @Test
    public void testFindEmployeeBySn() {
        EmployeeDao employeeDao = new EmployeeDaoImpl(sqlSessionFactory);
        Employee employee = employeeDao.findEmployeeBySn("10002");
        System.out.println(employee);

    }
}
