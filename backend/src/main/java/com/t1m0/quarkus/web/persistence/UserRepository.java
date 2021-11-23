package com.t1m0.quarkus.web.persistence;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import com.t1m0.quarkus.web.Page;
import com.t1m0.quarkus.web.SortDirection;
import com.t1m0.quarkus.web.model.User;
import org.apache.commons.lang3.StringUtils;

@ApplicationScoped
public class UserRepository {

    private final EntityManager entityManager;

    @Inject
    public UserRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public User save(User user) {
        if (StringUtils.isEmpty(user.getUuid())) {
            entityManager.persist(user);
            return user;
        } else {
            return entityManager.merge(user);
        }
    }

    public User find(String uuid) {
        return entityManager.find(User.class, uuid);
    }

    public List<User> findAll() {
        TypedQuery<User> typedQuery = entityManager.createQuery("select u from User u", User.class);
        return typedQuery.getResultList();
    }

    public long count(Map<String, ?> filterBy) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<User> root = query.from(User.class);
        query.select(cb.count(root));
        entityManager.createQuery(query);
        query.where(getPredicates(root, cb, filterBy));
        return entityManager.createQuery(query).getSingleResult();
    }

    public Page<User> findAll(int limit, int page, Map<String, ?> filterBy, Map<String, SortDirection> orderBy) {
        long count = count(filterBy);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);
        query = query.where(getPredicates(root, cb, filterBy));
        query = query.orderBy(getOrderBy(cb, root, orderBy));
        TypedQuery<User> typedQuery = entityManager.createQuery(query);
        int start = page * limit;
        typedQuery = typedQuery.setFirstResult(start);
        typedQuery = typedQuery.setMaxResults(limit);
        List<User> result = typedQuery.getResultList();
        return new Page<>(result, page, limit, count);
    }

    private Predicate[] getPredicates(Root<User> root, CriteriaBuilder criteriaBuilder, Map<String, ?> filterBy) {
        List<Predicate> predicates = new LinkedList<>();
        for (Map.Entry<String, ?> filter : filterBy.entrySet()) {
            predicates.add(criteriaBuilder.equal(getPath(root, filter.getKey()), filter.getValue()));
        }
        return predicates.stream().toArray(Predicate[]::new);
    }

    private List<Order> getOrderBy(CriteriaBuilder cb, Root<User> root, Map<String, SortDirection> orderBy) {
        List<Order> orderCollection = new ArrayList<>();
        for (Map.Entry<String, SortDirection> entry : orderBy.entrySet()) {
            Order order;
            if (SortDirection.ASC.equals(entry.getValue())) {
                order = cb.asc(getPath(root, entry.getKey()));
            } else {
                order = cb.desc(getPath(root, entry.getKey()));
            }
            orderCollection.add(order);
        }
        orderCollection.add(cb.asc(getPath(root, "uuid")));
        return orderCollection;
    }

    private Path<?> getPath(Path<User> root, String path) {
        String[] pathElements = path.split("\\.");
        Path<?> retVal = root;
        for (String pathEl : pathElements) {
            retVal = retVal.get(pathEl);
        }
        return retVal;
    }
}
