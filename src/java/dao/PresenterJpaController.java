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
import model.GridPresenter;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import model.Presenter;

/**
 *
 * @author mdamaceno
 */
public class PresenterJpaController implements Serializable {

    public PresenterJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Presenter presenter) {
        if (presenter.getGridPresenterList() == null) {
            presenter.setGridPresenterList(new ArrayList<GridPresenter>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<GridPresenter> attachedGridPresenterList = new ArrayList<GridPresenter>();
            for (GridPresenter gridPresenterListGridPresenterToAttach : presenter.getGridPresenterList()) {
                gridPresenterListGridPresenterToAttach = em.getReference(gridPresenterListGridPresenterToAttach.getClass(), gridPresenterListGridPresenterToAttach.getId());
                attachedGridPresenterList.add(gridPresenterListGridPresenterToAttach);
            }
            presenter.setGridPresenterList(attachedGridPresenterList);
            em.persist(presenter);
            for (GridPresenter gridPresenterListGridPresenter : presenter.getGridPresenterList()) {
                Presenter oldPresenterIdOfGridPresenterListGridPresenter = gridPresenterListGridPresenter.getPresenterId();
                gridPresenterListGridPresenter.setPresenterId(presenter);
                gridPresenterListGridPresenter = em.merge(gridPresenterListGridPresenter);
                if (oldPresenterIdOfGridPresenterListGridPresenter != null) {
                    oldPresenterIdOfGridPresenterListGridPresenter.getGridPresenterList().remove(gridPresenterListGridPresenter);
                    oldPresenterIdOfGridPresenterListGridPresenter = em.merge(oldPresenterIdOfGridPresenterListGridPresenter);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Presenter presenter) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Presenter persistentPresenter = em.find(Presenter.class, presenter.getId());
            List<GridPresenter> gridPresenterListOld = persistentPresenter.getGridPresenterList();
            List<GridPresenter> gridPresenterListNew = presenter.getGridPresenterList();
            List<String> illegalOrphanMessages = null;
            for (GridPresenter gridPresenterListOldGridPresenter : gridPresenterListOld) {
                if (!gridPresenterListNew.contains(gridPresenterListOldGridPresenter)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain GridPresenter " + gridPresenterListOldGridPresenter + " since its presenterId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<GridPresenter> attachedGridPresenterListNew = new ArrayList<GridPresenter>();
            for (GridPresenter gridPresenterListNewGridPresenterToAttach : gridPresenterListNew) {
                gridPresenterListNewGridPresenterToAttach = em.getReference(gridPresenterListNewGridPresenterToAttach.getClass(), gridPresenterListNewGridPresenterToAttach.getId());
                attachedGridPresenterListNew.add(gridPresenterListNewGridPresenterToAttach);
            }
            gridPresenterListNew = attachedGridPresenterListNew;
            presenter.setGridPresenterList(gridPresenterListNew);
            presenter = em.merge(presenter);
            for (GridPresenter gridPresenterListNewGridPresenter : gridPresenterListNew) {
                if (!gridPresenterListOld.contains(gridPresenterListNewGridPresenter)) {
                    Presenter oldPresenterIdOfGridPresenterListNewGridPresenter = gridPresenterListNewGridPresenter.getPresenterId();
                    gridPresenterListNewGridPresenter.setPresenterId(presenter);
                    gridPresenterListNewGridPresenter = em.merge(gridPresenterListNewGridPresenter);
                    if (oldPresenterIdOfGridPresenterListNewGridPresenter != null && !oldPresenterIdOfGridPresenterListNewGridPresenter.equals(presenter)) {
                        oldPresenterIdOfGridPresenterListNewGridPresenter.getGridPresenterList().remove(gridPresenterListNewGridPresenter);
                        oldPresenterIdOfGridPresenterListNewGridPresenter = em.merge(oldPresenterIdOfGridPresenterListNewGridPresenter);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = presenter.getId();
                if (findPresenter(id) == null) {
                    throw new NonexistentEntityException("The presenter with id " + id + " no longer exists.");
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
            Presenter presenter;
            try {
                presenter = em.getReference(Presenter.class, id);
                presenter.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The presenter with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<GridPresenter> gridPresenterListOrphanCheck = presenter.getGridPresenterList();
            for (GridPresenter gridPresenterListOrphanCheckGridPresenter : gridPresenterListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Presenter (" + presenter + ") cannot be destroyed since the GridPresenter " + gridPresenterListOrphanCheckGridPresenter + " in its gridPresenterList field has a non-nullable presenterId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(presenter);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Presenter> findPresenterEntities() {
        return findPresenterEntities(true, -1, -1);
    }

    public List<Presenter> findPresenterEntities(int maxResults, int firstResult) {
        return findPresenterEntities(false, maxResults, firstResult);
    }

    private List<Presenter> findPresenterEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Presenter.class));
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

    public Presenter findPresenter(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Presenter.class, id);
        } finally {
            em.close();
        }
    }

    public int getPresenterCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Presenter> rt = cq.from(Presenter.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
