package org.example.dao;

import org.example.hibernateconfig.HibernateConfiguration;
import org.example.entity.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserDAO {

    private static final Logger log = LoggerFactory.getLogger(UserDAO.class);

    public void create(User user) {
        Transaction transaction = null;
        try (Session session = HibernateConfiguration.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            log.error("Ошибка Hibernate/PostgreSQL при создании пользователя", e);
            throw new RuntimeException("Ошибка при создании пользователя", e);
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            log.error("Неизвестная ошибка при создании пользователя", e);
            throw new RuntimeException("Ошибка при создании пользователя", e);
        }
    }

    public User read(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateConfiguration.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.find(User.class, id);
            transaction.commit();
            return user;
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            log.error("Ошибка Hibernate/PostgreSQL при чтении пользователя", e);
            throw new RuntimeException("Ошибка при чтении пользователя", e);
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            log.error("Неизвестная ошибка при чтении пользователя", e);
            throw new RuntimeException("Ошибка при чтении пользователя", e);
        }
    }

    public void update(User user) {
        Transaction transaction = null;
        try (Session session = HibernateConfiguration.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            log.error("Ошибка Hibernate/PostgreSQL при обновлении пользователя", e);
            throw new RuntimeException("Ошибка при обновлении пользователя", e);
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            log.error("Неизвестная ошибка при обновлении пользователя", e);
            throw new RuntimeException("Ошибка при обновлении пользователя", e);
        }
    }

    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateConfiguration.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.find(User.class, id);
            if (user != null) {
                session.remove(user);
            } else {
                log.warn("Пользователь с ID {} не найден для удаления", id);
            }
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            log.error("Ошибка Hibernate/PostgreSQL при удалении пользователя", e);
            throw new RuntimeException("Ошибка при удалении пользователя", e);
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            log.error("Неизвестная ошибка при удалении пользователя", e);
            throw new RuntimeException("Ошибка при удалении пользователя", e);
        }
    }

    public List<User> findAll() {
        try (Session session = HibernateConfiguration.getSessionFactory().openSession()) {
            List<User> users = session.createQuery("SELECT u FROM User u", User.class).getResultList();
            return users;
        } catch (HibernateException e) {
            log.error("Ошибка Hibernate/PostgreSQL при получении списка пользователей", e);
            throw new RuntimeException("Ошибка при получении списка пользователей", e);
        } catch (Exception e) {
            log.error("Неизвестная ошибка при получении списка пользователей", e);
            throw new RuntimeException("Ошибка при получении списка пользователей", e);
        }
    }
}