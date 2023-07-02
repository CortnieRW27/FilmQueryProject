package com.skilldistillery.filmquery.app;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.skilldistillery.filmquery.database.DatabaseAccessor;
import com.skilldistillery.filmquery.database.DatabaseAccessorObject;
import com.skilldistillery.filmquery.entities.Film;

public class FilmQueryApp {
	private boolean keepAsking = true;

	DatabaseAccessor db = new DatabaseAccessorObject();

	public static void main(String[] args) throws SQLException {
		FilmQueryApp app = new FilmQueryApp();
//		app.test();
		app.launch();
	}

	private void test() throws SQLException {
		Film film = db.findFilmById(1);
		System.out.println(film);
	}

	private void launch() throws SQLException {
		Scanner input = new Scanner(System.in);

		startUserInterface(input);

		input.close();
	}

	private void startUserInterface(Scanner input) throws SQLException {
		while (keepAsking) {
			System.out.println("Enter the menu number based on how you would like to search for a film.\n"
					+ "1. Look up a film by its id.\n" + "2. Look up a film with a keyword.\n" + "3. Exit.\n");

			int option = input.nextInt();
			input.nextLine();

			switch (option) {
			case 1:
				System.out.println("Enter the film id ");
				int idNum = input.nextInt();
				input.nextLine();
				Film filmById = db.findFilmById(idNum);
				break;
			case 2:
				System.out.println("Enter keyword");
				String keyWord = input.nextLine();
				List<Film> filmByKeyWord = db.findFilmByKeyWord(keyWord);
				break;
			case 3:
				System.out.println("See Ya!");
				keepAsking = false;
				break;
			}

		}

	}
}
