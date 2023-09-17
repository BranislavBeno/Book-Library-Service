package com.book.library.dto;

import com.book.library.reader.Reader;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public final class ReaderDto implements DataTransferObject {

    private int id;

    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    @Email
    private String email;

    public ReaderDto(Reader reader) {
        this(reader.getId(), reader.getFirstName(), reader.getLastName(), reader.getEmail());
    }

    public ReaderDto(int id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String readerName() {
        return "%s %s".formatted(firstName, lastName);
    }

    @Override
    public String toString() {
        return "Reader[id=%d, name='%s', email='%s']".formatted(id, readerName(), email);
    }
}
