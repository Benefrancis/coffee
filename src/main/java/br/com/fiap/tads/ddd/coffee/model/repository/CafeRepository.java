package br.com.fiap.tads.ddd.coffee.model.repository;

import java.lang.invoke.MethodHandles;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.fiap.tads.ddd.coffee.model.Coffee;
import jakarta.ejb.Stateless;

@Stateless
public class CafeRepository extends Repository {

	private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	public static List<Coffee> getAllCoffees() {
		logger.log(Level.INFO, "Finding all coffees.");

		List<Coffee> retorno = new ArrayList<>();

		String sql = "SELECT * from DDD_COFFEE";

		PreparedStatement ps = null;

		ResultSet rs = null;

		try {
			ps = getConnection().prepareStatement(sql);
			rs = ps.executeQuery();

			if (rs.isBeforeFirst()) {
				while (rs.next()) {
					retorno.add(new Coffee(rs.getLong("ID"), rs.getString("NAME"), rs.getDouble("PRICE")));
				}
			} else {
				System.out.println("Não temos cafés cadastrados no banco de dados.");
			}
			return retorno;
		} catch (SQLException e) {
			System.out.println("Não foi possível consultar as vagas disponíveis: " + e.getMessage());
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				System.out.println("Erro ao tentar fechar o Statment ou o ResultSet");
			}
			if (connection != null)
				closeConnection();
		}

		return retorno;
	}

	public static Coffee persistCoffee(Coffee coffee) {
		logger.log(Level.INFO, "Persisting the new coffee {0}.", coffee);

		String sql = "INSERT INTO DDD_COFFEE (ID, NAME, PRICE) VALUES (SQ_COFFEE.nextvalue,?,?)";

		ResultSet rs = null;

		PreparedStatement ps = null;

		try {
			ps = getConnection().prepareStatement(sql);
			ps.setString(1, coffee.getName());
			ps.setDouble(2, coffee.getPrice());
			rs = ps.executeQuery();
			if (rs.isBeforeFirst()) {
				while (rs.next()) {
					coffee.setId(rs.getLong("ID"));
				}
				return coffee;
			} else {
				return null;
			}
		} catch (SQLException e) {
			System.out.println("Erro ao salvar o sorteio o banco de dados: " + e.getMessage());
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (SQLException e) {
				System.out.println("Erro ao tentar fechar o Statment ");
			}
		}
		return null;
	}

	public static boolean remove(Long id) {

		logger.log(Level.INFO, "Removing a coffee {0}.", id);
		Coffee coffee = findById(id);

		String sql = "DELETE FROM DDD_COFFEE where ID= ?";

		PreparedStatement ps = null;

		try {
			ps = getConnection().prepareStatement(sql);
			ps.setLong(1, coffee.getId());
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.out.println("Erro ao deletar o café no banco de dados: " + e.getMessage());
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (SQLException e) {
				System.out.println("Erro ao tentar fechar o Statment ");
			}
		}
		return false;
	}

	public static Coffee findById(Long id) {

		logger.log(Level.INFO, "Finding the coffee with id {0}.", id);

		String sql = "SELECT * from DDD_COFFEE where id  =?";

		PreparedStatement ps = null;

		ResultSet rs = null;

		try {
			ps = getConnection().prepareStatement(sql);
			ps.setLong(1, id);

			rs = ps.executeQuery();

			if (rs.isBeforeFirst()) {
				while (rs.next()) {
					return new Coffee(rs.getLong("ID"), rs.getString("NAME"), rs.getDouble("PRICE"));
				}
			} else {
				System.out.println("Coffe not found");
			}

			return null;

		} catch (SQLException e) {
			System.out.println("Não foi possível consultar o café: " + e.getMessage());
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				System.out.println("Erro ao tentar fechar o Statment ou o ResultSet");
			}

		}

		return null;
	}

}
