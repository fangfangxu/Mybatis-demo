package record01.dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import record01.pojo.Employee;


public class EmployeeDaoImpl implements EmployeeDao {
    private SqlSessionFactory sqlSessionFactory;

    //注入sqlSessionFactory
    public EmployeeDaoImpl(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public Employee findEmployeeBySn(String sn) {
        //从sqlSessionFactory工厂类中创建SqlSession会话
        SqlSession sqlSession = sqlSessionFactory.openSession();
        Employee e = sqlSession.selectOne("test.findEmployeeBySn", sn);
        return e;
    }

}
