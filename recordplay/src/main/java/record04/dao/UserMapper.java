package record04.dao;

import record04.pojo.OrdersExt;
import record04.pojo.User;

import java.util.List;

public interface UserMapper {
    //一对一
    OrdersExt findOrdersAndUserRstMap();
   //一对多
    List<User> finduserAndOrderRstMap();
}
