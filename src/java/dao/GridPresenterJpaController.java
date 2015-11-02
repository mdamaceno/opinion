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
import model.Presenter;
import model.Grid;
import model.GridPresenter;

/**
 *
 * @author mdamaceno
 */
public class GridPresenterJpaController implements Serializable {

    public GridPresenterJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(GridPresenter gridPresenter) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Presenter presenterId = gridPresenter.getPresenterId();
            if (presenterId != null) {
                presenterId = em.getReference(presenterId.getClass(), presenterId.getId());
                gridPresenter.setPresenterId(presenterId);
            }
            Grid gridId = gridPresenter.getGridId();
            if (gridId != null) {
                gridId = em.getReference(gridId.getClass(), gridId.getId());
                gridPresenter.setGridId(gridId);
            }
            em.persist(gridPresenter);
            if (presenterId != null) {
                presenterId.getGridPresenterList().add(gridPresenter);
                presenterId = em.merge(presenterId);
            }
            if (gridId != null) {
                gridId.getGridPresenterList().add(gridPresenter);
                gridId = em.merge(gridId);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(GridPresenter gridPresenter) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            GridPresenter persistentGridPresenter = em.find(GridPresenter.class, gridPresenter.getId());
            Presenter presenterIdOld = persistentGridPresenter.getPresenterId();
            Presenter presenterIdNew = gridPresenter.getPresenterId();
            Grid gridIdOld = persistentGridPresenter.getGridId();
            Grid gridIdNew = gridPresenter.getGridId();
            if (presenterIdNew != null) {
                presenterIdNew = em.getReference(presenterIdNew.getClass(), presenterIdNew.getId());
                gridPresenter.setPresenterId(presenterIdNew);
            }
            if (gridIdNew != null) {
                gridIdNew = em.getReference(gridIdNew.getClass(), gridIdNew.getId());
                gridPresenter.setGridId(gridIdNew);
            }
            gridPresenter = em.merge(gridPresenter);
            if (presenterIdOld != null && !presenterIdOld.equals(presenterIdNew)) {
                presenterIdOld.getGridPresenterList().remove(gridPresenter);
                presenterIdOld = em.merge(presenterIdOld);
            }
            if (presenterIdNew != null && !presenterIdNew.equals(presenterIdOld)) {
                presenterIdNew.getGridPresenterList().add(gridPresenter);
                presenterIdNew = em.merge(presenterIdNew);
            }
            if (gridIdOld != null && !gridIdOld.equals(gridIdNew)) {
                gridIdOld.getGridPresenterList().remove(gridPresenter);
                gridIdOld = em.merge(gridIdOld);
            }
            if (gridIdNew != null && !gridIdNew.equals(gridIdOld)) {
                gridIdNew.getGridPresenterList().add(gridPresenter);
                gridIdNew = em.merge(gridIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = gridPresenter.getId();
                if (findGridPresenter(id) == null) {
                    throw new NonexistentEntityException("The gridPresenter with id " + id + " no longer exists.");
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
            GridPresenter gridPresenter;
            try {
                gridPresenter = em.getReference(GridPresenter.class, id);
                gridPresenter.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The gridPresenter with id " + id + " no longer exists.", enfe);
            }
            Presenter presenterId = gridPresenter.getPresenterId();
            if (presenterId != null) {
                presenterId.getGridPresenterList().remove(gridPresenter);
                presenterId = em.merge(presenterId);
            }
            Grid gridId = gridPresenter.getGridId();
            if (gridId != null) {
                gridId.getGridPresenterList().remove(gridPresenter);
                gridId = em.merge(gridId);
            }
            em.remove(gridPresenter);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<GridPresenter> findGridPresenterEntities() {
        return findGridPresenterEntities(true, -1, -1);
    }

    public List<GridPresenter> findGridPresenterEntities(int maxResults, int firstResult) {
        return findGridPresenterEntities(false, maxResults, firstResult);
    }

    private List<GridPresenter> findGridPresenterEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(GridPresenter.class));
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

    public GridPresenter findGridPresenter(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(GridPresenter.class, id);
        } finally {
            em.close();
        }
    }

    public int getGridPresenterCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<GridPresenter> rt = cq.from(GridPresenter.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
