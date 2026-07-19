/**
 * Author: Johnathan McAllister (McAdmin)
 * Date: 2026-07-03
 * Course:
 * Professor:
 * <p>
 * Purpose:
 * -
 * <p>
 * Constraints:
 * -
 */

package com.app.webserver.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class JpaUtil {

    private static final EntityManagerFactory emf = createFactory();

    private static EntityManagerFactory createFactory() {
        Map<String, String> settings = new HashMap<>();
        putIfSet(settings, "jakarta.persistence.jdbc.url", "TODO_DB_URL", "todo.db.url");
        putIfSet(settings, "jakarta.persistence.jdbc.user", "TODO_DB_USER", "todo.db.user");
        putIfSet(settings, "jakarta.persistence.jdbc.password", "TODO_DB_PASSWORD", "todo.db.password");
        putDecodedPasswordIfSet(settings);
        return Persistence.createEntityManagerFactory("default", settings);
    }

    private static void putDecodedPasswordIfSet(Map<String, String> settings) {
        String encoded = System.getenv("TODO_DB_PASSWORD_BASE64");
        if (encoded == null || encoded.isBlank()) {
            encoded = System.getProperty("todo.db.password.base64");
        }
        if (encoded != null && !encoded.isBlank()) {
            String password = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
            settings.put("jakarta.persistence.jdbc.password", password);
        }
    }

    private static void putIfSet(Map<String, String> settings, String property,
                                 String environmentName, String systemPropertyName) {
        String value = System.getenv(environmentName);
        if (value == null || value.isBlank()) {
            value = System.getProperty(systemPropertyName);
        }
        if (value != null && !value.isBlank()) {
            settings.put(property, value);
        }
    }

    private JpaUtil() {
        // Utility class
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public static void shutdown() {
        if (emf.isOpen()) {
            emf.close();
        }
    }

    public static <T> T runInTransaction(Function<EntityManager, T> action) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            T result = action.apply(em);
            tx.commit();
            return result;
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public static void runInTransactionVoid(Consumer<EntityManager> action) {
        runInTransaction(em -> {
            action.accept(em);
            return null;
        });
    }

    public static <T> boolean hasAny(Class<T> entityClass) {
        return runInTransaction(em ->
                !em.createQuery(
                                "SELECT e FROM " + entityClass.getSimpleName() + " e",
                                entityClass
                        )
                        .setMaxResults(1)
                        .getResultList()
                        .isEmpty()
        );
    }

    public static <T, ID> Optional<T> findById(Class<T> entityClass, ID id) {
        return runInTransaction(em ->
                Optional.ofNullable(em.find(entityClass, id))
        );
    }

    public static <T> List<T> findAll(Class<T> entityClass) {
        return runInTransaction(em ->
                em.createQuery(
                                "SELECT e FROM " + entityClass.getSimpleName() + " e",
                                entityClass
                        )
                        .getResultList()
        );
    }

    public static <T> List<T> findAll(Class<T> entityClass, int maxResults) {
        return runInTransaction(em ->
                em.createQuery(
                                "SELECT e FROM " + entityClass.getSimpleName() + " e",
                                entityClass
                        )
                        .setMaxResults(maxResults)
                        .getResultList()
        );
    }

    public static <T> long count(Class<T> entityClass) {
        return runInTransaction(em ->
                em.createQuery(
                                "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e",
                                Long.class
                        )
                        .getSingleResult()
        );
    }

    public static <T> T save(T entity) {
        return runInTransaction(em -> {
            em.persist(entity);
            return entity;
        });
    }

    public static <T> T update(T entity) {
        return runInTransaction(em ->
                em.merge(entity)
        );
    }

    public static <T> void delete(Class<T> entityClass, Object id) {
        runInTransactionVoid(em -> {
            T entity = em.find(entityClass, id);

            if (entity != null) {
                em.remove(entity);
            }
        });
    }

    public static <T> boolean existsById(Class<T> entityClass, Object id) {
        return runInTransaction(em ->
                em.find(entityClass, id) != null
        );
    }

    public static <T> List<T> findRange(Class<T> entityClass, int start, int limit) {
        return runInTransaction(em ->
                em.createQuery(
                                "SELECT e FROM " + entityClass.getSimpleName() + " e",
                                entityClass
                        )
                        .setFirstResult(start)
                        .setMaxResults(limit)
                        .getResultList()
        );
    }

    public static <T> List<T> findByField(
            Class<T> entityClass,
            String fieldName,
            Object value
    ) {
        return runInTransaction(em ->
                em.createQuery(
                                "SELECT e FROM " + entityClass.getSimpleName() +
                                        " e WHERE e." + fieldName + " = :value",
                                entityClass
                        )
                        .setParameter("value", value)
                        .getResultList()
        );
    }

    public static <T> Optional<T> findOneByField(
            Class<T> entityClass,
            String fieldName,
            Object value
    ) {
        return runInTransaction(em ->
                em.createQuery(
                                "SELECT e FROM " + entityClass.getSimpleName() +
                                        " e WHERE e." + fieldName + " = :value",
                                entityClass
                        )
                        .setParameter("value", value)
                        .setMaxResults(1)
                        .getResultList()
                        .stream()
                        .findFirst()
        );
    }

    public static <T> boolean existsByField(
            Class<T> entityClass,
            String fieldName,
            Object value
    ) {
        return runInTransaction(em ->
                !em.createQuery(
                                "SELECT e FROM " + entityClass.getSimpleName() +
                                        " e WHERE e." + fieldName + " = :value",
                                entityClass
                        )
                        .setParameter("value", value)
                        .setMaxResults(1)
                        .getResultList()
                        .isEmpty()
        );
    }
}
