package model.dao;

import db.DB;
import model.dao.impl.DepartmentDao;
import model.dao.impl.SellerDao;

public class DaoFactory {

    public static DAO createSellerDao() {
        return new SellerDao(DB.getConnection());
    }

    public static DAO createDepartmentDao() {
        return new DepartmentDao(DB.getConnection());
    }
}