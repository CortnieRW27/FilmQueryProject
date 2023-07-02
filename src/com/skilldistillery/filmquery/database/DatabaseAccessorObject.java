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
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	@Override
	public Film findFilmById(int filmId) throws SQLException {
		Film film = null;
		String sql = "SELECT film.id, film.title, film.release_year, film.rating, film.description, language.name, actor.first_name, actor.last_name\n"
				+ "FROM film\n" + "JOIN language\n" + "ON language.id = film.language_id\n" + "JOIN film_actor\n"
				+ "ON film.id = film_actor.film_id\n" + "JOIN actor\n" + "ON film_actor.actor_id = actor.id\n"
				+ "WHERE film.id = ?";

		Connection conn = DriverManager.getConnection(URL, user, pass);

		PreparedStatement stmt = conn.prepareStatement(sql);

		// bind variable for the one ?
		stmt.setInt(1, filmId);

		ResultSet rs = stmt.executeQuery();

		if (rs.next()) {
			film = new Film(); // Create the object

			// Here is our mapping of query columns to our object fields:
			film.setTitle(rs.getString("title"));
			film.setDescription(rs.getString("description"));
			film.setReleaseYear(rs.getInt("release_year"));
			film.setRating(rs.getString("rating"));
			film.setLanguage(rs.getString("name"));

			List<Actor> actors = new ArrayList<>();

			do {
				Actor actor = new Actor();
				actor.setFirstName(rs.getString("first_name"));
				actor.setLastName(rs.getString("last_name"));
				actors.add(actor);

			} while (rs.next());

			film.setActors(actors);
		}
		rs.close();
		stmt.close();
		conn.close();

		if (film != null) {
			System.out.println("Title: " + film.getTitle());
			System.out.println("Description: " + film.getDescription());
			System.out.println("Release Year: " + film.getReleaseYear());
			System.out.println("Rating: " + film.getRating());
			System.out.println("Language: " + film.getLanguage());
			System.out.println("Actors: ");
			for (Actor actor : film.getActors()) {
				System.out.println(actor.getFirstName() + " " + actor.getLastName());
			}
		} else {
			System.out.println(
					"That movie does not exist! You could go to school, spend thousands to become a movie producer and create it!");
		}

		return film;
	}

	@Override
	public List<Film> findFilmByKeyWord(String keyWord) {
		List<Film> films = new ArrayList<>();
		try {
			Connection conn = DriverManager.getConnection(URL, user, pass);

			String sql = "SELECT film.id, film.title, film.release_year, film.rating, film.description, language.name, actor.first_name, actor.last_name\n"
					+ "FROM film\n" + "JOIN language\n" + "ON language.id = film.language_id\n" + "JOIN film_actor\n"
					+ "ON film.id = film_actor.film_id\n" + "JOIN actor\n" + "ON film_actor.actor_id = actor.id\n"
					+ "WHERE film.title LIKE ? OR film.description LIKE ?";

			PreparedStatement stmt = conn.prepareStatement(sql);

			stmt.setString(1, "%" + keyWord + "%");
			stmt.setString(2, "%" + keyWord + "%");

			ResultSet rs = stmt.executeQuery();
			
			boolean matchFound = false;

			while (rs.next()) {
				matchFound = true;
				int filmId = rs.getInt("id");
				String title = rs.getString("title");
				String description = rs.getString("description");
				int releaseYear = rs.getInt("release_year");
				String rating = rs.getString("rating");
				String languageName = rs.getString("name");

				Film film = new Film();
				film.setId(filmId);
				film.setTitle(title);
				film.setDescription(description);
				film.setReleaseYear(releaseYear);
				film.setRating(rating);
				film.setLanguage(languageName);
				films.add(film);
				film.setActors(findActorsByFilmId(filmId));
			}

			rs.close();
			stmt.close();
			conn.close();

			if (!matchFound) {
				System.out.println("WOAH! No such movie title or description with the keyword " + keyWord + "exists");
			} else {
				String previousTitle = null;
				for (Film film : films) {
					String currentTitle = film.getTitle();
					if (currentTitle.equals(previousTitle)) {
						continue;
					}
					previousTitle = currentTitle;

					System.out.println("\nTitle: " + film.getTitle());
					System.out.println("Description: " + film.getDescription());
					System.out.println("Release Year: " + film.getReleaseYear());
					System.out.println("Rating: " + film.getRating());
					System.out.println("Language: " + film.getLanguage());
					System.out.println("Actors: ");
					for (Actor actor : film.getActors()) {
						System.out.println(actor.getFirstName() + " " + actor.getLastName());
					}
				}
			}
		} catch (

		SQLException e) {
			e.printStackTrace();
		}

		return films;
	}

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

			String sql = "SELECT actor.*" + "FROM actor JOIN film_actor ON actor.id = film_actor.actor_id "
					+ "WHERE film_actor.film_id = ?";

			PreparedStatement stmt = conn.prepareStatement(sql);

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
