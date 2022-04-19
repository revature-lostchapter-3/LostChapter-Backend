package com.revature.lostchapterbackend.model;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class BookToBuy {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	private Book books;

	private int quantityToBuy;

	public BookToBuy() {
		super();
	}

	public BookToBuy(Book books) {
		this.books = books;
	}

	public BookToBuy(Book books, int quantityToBuy) {
		super();
		this.books = books;
		this.quantityToBuy = quantityToBuy;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Book getBooks() {
		return books;
	}

	public void setBooks(Book books) {
		this.books = books;
	}

	public int getQuantityToBuy() {
		return quantityToBuy;
	}

	public void setQuantityToBuy(int quantityToBuy) {
		this.quantityToBuy = quantityToBuy;
	}

	@Override
	public int hashCode() {
		return Objects.hash(books, id, quantityToBuy);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BookToBuy other = (BookToBuy) obj;
		return Objects.equals(books, other.books) && id == other.id && quantityToBuy == other.quantityToBuy;
	}

	@Override
	public String toString() {
		return "BookToBuy [id=" + id + ", books=" + books + ", quantityToBuy=" + quantityToBuy + "]";
	}
}
