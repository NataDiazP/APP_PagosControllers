/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.polijic.APP_Pagos.Controllers;

import co.edu.polijic.APP_Pagos.Controllers.exceptions.NonexistentEntityException;
import co.edu.polijic.APP_Pagos.Controllers.exceptions.PreexistingEntityException;
import co.edu.polijic.app_pagos.model.RegistroTransaccion;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import co.edu.polijic.app_pagos.model.Transaccion;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author felipe
 */
public class RegistroTransaccionJpaController implements Serializable {

    public RegistroTransaccionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RegistroTransaccion registroTransaccion) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Transaccion cdtransaccion = registroTransaccion.getCdtransaccion();
            if (cdtransaccion != null) {
                cdtransaccion = em.getReference(cdtransaccion.getClass(), cdtransaccion.getCdtransaccion());
                registroTransaccion.setCdtransaccion(cdtransaccion);
            }
            em.persist(registroTransaccion);
            if (cdtransaccion != null) {
                cdtransaccion.getRegistroTransaccionList().add(registroTransaccion);
                cdtransaccion = em.merge(cdtransaccion);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRegistroTransaccion(registroTransaccion.getCdregistro()) != null) {
                throw new PreexistingEntityException("RegistroTransaccion " + registroTransaccion + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RegistroTransaccion registroTransaccion) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RegistroTransaccion persistentRegistroTransaccion = em.find(RegistroTransaccion.class, registroTransaccion.getCdregistro());
            Transaccion cdtransaccionOld = persistentRegistroTransaccion.getCdtransaccion();
            Transaccion cdtransaccionNew = registroTransaccion.getCdtransaccion();
            if (cdtransaccionNew != null) {
                cdtransaccionNew = em.getReference(cdtransaccionNew.getClass(), cdtransaccionNew.getCdtransaccion());
                registroTransaccion.setCdtransaccion(cdtransaccionNew);
            }
            registroTransaccion = em.merge(registroTransaccion);
            if (cdtransaccionOld != null && !cdtransaccionOld.equals(cdtransaccionNew)) {
                cdtransaccionOld.getRegistroTransaccionList().remove(registroTransaccion);
                cdtransaccionOld = em.merge(cdtransaccionOld);
            }
            if (cdtransaccionNew != null && !cdtransaccionNew.equals(cdtransaccionOld)) {
                cdtransaccionNew.getRegistroTransaccionList().add(registroTransaccion);
                cdtransaccionNew = em.merge(cdtransaccionNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = registroTransaccion.getCdregistro();
                if (findRegistroTransaccion(id) == null) {
                    throw new NonexistentEntityException("The registroTransaccion with id " + id + " no longer exists.");
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
            RegistroTransaccion registroTransaccion;
            try {
                registroTransaccion = em.getReference(RegistroTransaccion.class, id);
                registroTransaccion.getCdregistro();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The registroTransaccion with id " + id + " no longer exists.", enfe);
            }
            Transaccion cdtransaccion = registroTransaccion.getCdtransaccion();
            if (cdtransaccion != null) {
                cdtransaccion.getRegistroTransaccionList().remove(registroTransaccion);
                cdtransaccion = em.merge(cdtransaccion);
            }
            em.remove(registroTransaccion);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RegistroTransaccion> findRegistroTransaccionEntities() {
        return findRegistroTransaccionEntities(true, -1, -1);
    }

    public List<RegistroTransaccion> findRegistroTransaccionEntities(int maxResults, int firstResult) {
        return findRegistroTransaccionEntities(false, maxResults, firstResult);
    }

    private List<RegistroTransaccion> findRegistroTransaccionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RegistroTransaccion.class));
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

    public RegistroTransaccion findRegistroTransaccion(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RegistroTransaccion.class, id);
        } finally {
            em.close();
        }
    }

    public int getRegistroTransaccionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RegistroTransaccion> rt = cq.from(RegistroTransaccion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
