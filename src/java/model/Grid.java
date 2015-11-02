/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
    @NamedQuery(name = "Grid.findAll", query = "SELECT g FROM Grid g"),
    @NamedQuery(name = "Grid.findById", query = "SELECT g FROM Grid g WHERE g.id = :id"),
    @NamedQuery(name = "Grid.findByName", query = "SELECT g FROM Grid g WHERE g.name = :name"),
    @NamedQuery(name = "Grid.findBySchedule", query = "SELECT g FROM Grid g WHERE g.schedule = :schedule")})
public class Grid implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(nullable = false, length = 150)
    private String name;
    @Temporal(TemporalType.TIME)
    private Date schedule;
    @Lob
    @Column(length = 65535)
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "gridId")
    private List<Comment> commentList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "gridId")
    private List<GridPresenter> gridPresenterList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "gridId")
    private List<Vote> voteList;

    public Grid() {
    }

    public Grid(Integer id) {
        this.id = id;
    }

    public Grid(Integer id, String name) {
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

    public Date getSchedule() {
        return schedule;
    }

    public void setSchedule(Date schedule) {
        this.schedule = schedule;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    @XmlTransient
    public List<GridPresenter> getGridPresenterList() {
        return gridPresenterList;
    }

    public void setGridPresenterList(List<GridPresenter> gridPresenterList) {
        this.gridPresenterList = gridPresenterList;
    }

    @XmlTransient
    public List<Vote> getVoteList() {
        return voteList;
    }

    public void setVoteList(List<Vote> voteList) {
        this.voteList = voteList;
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
        if (!(object instanceof Grid)) {
            return false;
        }
        Grid other = (Grid) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Grid[ id=" + id + " ]";
    }
    
}
