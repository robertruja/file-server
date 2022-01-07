package org.crumbs.fileserver;

import org.crumbs.core.annotation.CrumbsApplication;
import org.crumbs.core.context.CrumbsApp;

@CrumbsApplication
public class App {
    public static void main(String[] args) {
        CrumbsApp.run(App.class);
    }
}
