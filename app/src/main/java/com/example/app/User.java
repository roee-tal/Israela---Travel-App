package com.example.app;

import java.util.Comparator;

public class User {

    private String Email;
    private String Id;
    private String isUser;

    public User(String Email, String Id, String isUser){
        this.Email = Email;
        this.Id = Id;
        this.isUser = isUser;
    }

    public String getEmail(){
        return this.Email;
    }

    public void setEmail(String Email){
        this.Email = Email;
    }

    public String getId(){
        return this.Id;
    }

    public void setId(String Id){
        this.Id = Id;
    }

    public String getIs(){
        return this.isUser;
    }

    public void setIs(String isUser){
        this.isUser = isUser;
    }

    public static Comparator<User> idAscending = new Comparator<User>()
    {
        @Override
        public int compare(User shape1, User shape2)
        {
            int id1 = Integer.valueOf(shape1.getId());
            int id2 = Integer.valueOf(shape2.getId());

            return Integer.compare(id1, id2);
        }
    };

    public static Comparator<User> nameAscending = new Comparator<User>()
    {
        @Override
        public int compare(User shape1, User shape2)
        {
            String name1 = shape1.getEmail();
            String name2 = shape2.getEmail();
            name1 = name1.toLowerCase();
            name2 = name2.toLowerCase();

            return name1.compareTo(name2);
        }
    };
}
