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
import model.Comment;
import model.User;
import model.Grid;

/**
 *
 * @author mdamaceno
 */
public class CommentJpaController implements Serializable {

    public CommentJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Comment comment) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User userId = comment.getUserId();
            if (userId != null) {
                userId = em.getReference(userId.getClass(), userId.getId());
                comment.setUserId(userId);
            }
            Grid gridId = comment.getGridId();
            if (gridId != null) {
                gridId = em.getReference(gridId.getClass(), gridId.getId());
                comment.setGridId(gridId);
            }
            em.persist(comment);
            if (userId != null) {
                userId.getCommentList().add(comment);
                userId = em.merge(userId);
            }
            if (gridId != null) {
                gridId.getCommentList().add(comment);
                gridId = em.merge(gridId);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Comment comment) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Comment persistentComment = em.find(Comment.class, comment.getId());
            User userIdOld = persistentComment.getUserId();
            User userIdNew = comment.getUserId();
            Grid gridIdOld = persistentComment.getGridId();
            Grid gridIdNew = comment.getGridId();
            if (userIdNew != null) {
                userIdNew = em.getReference(userIdNew.getClass(), userIdNew.getId());
                comment.setUserId(userIdNew);
            }
            if (gridIdNew != null) {
                gridIdNew = em.getReference(gridIdNew.getClass(), gridIdNew.getId());
                comment.setGridId(gridIdNew);
            }
            comment = em.merge(comment);
            if (userIdOld != null && !userIdOld.equals(userIdNew)) {
                userIdOld.getCommentList().remove(comment);
                userIdOld = em.merge(userIdOld);
            }
            if (userIdNew != null && !userIdNew.equals(userIdOld)) {
                userIdNew.getCommentList().add(comment);
                userIdNew = em.merge(userIdNew);
            }
            if (gridIdOld != null && !gridIdOld.equals(gridIdNew)) {
                gridIdOld.getCommentList().remove(comment);
                gridIdOld = em.merge(gridIdOld);
            }
            if (gridIdNew != null && !gridIdNew.equals(gridIdOld)) {
                gridIdNew.getCommentList().add(comment);
                gridIdNew = em.merge(gridIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = comment.getId();
                if (findComment(id) == null) {
                    throw new NonexistentEntityException("The comment with id " + id + " no longer exists.");
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
            Comment comment;
            try {
                comment = em.getReference(Comment.class, id);
                comment.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The comment with id " + id + " no longer exists.", enfe);
            }
            User userId = comment.getUserId();
            if (userId != null) {
                userId.getCommentList().remove(comment);
                userId = em.merge(userId);
            }
            Grid gridId = comment.getGridId();
            if (gridId != null) {
                gridId.getCommentList().remove(comment);
                gridId = em.merge(gridId);
            }
            em.remove(comment);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Comment> findCommentEntities() {
        return findCommentEntities(true, -1, -1);
    }

    public List<Comment> findCommentEntities(int maxResults, int firstResult) {
        return findCommentEntities(false, maxResults, firstResult);
    }

    private List<Comment> findCommentEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Comment.class));
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

    public Comment findComment(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Comment.class, id);
        } finally {
            em.close();
        }
    }

    public int getCommentCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Comment> rt = cq.from(Comment.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
