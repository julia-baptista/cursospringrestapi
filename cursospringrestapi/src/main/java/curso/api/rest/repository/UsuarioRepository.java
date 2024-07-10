package curso.api.rest.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import curso.api.rest.model.Usuario;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long> {
	
	
//	@Query("select u from Usuario u where u.login = ?1")
//	Usuario findUserByLogin(String login);
	
	@Query("select u from Usuario u where u.login = :login")
	Usuario findUserByLogin(@Param("login") String login);


}
