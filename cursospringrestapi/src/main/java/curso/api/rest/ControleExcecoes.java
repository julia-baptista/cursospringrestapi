package curso.api.rest;

import java.sql.SQLException;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@ControllerAdvice
public class ControleExcecoes extends ResponseEntityExceptionHandler {
	
	
	/*Interceptar erros mais comuns no projeto*/
	@Override
	@ExceptionHandler({Exception.class, RuntimeException.class, Throwable.class})
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		
		String msg = "";
		
		/*Essa exceção se tratar de argumentos inválidos */
		if(ex instanceof MethodArgumentNotValidException) {
			List<ObjectError> list = ((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors();
			for (ObjectError objectError : list) {
				msg += objectError.getDefaultMessage() + "\n";
			}
		} else {
			msg = ex.getMessage();
		}
		
		
		ObjetoErro objetoErro = new ObjetoErro();
		objetoErro.setError(msg);
		objetoErro.setCode(status.value() + " ==> " + status.getReasonPhrase());
		
		return new ResponseEntity<>(objetoErro, headers, status);
	}

	
	//Excções com erros a nível de banco de dados	
	
	
	  @ExceptionHandler({ DataIntegrityViolationException.class,
				ConstraintViolationException.class, PSQLException.class, SQLException.class,
				TransactionSystemException.class })
		protected ResponseEntity<Object> handleExceptionDataIntegrety(Exception ex) {

			String msg = "";

			Throwable rootCause = findRootCause(ex);

			if (ex instanceof DataIntegrityViolationException) {
				msg = ((DataIntegrityViolationException) ex).getCause().getCause().getMessage();
			} else if (rootCause instanceof ConstraintViolationException) {
				ConstraintViolationException cve = (ConstraintViolationException) rootCause;
				StringBuilder messages = new StringBuilder();
				for (ConstraintViolation<?> violation : cve.getConstraintViolations()) {
					messages.append(violation.getMessage()).append("; ");
				}
				msg = messages.toString();
			} else if (ex instanceof PSQLException) {
				msg = ((PSQLException) ex).getCause().getCause().getMessage();
			} else if (ex instanceof SQLException) {
				msg = ((SQLException) ex).getCause().getCause().getMessage();
			} else {
				msg = ex.getMessage();
			}

			ObjetoErro objetoError = new ObjetoErro();
			objetoError.setError(msg);
			objetoError.setCode(
					HttpStatus.INTERNAL_SERVER_ERROR + " ==> " + HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());

			return new ResponseEntity<>(objetoError, HttpStatus.INTERNAL_SERVER_ERROR);

		}
	 
	 	

	    private Throwable findRootCause(Throwable ex) {
	        Throwable cause = null; // Declara uma variável 'cause' do tipo Throwable e a inicializa como null.
	        Throwable result = ex; // Declara uma variável 'result' e a inicializa com a exceção passada como argumento.

	        // Dentro do laço, cause é atribuído a result.getCause(), que é a causa da exceção atual.
	        // O laço continua enquanto result.getCause() não for null e result não for a mesma que cause.
	        while ((cause = result.getCause()) != null && result != cause) {
	        	
	            result = cause; // Atualiza 'result' para ser a causa da exceção atual.
	        }        
	        
	        // Depois que o laço termina, result contém a causa raiz da exceção inicial ex.
	        // A causa raiz é a última exceção na cadeia de causas, que não tem uma causa própria (ou seja, getCause() retorna null).
	        return result; // Retorna a causa raiz, que é a última exceção encontrada na cadeia de causas.
	    }

	

}
