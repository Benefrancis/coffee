package br.com.fiap.tads.ddd.coffee.controller;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.fiap.tads.ddd.coffee.model.Coffee;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/coffees")
public class CoffeeRestController {

	private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Coffee> getAllCoffeesJson() {
		logger.log(Level.INFO, "Consultando a listagem de cafés.");
		return Arrays.asList(new Coffee("Benefrancis", 9.99));
	}

}