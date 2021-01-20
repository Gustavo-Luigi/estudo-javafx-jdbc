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

    public void saveOrUpdate(Department department) {
        if(department.getId() == null) {
            dao.insert(department);
        } else {
            dao.update(department);
        }
    }

    public void remove(Department department) {
        dao.deleteById(department.getId());
    }
}
