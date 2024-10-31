package duongvanbao.Book.Store.service;

import duongvanbao.Book.Store.model.Supplier;
import duongvanbao.Book.Store.repository.ISupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupplierService {
    @Autowired
    private ISupplierRepository supplierRepository;

    public SupplierService(ISupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<Supplier> getAllSuppliers() {
        return (List<Supplier>) supplierRepository.findAll();
    }

    public Optional<Supplier> getSupplierByID(String id) {
        return supplierRepository.findById(id);
    }

    public Supplier saveSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    public void deleteSupplier(Supplier supplier) {
        supplierRepository.delete(supplier);
    }
}
