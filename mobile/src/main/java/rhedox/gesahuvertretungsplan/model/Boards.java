package rhedox.gesahuvertretungsplan.model;

import java.util.List;

/**
 * Created by robin on 28.09.2016.
 */

public class Boards {

	private List<Board> boards;

	public Boards(List<Board> boards) {
		this.boards = boards;
	}

	public List<Board> getBoards() {
		return boards;
	}

	public static class Board {
		private String name;

		public Board(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}
}
