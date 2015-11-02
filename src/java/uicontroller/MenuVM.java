/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uicontroller;

import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;

/**
 *
 * @author mdamaceno
 */
public class MenuVM {

    private boolean isLogged;

    public boolean confirmIsLogged() {
        isLogged = Sessions.getCurrent().getAttribute("user") != null;
        return isLogged;
    }

    @Command
    public void goHome() {
        if (confirmIsLogged()) {
            Executions.sendRedirect("admin.zul");
        } else {
            Executions.sendRedirect("home.zul");
        }
    }

    public boolean isIsLogged() {
        return isLogged;
    }

    public void setIsLogged(boolean isLogged) {
        this.isLogged = isLogged;
    }
}
