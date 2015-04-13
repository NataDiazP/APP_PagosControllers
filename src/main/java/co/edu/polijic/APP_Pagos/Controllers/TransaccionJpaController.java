/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.polijic.APP_Pagos.Controllers;

import co.edu.polijic.APP_Pagos.Controllers.exceptions.NonexistentEntityException;
import co.edu.polijic.APP_Pagos.Controllers.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import co.edu.polijic.app_pagos.model.Tarjeta;
import co.edu.polijic.app_pagos.model.TipoPago;
import co.edu.polijic.app_pagos.model.Transaccion;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author felipe
 */
public class TransaccionJpaController implements Serializable {

    public TransaccionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Transaccion transaccion) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tarjeta cdtarjetaorigen = transaccion.getCdtarjetaorigen();
            if (cdtarjetaorigen != null) {
                cdtarjetaorigen = em.getReference(cdtarjetaorigen.getClass(), cdtarjetaorigen.getCdtarjeta());
                transaccion.setCdtarjetaorigen(cdtarjetaorigen);
            }
            TipoPago cdtipopago = transaccion.getCdtipopago();
            if (cdtipopago != null) {
                cdtipopago = em.getReference(cdtipopago.getClass(), cdtipopago.getCdtipopago());
                transaccion.setCdtipopago(cdtipopago);
            }
            em.persist(transaccion);
            if (cdtarjetaorigen != null) {
                cdtarjetaorigen.getTransaccionList().add(transaccion);
                cdtarjetaorigen = em.merge(cdtarjetaorigen);
            }
            if (cdtipopago != null) {
                cdtipopago.getTransaccionList().add(transaccion);
                cdtipopago = em.merge(cdtipopago);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTransaccion(transaccion.getCdtransaccion()) != null) {
                throw new PreexistingEntityException("Transaccion " + transaccion + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Transaccion transaccion) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Transaccion persistentTransaccion = em.find(Transaccion.class, transaccion.getCdtransaccion());
            Tarjeta cdtarjetaorigenOld = persistentTransaccion.getCdtarjetaorigen();
            Tarjeta cdtarjetaorigenNew = transaccion.getCdtarjetaorigen();
            TipoPago cdtipopagoOld = persistentTransaccion.getCdtipopago();
            TipoPago cdtipopagoNew = transaccion.getCdtipopago();
            if (cdtarjetaorigenNew != null) {
                cdtarjetaorigenNew = em.getReference(cdtarjetaorigenNew.getClass(), cdtarjetaorigenNew.getCdtarjeta());
                transaccion.setCdtarjetaorigen(cdtarjetaorigenNew);
            }
            if (cdtipopagoNew != null) {
                cdtipopagoNew = em.getReference(cdtipopagoNew.getClass(), cdtipopagoNew.getCdtipopago());
                transaccion.setCdtipopago(cdtipopagoNew);
            }
            transaccion = em.merge(transaccion);
            if (cdtarjetaorigenOld != null && !cdtarjetaorigenOld.equals(cdtarjetaorigenNew)) {
                cdtarjetaorigenOld.getTransaccionList().remove(transaccion);
                cdtarjetaorigenOld = em.merge(cdtarjetaorigenOld);
            }
            if (cdtarjetaorigenNew != null && !cdtarjetaorigenNew.equals(cdtarjetaorigenOld)) {
                cdtarjetaorigenNew.getTransaccionList().add(transaccion);
                cdtarjetaorigenNew = em.merge(cdtarjetaorigenNew);
            }
            if (cdtipopagoOld != null && !cdtipopagoOld.equals(cdtipopagoNew)) {
                cdtipopagoOld.getTransaccionList().remove(transaccion);
                cdtipopagoOld = em.merge(cdtipopagoOld);
            }
            if (cdtipopagoNew != null && !cdtipopagoNew.equals(cdtipopagoOld)) {
                cdtipopagoNew.getTransaccionList().add(transaccion);
                cdtipopagoNew = em.merge(cdtipopagoNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = transaccion.getCdtransaccion();
                if (findTransaccion(id) == null) {
                    throw new NonexistentEntityException("The transaccion with id " + id + " no longer exists.");
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
            Transaccion transaccion;
            try {
                transaccion = em.getReference(Transaccion.class, id);
                transaccion.getCdtransaccion();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The transaccion with id " + id + " no longer exists.", enfe);
            }
            Tarjeta cdtarjetaorigen = transaccion.getCdtarjetaorigen();
            if (cdtarjetaorigen != null) {
                cdtarjetaorigen.getTransaccionList().remove(transaccion);
                cdtarjetaorigen = em.merge(cdtarjetaorigen);
            }
            TipoPago cdtipopago = transaccion.getCdtipopago();
            if (cdtipopago != null) {
                cdtipopago.getTransaccionList().remove(transaccion);
                cdtipopago = em.merge(cdtipopago);
            }
            em.remove(transaccion);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Transaccion> findTransaccionEntities() {
        return findTransaccionEntities(true, -1, -1);
    }

    public List<Transaccion> findTransaccionEntities(int maxResults, int firstResult) {
        return findTransaccionEntities(false, maxResults, firstResult);
    }

    private List<Transaccion> findTransaccionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Transaccion.class));
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

    public Transaccion findTransaccion(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Transaccion.class, id);
        } finally {
            em.close();
        }
    }

    public int getTransaccionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Transaccion> rt = cq.from(Transaccion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public List<Tarjeta> getTransaccionesTarjetaOrigen(int nmtarjeta) {
        EntityManager em = getEntityManager();
        Query q = em.createQuery("SELECT tarj FROM Tarjeta tarj WHERE tarj.nmtarjeta = :nmtarjeta", Tarjeta.class);
        q.setParameter("nmtarjeta", nmtarjeta);
        return q.getResultList();
    }
    
    public List<Tarjeta> getTransaccionesRealizadasCuentaOrigenCuentaDestino(int nmCuentaOrigen,int nmCuentaDestino){
        EntityManager em = getEntityManager();
        Query q = em.createQuery("SELECT tarj FROM Tarjeta tarj INNER JOIN tarj.transaccionList p1 where tarj.nmtarjeta :nmCuentaOrigen and p1.nmtarjetaOrigen :nmtarjetaDestino", Tarjeta.class);
        q.setParameter("nmtarjetaOrigen", nmCuentaOrigen);
        q.setParameter("nmtarjetaDestino", nmCuentaDestino);
        return q.getResultList();
    
    }
    
    public List<Transaccion> getTransaccionByEstado(String estado) {
        List<String> estadoTransaccion = new ArrayList<String>();
        estadoTransaccion.add(estado);
        EntityManager em = getEntityManager();
        Query q = em.createQuery("SELECT c FROM Transaccion c JOIN FETCH c.transacciones po WHERE po.opestado IN :estadoTransaccion");
        q.setParameter("estadoTransaccion", estadoTransaccion);
        return q.getResultList();
    }

}
