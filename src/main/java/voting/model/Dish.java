package voting.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "dish",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"rest_id", "dish_date", "name"}, name = "dish_unique_restaurant_date_name_idx")},
        indexes = @Index(name = "dish_dish_date_idx", columnList = "dishDate"))
@Getter
@ToString(callSuper = true)
public class Dish extends NamedEntity {

    @NotNull
    @PositiveOrZero
    @Column(name = "price", nullable = false, precision = 6, scale = 2)
    private BigDecimal price;

    @Column(name = "dish_date", nullable = false, updatable = false)
    @NotNull
    @FutureOrPresent
    private final LocalDate dishDate = LocalDate.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rest_id", nullable = false, updatable = false)
    @JsonIgnore
    @Setter
    @ToString.Exclude
    private Restaurant restaurant;
}
