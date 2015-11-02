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
import model.Grid;
import model.GridPresenter;
import model.Vote;

/**
 *
 * @author mdamaceno
 */
public class GridJpaController implements Serializable {

    public GridJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Grid grid) {
        if (grid.getCommentList() == null) {
            grid.setCommentList(new ArrayList<Comment>());
        }
        if (grid.getGridPresenterList() == null) {
            grid.setGridPresenterList(new ArrayList<GridPresenter>());
        }
        if (grid.getVoteList() == null) {
            grid.setVoteList(new ArrayList<Vote>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Comment> attachedCommentList = new ArrayList<Comment>();
            for (Comment commentListCommentToAttach : grid.getCommentList()) {
                commentListCommentToAttach = em.getReference(commentListCommentToAttach.getClass(), commentListCommentToAttach.getId());
                attachedCommentList.add(commentListCommentToAttach);
            }
            grid.setCommentList(attachedCommentList);
            List<GridPresenter> attachedGridPresenterList = new ArrayList<GridPresenter>();
            for (GridPresenter gridPresenterListGridPresenterToAttach : grid.getGridPresenterList()) {
                gridPresenterListGridPresenterToAttach = em.getReference(gridPresenterListGridPresenterToAttach.getClass(), gridPresenterListGridPresenterToAttach.getId());
                attachedGridPresenterList.add(gridPresenterListGridPresenterToAttach);
            }
            grid.setGridPresenterList(attachedGridPresenterList);
            List<Vote> attachedVoteList = new ArrayList<Vote>();
            for (Vote voteListVoteToAttach : grid.getVoteList()) {
                voteListVoteToAttach = em.getReference(voteListVoteToAttach.getClass(), voteListVoteToAttach.getId());
                attachedVoteList.add(voteListVoteToAttach);
            }
            grid.setVoteList(attachedVoteList);
            em.persist(grid);
            for (Comment commentListComment : grid.getCommentList()) {
                Grid oldGridIdOfCommentListComment = commentListComment.getGridId();
                commentListComment.setGridId(grid);
                commentListComment = em.merge(commentListComment);
                if (oldGridIdOfCommentListComment != null) {
                    oldGridIdOfCommentListComment.getCommentList().remove(commentListComment);
                    oldGridIdOfCommentListComment = em.merge(oldGridIdOfCommentListComment);
                }
            }
            for (GridPresenter gridPresenterListGridPresenter : grid.getGridPresenterList()) {
                Grid oldGridIdOfGridPresenterListGridPresenter = gridPresenterListGridPresenter.getGridId();
                gridPresenterListGridPresenter.setGridId(grid);
                gridPresenterListGridPresenter = em.merge(gridPresenterListGridPresenter);
                if (oldGridIdOfGridPresenterListGridPresenter != null) {
                    oldGridIdOfGridPresenterListGridPresenter.getGridPresenterList().remove(gridPresenterListGridPresenter);
                    oldGridIdOfGridPresenterListGridPresenter = em.merge(oldGridIdOfGridPresenterListGridPresenter);
                }
            }
            for (Vote voteListVote : grid.getVoteList()) {
                Grid oldGridIdOfVoteListVote = voteListVote.getGridId();
                voteListVote.setGridId(grid);
                voteListVote = em.merge(voteListVote);
                if (oldGridIdOfVoteListVote != null) {
                    oldGridIdOfVoteListVote.getVoteList().remove(voteListVote);
                    oldGridIdOfVoteListVote = em.merge(oldGridIdOfVoteListVote);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Grid grid) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Grid persistentGrid = em.find(Grid.class, grid.getId());
            List<Comment> commentListOld = persistentGrid.getCommentList();
            List<Comment> commentListNew = grid.getCommentList();
            List<GridPresenter> gridPresenterListOld = persistentGrid.getGridPresenterList();
            List<GridPresenter> gridPresenterListNew = grid.getGridPresenterList();
            List<Vote> voteListOld = persistentGrid.getVoteList();
            List<Vote> voteListNew = grid.getVoteList();
            List<String> illegalOrphanMessages = null;
            for (Comment commentListOldComment : commentListOld) {
                if (!commentListNew.contains(commentListOldComment)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Comment " + commentListOldComment + " since its gridId field is not nullable.");
                }
            }
            for (GridPresenter gridPresenterListOldGridPresenter : gridPresenterListOld) {
                if (!gridPresenterListNew.contains(gridPresenterListOldGridPresenter)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain GridPresenter " + gridPresenterListOldGridPresenter + " since its gridId field is not nullable.");
                }
            }
            for (Vote voteListOldVote : voteListOld) {
                if (!voteListNew.contains(voteListOldVote)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Vote " + voteListOldVote + " since its gridId field is not nullable.");
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
            grid.setCommentList(commentListNew);
            List<GridPresenter> attachedGridPresenterListNew = new ArrayList<GridPresenter>();
            for (GridPresenter gridPresenterListNewGridPresenterToAttach : gridPresenterListNew) {
                gridPresenterListNewGridPresenterToAttach = em.getReference(gridPresenterListNewGridPresenterToAttach.getClass(), gridPresenterListNewGridPresenterToAttach.getId());
                attachedGridPresenterListNew.add(gridPresenterListNewGridPresenterToAttach);
            }
            gridPresenterListNew = attachedGridPresenterListNew;
            grid.setGridPresenterList(gridPresenterListNew);
            List<Vote> attachedVoteListNew = new ArrayList<Vote>();
            for (Vote voteListNewVoteToAttach : voteListNew) {
                voteListNewVoteToAttach = em.getReference(voteListNewVoteToAttach.getClass(), voteListNewVoteToAttach.getId());
                attachedVoteListNew.add(voteListNewVoteToAttach);
            }
            voteListNew = attachedVoteListNew;
            grid.setVoteList(voteListNew);
            grid = em.merge(grid);
            for (Comment commentListNewComment : commentListNew) {
                if (!commentListOld.contains(commentListNewComment)) {
                    Grid oldGridIdOfCommentListNewComment = commentListNewComment.getGridId();
                    commentListNewComment.setGridId(grid);
                    commentListNewComment = em.merge(commentListNewComment);
                    if (oldGridIdOfCommentListNewComment != null && !oldGridIdOfCommentListNewComment.equals(grid)) {
                        oldGridIdOfCommentListNewComment.getCommentList().remove(commentListNewComment);
                        oldGridIdOfCommentListNewComment = em.merge(oldGridIdOfCommentListNewComment);
                    }
                }
            }
            for (GridPresenter gridPresenterListNewGridPresenter : gridPresenterListNew) {
                if (!gridPresenterListOld.contains(gridPresenterListNewGridPresenter)) {
                    Grid oldGridIdOfGridPresenterListNewGridPresenter = gridPresenterListNewGridPresenter.getGridId();
                    gridPresenterListNewGridPresenter.setGridId(grid);
                    gridPresenterListNewGridPresenter = em.merge(gridPresenterListNewGridPresenter);
                    if (oldGridIdOfGridPresenterListNewGridPresenter != null && !oldGridIdOfGridPresenterListNewGridPresenter.equals(grid)) {
                        oldGridIdOfGridPresenterListNewGridPresenter.getGridPresenterList().remove(gridPresenterListNewGridPresenter);
                        oldGridIdOfGridPresenterListNewGridPresenter = em.merge(oldGridIdOfGridPresenterListNewGridPresenter);
                    }
                }
            }
            for (Vote voteListNewVote : voteListNew) {
                if (!voteListOld.contains(voteListNewVote)) {
                    Grid oldGridIdOfVoteListNewVote = voteListNewVote.getGridId();
                    voteListNewVote.setGridId(grid);
                    voteListNewVote = em.merge(voteListNewVote);
                    if (oldGridIdOfVoteListNewVote != null && !oldGridIdOfVoteListNewVote.equals(grid)) {
                        oldGridIdOfVoteListNewVote.getVoteList().remove(voteListNewVote);
                        oldGridIdOfVoteListNewVote = em.merge(oldGridIdOfVoteListNewVote);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = grid.getId();
                if (findGrid(id) == null) {
                    throw new NonexistentEntityException("The grid with id " + id + " no longer exists.");
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
            Grid grid;
            try {
                grid = em.getReference(Grid.class, id);
                grid.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The grid with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Comment> commentListOrphanCheck = grid.getCommentList();
            for (Comment commentListOrphanCheckComment : commentListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Grid (" + grid + ") cannot be destroyed since the Comment " + commentListOrphanCheckComment + " in its commentList field has a non-nullable gridId field.");
            }
            List<GridPresenter> gridPresenterListOrphanCheck = grid.getGridPresenterList();
            for (GridPresenter gridPresenterListOrphanCheckGridPresenter : gridPresenterListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Grid (" + grid + ") cannot be destroyed since the GridPresenter " + gridPresenterListOrphanCheckGridPresenter + " in its gridPresenterList field has a non-nullable gridId field.");
            }
            List<Vote> voteListOrphanCheck = grid.getVoteList();
            for (Vote voteListOrphanCheckVote : voteListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Grid (" + grid + ") cannot be destroyed since the Vote " + voteListOrphanCheckVote + " in its voteList field has a non-nullable gridId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(grid);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Grid> findGridEntities() {
        return findGridEntities(true, -1, -1);
    }

    public List<Grid> findGridEntities(int maxResults, int firstResult) {
        return findGridEntities(false, maxResults, firstResult);
    }

    private List<Grid> findGridEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Grid.class));
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

    public Grid findGrid(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Grid.class, id);
        } finally {
            em.close();
        }
    }

    public int getGridCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Grid> rt = cq.from(Grid.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
