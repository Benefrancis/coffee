package br.com.fiap.tads.ddd.coffee.controller;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.fiap.tads.ddd.coffee.model.Coffee;
import br.com.fiap.tads.ddd.coffee.model.repository.CafeRepository;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.UriBuilder;

/**
 * https://docs.oracle.com/middleware/12212/wls/RESTF/develop-restful-service.htm#RESTF208
 * https://blog.payara.fish/getting-started-with-jakarta-ee-9-how-to-create-a-rest-api-with-jakarta-ee-9
 * https://www.oreilly.com/library/view/restful-java-with/9781449361433/ch04.html
 * http://www.mastertheboss.com/jboss-frameworks/resteasy/getting-started-with-jakarta-restful-services/
 * 
 * @author Francis
 *
 */
@Path("/coffees")
public class CoffeeRestController {

	private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getAllCoffeesJson() {
		logger.log(Level.INFO, "Consultando a listagem de cafés.");
		List<Coffee> resp = CafeRepository.getAllCoffees();
		ResponseBuilder response = Response.ok(resp);
		response.entity(resp);
		return response.build();
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	public static Response createCoffee(Coffee coffee) {
		try {
			Coffee resp = CafeRepository.persistCoffee(coffee);
			// @formatter:off
 			final URI coffeeUri = UriBuilder.fromResource(CoffeeRestController.class).path("/coffees/{id}").build(resp.getId());
 			// @formatter:on
			ResponseBuilder response = Response.created(coffeeUri);
			response.entity(resp);
			return response.build();
		} catch (PersistenceException e) {
			logger.log(Level.SEVERE, "Error creating coffee {0}: {1}.", new Object[] { coffee, e });
			throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	@GET
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getCoffeeById(@PathParam("id") Long coffeeId) {
		Coffee resp = CafeRepository.findById(coffeeId);
		ResponseBuilder response = Response.ok(resp);
		response.entity(resp);
		return response.build();
	}

	/**
	 * @see https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods/PUT
	 * @param coffeeId
	 * @param coffee
	 * @return
	 */
	@PUT
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response updateCoffee(@PathParam("id") Long coffeeId, Coffee coffee) {
		Coffee old = null;
		old = CafeRepository.findById(coffeeId);

		if (old == null || old.getId() != coffee.getId()) {
			Coffee resp = CafeRepository.persistCoffee(coffee);
			// @formatter:off
 			final URI coffeeUri = UriBuilder.fromResource(CoffeeRestController.class).path("/coffees/{id}").build(resp.getId());
 			// @formatter:on
			ResponseBuilder response = Response.created(coffeeUri);
			response.entity(resp);
			return response.build();
		}

		Coffee resp = CafeRepository.update(coffee);
		ResponseBuilder response = Response.ok(resp);
		response.entity(resp);
		return response.build();
	}

	@DELETE
	@Path("{id}")
	public static Response deleteCoffee(@PathParam("id") Long coffeeId) {
		try {
			if (CafeRepository.remove(coffeeId)) {
				logger.log(Level.INFO, "Café removido com sucesso {0}.", new Object[] { coffeeId });
				ResponseBuilder response = Response.noContent();
				return response.build();
			} else {
				logger.log(Level.INFO, "Usuário tentou remover café que não existia no banco de dados:  {0}.",
						new Object[] { coffeeId });
			}
		} catch (IllegalArgumentException ex) {
			// Irei apenas gerar o log. Não deixarei o usuário saber que o coffe não existe
			// @formatter:off
 			logger.log(Level.SEVERE, "Error calling deleteCoffee() for coffeeId {0}: {1}.",new Object[] { coffeeId, ex });
 			// @formatter:on
		}
		ResponseBuilder response = Response.noContent();
		return response.build();
	}

}