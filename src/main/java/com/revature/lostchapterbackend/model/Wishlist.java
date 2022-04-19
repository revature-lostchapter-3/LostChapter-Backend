package com.revature.lostchapterbackend.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class Wishlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    //Set the one-to-one relationship with Users model
    @OneToOne
    private Users user;

    @OneToMany
    private List<BookToBuy> booksToBuy;

    public Wishlist() {
    }

    public Wishlist(Users user) {
        this.user = user;
    }

    public Wishlist(List<BookToBuy> booksToBuy) {
        this.booksToBuy = booksToBuy;
    }

    public Wishlist(int id, Users user, List<BookToBuy> booksToBuy) {
        this.id = id;
        this.user = user;
        this.booksToBuy = booksToBuy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public List<BookToBuy> getBooksToBuy() {
        return booksToBuy;
    }

    public void setBooksToBuy(List<BookToBuy> booksToBuy) {
        this.booksToBuy = booksToBuy;
    }

    @Override
    public String toString() {
        return "Wishlist{" +
                "id=" + id +
                ", user=" + user +
                ", booksToBuy=" + booksToBuy +
                '}';
    }
}
