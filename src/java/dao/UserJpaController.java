/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dao.exceptions.IllegalOrphanException;
import dao.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.Comment;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import model.User;
import model.Vote;

/**
 *
 * @author mdamaceno
 */
public class UserJpaController implements Serializable {

    public UserJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(User user) {
        if (user.getCommentList() == null) {
            user.setCommentList(new ArrayList<Comment>());
        }
        if (user.getVoteList() == null) {
            user.setVoteList(new ArrayList<Vote>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Comment> attachedCommentList = new ArrayList<Comment>();
            for (Comment commentListCommentToAttach : user.getCommentList()) {
                commentListCommentToAttach = em.getReference(commentListCommentToAttach.getClass(), commentListCommentToAttach.getId());
                attachedCommentList.add(commentListCommentToAttach);
            }
            user.setCommentList(attachedCommentList);
            List<Vote> attachedVoteList = new ArrayList<Vote>();
            for (Vote voteListVoteToAttach : user.getVoteList()) {
                voteListVoteToAttach = em.getReference(voteListVoteToAttach.getClass(), voteListVoteToAttach.getId());
                attachedVoteList.add(voteListVoteToAttach);
            }
            user.setVoteList(attachedVoteList);
            em.persist(user);
            for (Comment commentListComment : user.getCommentList()) {
                User oldUserIdOfCommentListComment = commentListComment.getUserId();
                commentListComment.setUserId(user);
                commentListComment = em.merge(commentListComment);
                if (oldUserIdOfCommentListComment != null) {
                    oldUserIdOfCommentListComment.getCommentList().remove(commentListComment);
                    oldUserIdOfCommentListComment = em.merge(oldUserIdOfCommentListComment);
                }
            }
            for (Vote voteListVote : user.getVoteList()) {
                User oldUserIdOfVoteListVote = voteListVote.getUserId();
                voteListVote.setUserId(user);
                voteListVote = em.merge(voteListVote);
                if (oldUserIdOfVoteListVote != null) {
                    oldUserIdOfVoteListVote.getVoteList().remove(voteListVote);
                    oldUserIdOfVoteListVote = em.merge(oldUserIdOfVoteListVote);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(User user) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User persistentUser = em.find(User.class, user.getId());
            List<Comment> commentListOld = persistentUser.getCommentList();
            List<Comment> commentListNew = user.getCommentList();
            List<Vote> voteListOld = persistentUser.getVoteList();
            List<Vote> voteListNew = user.getVoteList();
            List<String> illegalOrphanMessages = null;
            for (Comment commentListOldComment : commentListOld) {
                if (!commentListNew.contains(commentListOldComment)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Comment " + commentListOldComment + " since its userId field is not nullable.");
                }
            }
            for (Vote voteListOldVote : voteListOld) {
                if (!voteListNew.contains(voteListOldVote)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Vote " + voteListOldVote + " since its userId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Comment> attachedCommentListNew = new ArrayList<Comment>();
            for (Comment commentListNewCommentToAttach : commentListNew) {
                commentListNewCommentToAttach = em.getReference(commentListNewCommentToAttach.getClass(), commentListNewCommentToAttach.getId());
                attachedCommentListNew.add(commentListNewCommentToAttach);
            }
            commentListNew = attachedCommentListNew;
            user.setCommentList(commentListNew);
            List<Vote> attachedVoteListNew = new ArrayList<Vote>();
            for (Vote voteListNewVoteToAttach : voteListNew) {
                voteListNewVoteToAttach = em.getReference(voteListNewVoteToAttach.getClass(), voteListNewVoteToAttach.getId());
                attachedVoteListNew.add(voteListNewVoteToAttach);
            }
            voteListNew = attachedVoteListNew;
            user.setVoteList(voteListNew);
            user = em.merge(user);
            for (Comment commentListNewComment : commentListNew) {
                if (!commentListOld.contains(commentListNewComment)) {
                    User oldUserIdOfCommentListNewComment = commentListNewComment.getUserId();
                    commentListNewComment.setUserId(user);
                    commentListNewComment = em.merge(commentListNewComment);
                    if (oldUserIdOfCommentListNewComment != null && !oldUserIdOfCommentListNewComment.equals(user)) {
                        oldUserIdOfCommentListNewComment.getCommentList().remove(commentListNewComment);
                        oldUserIdOfCommentListNewComment = em.merge(oldUserIdOfCommentListNewComment);
                    }
                }
            }
            for (Vote voteListNewVote : voteListNew) {
                if (!voteListOld.contains(voteListNewVote)) {
                    User oldUserIdOfVoteListNewVote = voteListNewVote.getUserId();
                    voteListNewVote.setUserId(user);
                    voteListNewVote = em.merge(voteListNewVote);
                    if (oldUserIdOfVoteListNewVote != null && !oldUserIdOfVoteListNewVote.equals(user)) {
                        oldUserIdOfVoteListNewVote.getVoteList().remove(voteListNewVote);
                        oldUserIdOfVoteListNewVote = em.merge(oldUserIdOfVoteListNewVote);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = user.getId();
                if (findUser(id) == null) {
                    throw new NonexistentEntityException("The user with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User user;
            try {
                user = em.getReference(User.class, id);
                user.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The user with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Comment> commentListOrphanCheck = user.getCommentList();
            for (Comment commentListOrphanCheckComment : commentListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This User (" + user + ") cannot be destroyed since the Comment " + commentListOrphanCheckComment + " in its commentList field has a non-nullable userId field.");
            }
            List<Vote> voteListOrphanCheck = user.getVoteList();
            for (Vote voteListOrphanCheckVote : voteListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This User (" + user + ") cannot be destroyed since the Vote " + voteListOrphanCheckVote + " in its voteList field has a non-nullable userId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(user);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<User> findUserEntities() {
        return findUserEntities(true, -1, -1);
    }

    public List<User> findUserEntities(int maxResults, int firstResult) {
        return findUserEntities(false, maxResults, firstResult);
    }

    private List<User> findUserEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(User.class));
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

    public User findUser(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<User> rt = cq.from(User.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public User getUserbyDoc(String doc) {
        Query q = getEntityManager().createNamedQuery("User.findByDoc");

        q.setParameter("doc", doc);

        try {
            return (User) q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        } catch (NonUniqueResultException ex) {
            return null;
        }
    }
}
