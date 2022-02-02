package com.library.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "customers")
public class Customer  implements Serializable {

  private static final long serialVersionUID = -5904485961101850235L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @NotNull(message = "Username is required")
  @Pattern(regexp = "[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\."
      + "[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+)*@"
      + "(?:[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?\\.)+[A-Za-z0-9]"
      + "(?:[A-Za-z0-9-]*[A-Za-z0-9])?",
      message = "Invalid email provided")
  public String username;
  @NotNull(message = "Password is required")
  public String password;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Customer)) {
      return false;
    }
    Customer customer = (Customer) o;
    return Objects.equals(getId(), customer.getId()) &&
        Objects.equals(getUsername(), customer.getUsername());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getUsername());
  }
}
