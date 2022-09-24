package br.com.fiap.tads.ddd.coffee.controller;

import java.lang.invoke.MethodHandles;
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
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/coffees")
public class CoffeeRestController {

	private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Coffee> getAllCoffeesJson() {
		logger.log(Level.INFO, "Consultando a listagem de cafés.");
		return CafeRepository.getAllCoffees();
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	public static Coffee createCoffee(Coffee coffee) {
		try {
			return CafeRepository.persistCoffee(coffee);
		} catch (PersistenceException e) {
			logger.log(Level.SEVERE, "Error creating coffee {0}: {1}.", new Object[] { coffee, e });
			throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	@GET
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Coffee getCoffeeById(@PathParam("id") Long coffeeId) {
		return CafeRepository.findById(coffeeId);
	}

	@DELETE
	@Path("{id}")
	public static void deleteCoffee(@PathParam("id") Long coffeeId) {
		try {
			if (CafeRepository.remove(coffeeId)) {
				logger.log(Level.INFO, "Café removido com sucesso {0}.", new Object[] { coffeeId });
			}
		} catch (IllegalArgumentException ex) {
			logger.log(Level.SEVERE, "Error calling deleteCoffee() for coffeeId {0}: {1}.",
					new Object[] { coffeeId, ex });
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

}