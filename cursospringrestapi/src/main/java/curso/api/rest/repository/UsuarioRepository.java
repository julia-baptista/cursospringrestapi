package curso.api.rest.repository;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import curso.api.rest.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	
	
//	@Query("select u from Usuario u where u.login = ?1")
//	Usuario findUserByLogin(String login);
	
	//  JPQL (Java Persistence Query Language) 
	@Query("select u from Usuario u where u.login = :login")
	Usuario findUserByLogin(@Param("login") String login);
	
	@Query("select u from Usuario u where u.nome like :nome")
	List<Usuario> findUserByNome(@Param("nome") String nome);
	
	@Transactional // O método atualizaTokenUser é executado dentro de uma transação. Se ocorrer alguma exceção não verificada, a transação será revertida.
	@Modifying // A anotação @Modifying indica que a consulta é uma operação de modificação (UPDATE, DELETE, INSERT).
	@Query(nativeQuery = true, value = "update usuario set token = :token where login = :login")
	void atualizaTokenUser(String token, String login);
	//  A consulta SQL é uma consulta nativa, escrita diretamente em SQL, em vez de usar JPQL ou HQL.
	
	@Query(value="select constraint_name from information_schema.constraint_column_usage where table_name = 'usuarios_role' and column_name = 'role_id'\n"
			+ "and constraint_name <> 'unique_role_user';", nativeQuery = true)
	String consultaConstraintRole();
	
	/*
	 * @Modifying
	 * 
	 * @Query(value="alter table usuarios_role drop constraint :constraint;",
	 * nativeQuery = true) void removerConstraintRole(String constraint);
	 */
	
	@Transactional
	@Modifying
	@Query(value="insert into usuarios_role (usuario_id, role_id)\n"
			+ "values(:idUser, (select id from role where nome_role = 'ROLE_USER'))", nativeQuery = true)
	void insereAcessoRolePadrao(Long idUser);

	
	
	default Page<Usuario> findUserByNamePage(String nome, PageRequest pageRequest) {
		Usuario usuario = new Usuario();
		usuario.setNome(nome);
		
		/*Configurando para pesquisar por nome e paginação*/
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny()
				.withMatcher("nome", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
		
		Example<Usuario> example = Example.of(usuario, exampleMatcher);
		
		System.out.println("Example: " + example);
		
		Page<Usuario> retorno = findAll(example, pageRequest);
		
		System.out.println("Retorno: " + retorno);
		
		return retorno;
	}
}








