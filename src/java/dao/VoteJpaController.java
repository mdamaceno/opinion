/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dao.exceptions.NonexistentEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.User;
import model.Grid;
import model.Vote;

/**
 *
 * @author mdamaceno
 */
public class VoteJpaController implements Serializable {

    public VoteJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Vote vote) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User userId = vote.getUserId();
            if (userId != null) {
                userId = em.getReference(userId.getClass(), userId.getId());
                vote.setUserId(userId);
            }
            Grid gridId = vote.getGridId();
            if (gridId != null) {
                gridId = em.getReference(gridId.getClass(), gridId.getId());
                vote.setGridId(gridId);
            }
            em.persist(vote);
            if (userId != null) {
                userId.getVoteList().add(vote);
                userId = em.merge(userId);
            }
            if (gridId != null) {
                gridId.getVoteList().add(vote);
                gridId = em.merge(gridId);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Vote vote) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Vote persistentVote = em.find(Vote.class, vote.getId());
            User userIdOld = persistentVote.getUserId();
            User userIdNew = vote.getUserId();
            Grid gridIdOld = persistentVote.getGridId();
            Grid gridIdNew = vote.getGridId();
            if (userIdNew != null) {
                userIdNew = em.getReference(userIdNew.getClass(), userIdNew.getId());
                vote.setUserId(userIdNew);
            }
            if (gridIdNew != null) {
                gridIdNew = em.getReference(gridIdNew.getClass(), gridIdNew.getId());
                vote.setGridId(gridIdNew);
            }
            vote = em.merge(vote);
            if (userIdOld != null && !userIdOld.equals(userIdNew)) {
                userIdOld.getVoteList().remove(vote);
                userIdOld = em.merge(userIdOld);
            }
            if (userIdNew != null && !userIdNew.equals(userIdOld)) {
                userIdNew.getVoteList().add(vote);
                userIdNew = em.merge(userIdNew);
            }
            if (gridIdOld != null && !gridIdOld.equals(gridIdNew)) {
                gridIdOld.getVoteList().remove(vote);
                gridIdOld = em.merge(gridIdOld);
            }
            if (gridIdNew != null && !gridIdNew.equals(gridIdOld)) {
                gridIdNew.getVoteList().add(vote);
                gridIdNew = em.merge(gridIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = vote.getId();
                if (findVote(id) == null) {
                    throw new NonexistentEntityException("The vote with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Vote vote;
            try {
                vote = em.getReference(Vote.class, id);
                vote.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The vote with id " + id + " no longer exists.", enfe);
            }
            User userId = vote.getUserId();
            if (userId != null) {
                userId.getVoteList().remove(vote);
                userId = em.merge(userId);
            }
            Grid gridId = vote.getGridId();
            if (gridId != null) {
                gridId.getVoteList().remove(vote);
                gridId = em.merge(gridId);
            }
            em.remove(vote);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Vote> findVoteEntities() {
        return findVoteEntities(true, -1, -1);
    }

    public List<Vote> findVoteEntities(int maxResults, int firstResult) {
        return findVoteEntities(false, maxResults, firstResult);
    }

    private List<Vote> findVoteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Vote.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Vote findVote(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Vote.class, id);
        } finally {
            em.close();
        }
    }

    public int getVoteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Vote> rt = cq.from(Vote.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
