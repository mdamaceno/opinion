/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uicontroller;

import dao.UserJpaController;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import model.User;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;

/**
 *
 * @author mdamaceno
 */
public class HomeVM {

    private String doc;

    @Command
    @NotifyChange({"doc"})
    public void login() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("OpinionPU");

        User user = new UserJpaController(emf).getUserbyDoc(doc);

        emf.close();

        if (user == null) {
            Messagebox.show("CPF não encontrado!");
            doc = "";
        } else {
            Sessions.getCurrent().setAttribute("User", user);
            Executions.getCurrent().sendRedirect("views/admin/dashboard.zul");
        }
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }
}
