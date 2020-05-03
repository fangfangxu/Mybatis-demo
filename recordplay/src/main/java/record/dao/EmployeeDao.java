package record.dao;

import record.pojo.Employee;


public interface EmployeeDao {
    Employee findEmployeeBySn(String sn);
}
