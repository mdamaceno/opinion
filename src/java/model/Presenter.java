/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author mdamaceno
 */
@Entity
@Table(catalog = "opinion", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Presenter.findAll", query = "SELECT p FROM Presenter p"),
    @NamedQuery(name = "Presenter.findById", query = "SELECT p FROM Presenter p WHERE p.id = :id"),
    @NamedQuery(name = "Presenter.findByName", query = "SELECT p FROM Presenter p WHERE p.name = :name")})
public class Presenter implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(nullable = false, length = 150)
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "presenterId")
    private List<GridPresenter> gridPresenterList;

    public Presenter() {
    }

    public Presenter(Integer id) {
        this.id = id;
    }

    public Presenter(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlTransient
    public List<GridPresenter> getGridPresenterList() {
        return gridPresenterList;
    }

    public void setGridPresenterList(List<GridPresenter> gridPresenterList) {
        this.gridPresenterList = gridPresenterList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Presenter)) {
            return false;
        }
        Presenter other = (Presenter) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Presenter[ id=" + id + " ]";
    }
    
}
