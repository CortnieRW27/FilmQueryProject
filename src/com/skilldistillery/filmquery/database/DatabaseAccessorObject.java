package com.skilldistillery.filmquery.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.skilldistillery.filmquery.entities.Actor;
import com.skilldistillery.filmquery.entities.Film;

public class DatabaseAccessorObject implements DatabaseAccessor {
	private static final String URL = "jdbc:mysql://localhost:3306/sdvid?useSSL=false&useLegacyDatetimeCode=false&serverTimezone=US/Mountain";
	private static final String user = "student";
	private static final String pass = "student";

	static {
		try {
			Class.forName("com.mysql.cj.jdc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	@Override
	public Film findFilmById(int filmId) {
		Film film = null;
		String sql = "SELECT * FROM film WHERE id = ?";

		Connection conn = DriverManager.getConnection(URL, user, pass);

		PreparedStatement stmt = conn.prepareStatement(sql);

		// bind variable for the one ?
		stmt.setInt(1, filmId);

		ResultSet rs = stmt.executeQuery();

		if (rs.next()) {
			film = new Film(); // Create the object

			// Here is our mapping of query columns to our object fields:
			film.setId(rs.getInt("id"));
			film.setTitle(rs.getString("title"));
			film.setDescription(rs.getString("description"));
			film.setReleaseYear(rs.getInt("release_year"));
			film.setLanguageId(rs.getInt("language_id"));
			film.setRentalDuration(rs.getInt("rental_duration"));
			film.setRentalRate(rs.getDouble("rental_rate"));
			film.setLength(rs.getInt("length"));
			film.setReplacementCost(rs.getDouble("replacement_cost"));
			film.setRating(rs.getString("rating"));
			film.setSpecialFeatures(rs.getString("special_features"));

		}
	}

	rs.close();
	stmt.close();
	conn.close();
	return film;



	@Override
	public Actor findActorById(int actorId) throws SQLException {
		Actor actor = null;
		String sql = "SELECT * FROM actor WHERE id = ?";

		Connection conn = DriverManager.getConnection(URL, user, pass);

		PreparedStatement stmt = conn.prepareStatement(sql);

		// bind variable for the one ?
		stmt.setInt(1, actorId);

		ResultSet rs = stmt.executeQuery();

		if (rs.next()) {
			actor = new Actor(); // Create the object

			// Here is our mapping of query columns to our object fields:
			actor.setId(rs.getInt("id"));
			actor.setFirstName(rs.getString("first_name"));
			actor.setLastName(rs.getString("last_name"));
			actor.setFilms(findFilmsByActorId(actorId));
		}

		rs.close();
		stmt.close();
		conn.close();
		return actor;

	}

	@Override
	public List<Film> findFilmsByActorId(int actorId) {
		List<Film> films = new ArrayList<>();
		// if isEmpty is true, printout they work at taco bell
		try {
			Connection conn = DriverManager.getConnection(URL, user, pass);

			String sql = "SELECT id, title, description, release_year, language_id, rental_duration, ";
			sql += " rental_rate, length, replacement_cost, rating, special_features "
					// could also be written SELECT "film.*" + FROM....
					+ " FROM film JOIN film_actor ON film.id = film_actor.film_id " + " WHERE actor_id = ?";

			PreparedStatement stmt = conn.prepareStatement(sql);
			System.out.println(stmt); // to check the mysql statement, run it in terminal and check it

			stmt.setInt(1, actorId);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				int filmId = rs.getInt("id");
				String title = rs.getString("title");
				String desc = rs.getString("description");
				short releaseYear = rs.getShort("release_year");
				int langId = rs.getInt("language_id");
				int rentDur = rs.getInt("rental_duration");
				double rate = rs.getDouble("rental_rate");
				int length = rs.getInt("length");
				double repCost = rs.getDouble("replacement_cost");
				String rating = rs.getString("rating");
				String features = rs.getString("special_features");
				Film film = new Film(filmId, title, desc, releaseYear, langId, rentDur, rate, length, repCost, rating,
						features);
				films.add(film);
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return films;
	}

	@Override
	public List<Actor> findActorsByFilmId(int filmId) {
		List<Actor> actors = new ArrayList<>();

		try {
			Connection conn = DriverManager.getConnection(URL, user, pass);

			String sql = "SELECT actor.*" + "FROM actor JOIN film_actor ON actor.id = film_actor.actor_id"
					+ "WHERE film_id = ?";

			PreparedStatement stmt = conn.prepareStatement(sql);
			System.out.println(stmt);

			stmt.setInt(1, filmId);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				int actorId = rs.getInt("id");
				String firstName = rs.getString("first_name");
				String lastName = rs.getString("last_name");
				Actor actor = new Actor(actorId, firstName, lastName);
				actors.add(actor);

			}

			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return actors;
	}

}
