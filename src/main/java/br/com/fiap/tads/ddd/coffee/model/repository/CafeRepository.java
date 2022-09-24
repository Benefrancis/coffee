package br.com.fiap.tads.ddd.coffee.model.repository;

import java.lang.invoke.MethodHandles;
import java.sql.CallableStatement;
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
		logger.log(Level.INFO, sql);
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

		// Colocando SQL numa transaaction para pegar o id do café inserido
		String sql = "begin INSERT INTO DDD_COFFEE ( id, NAME, PRICE) VALUES ( sq_coffee.nextval,?,?) returning id into ?;  end;";

		CallableStatement s = null;
		try {
			s = getConnection().prepareCall(sql);
			s.setString(1, coffee.getName());
			s.setDouble(2, coffee.getPrice());
			s.registerOutParameter(3, java.sql.Types.INTEGER);
			s.executeUpdate();
			coffee.setId((long) s.getInt(3));
			return coffee;
		} catch (SQLException e) {
			System.out.println("Erro ao salvar o coffee o banco de dados: " + e.getMessage());
		} finally {
			try {
				if (s != null)
					s.close();
			} catch (SQLException e) {
				System.out.println("Erro ao tentar fechar o Statment ");
			}
		}
		return null;
	}

	public static boolean remove(Long id) {

		logger.log(Level.INFO, "Removing a coffee {0}.", id);
		Coffee coffee = null;

		coffee = findById(id);

		if (coffee == null) {
			return false;
		}

		String sql = "DELETE FROM DDD_COFFEE where ID= ?";
		logger.log(Level.INFO, sql);
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
		logger.log(Level.INFO, sql);
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

	public static Coffee update(Coffee coffee) {

		logger.log(Level.INFO, "Update coffee {0}.", coffee);

		// Colocando SQL numa transaaction para pegar o id do café atualizado
		String sql = "begin UPDATE DDD_COFFEE set NAME = ?,   PRICE = ?  where id = ? returning id into ?;  end;";

		CallableStatement s = null;
		try {
			s = getConnection().prepareCall(sql);
			s.setString(1, coffee.getName());
			s.setDouble(2, coffee.getPrice());
			s.setLong(3, coffee.getId());
			s.registerOutParameter(4, java.sql.Types.INTEGER);
			s.executeUpdate();
			coffee.setId((long) s.getInt(4));
			return coffee;
		} catch (SQLException e) {
			System.out.println("Erro ao atualizar o coffee no banco de dados: " + e.getMessage());
		} finally {
			try {
				if (s != null)
					s.close();
			} catch (SQLException e) {
				System.out.println("Erro ao tentar fechar o Statment ");
			}
		}
		return null;
	}

}
