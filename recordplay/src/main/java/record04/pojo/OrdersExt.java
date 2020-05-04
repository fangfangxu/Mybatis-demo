package record04.pojo;

public class OrdersExt extends Orders {
   //用户对象
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "OrdersExt{" +
                "user=" + user+",number="+this.getNumber() +
                '}';
    }

}
