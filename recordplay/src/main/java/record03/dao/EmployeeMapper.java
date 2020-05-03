package record03.dao;

import org.apache.ibatis.annotations.Select;
import record03.pojo.Employee;


public interface EmployeeMapper {
    //查询
    @Select("select * from employee where sn=#{sn}")
    Employee findEmployeeBySn(String sn);
}
