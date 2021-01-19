package model.services;

import model.dao.DAO;
import model.dao.DaoFactory;
import model.entities.Department;

import java.util.List;

public class DepartmentService {

    private DAO<Department> dao = DaoFactory.createDepartmentDao();

    public List<Department> findAll() {
        return dao.findAll();
    }
}
