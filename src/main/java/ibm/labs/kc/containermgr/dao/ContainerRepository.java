package ibm.labs.kc.containermgr.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ibm.labs.kc.containermgr.model.ContainerEntity;

@Repository
public interface ContainerRepository extends JpaRepository<ContainerEntity,String>  {

	 List<ContainerEntity> findByCurrentCity(String currentCity);
}
