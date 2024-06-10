package com.dot.bankingservice.repository;

import com.dot.bankingservice.enums.TransactionStatus;
import com.dot.bankingservice.models.Transactions;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TransactionRepositoryImpl implements TransactionRepositoryCustom{
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public Page<Transactions> findTransactions(String accountNumber, TransactionStatus status, String transactionReference, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transactions> query = cb.createQuery(Transactions.class);
        Root<Transactions> transaction = query.from(Transactions.class);

        List<Predicate> predicates = new ArrayList<>();

        if (accountNumber != null && !accountNumber.isEmpty()) {
            predicates.add(cb.equal(transaction.get("accountNumber"), accountNumber));
        }

        if (status != null) {
            predicates.add(cb.equal(transaction.get("transactionStatus"), status));
        }

        if (transactionReference != null && !transactionReference.isEmpty()) {
            predicates.add(cb.equal(transaction.get("transactionReference"), transactionReference));
        }

        if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(transaction.get("updatedAt"), LocalDateTime.of(startDate, LocalTime.MIN)));
        }

        if (endDate != null) {
            predicates.add(cb.lessThanOrEqualTo(transaction.get("updatedAt"), LocalDateTime.of(endDate, LocalTime.now())));
        }

        query.where(predicates.toArray(new Predicate[0]));
        TypedQuery<Transactions> typedQuery = entityManager.createQuery(query);

        int totalRows = typedQuery.getResultList().size();

        List<Transactions> resultList = typedQuery
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        return new PageImpl<>(resultList, pageable, totalRows);
    }
}
