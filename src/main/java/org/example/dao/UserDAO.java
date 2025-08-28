package org.example.dao;

import org.example.HibernateConfiguration;
import org.example.entity.User;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class UserDAO {
    public void create(User user){
        try(Session session = HibernateConfiguration.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
        }
        catch (Exception e){
            System.err.println("Ошибка при создании: " + e.getMessage());
        }
    }
}
