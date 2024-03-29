package voting.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "dish", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "rest_id", "dish_date"}, name = "dish_unique_name_restaurant_date_idx")})
@Getter
@ToString(callSuper = true, exclude = {"restaurant"})
public class Dish extends NamedEntity {

    @NotNull
    @DecimalMin(value = "1.00")
    @DecimalMax(value = "5000.00")
    @Column(name = "price", nullable = false, precision = 6, scale = 2)
    private BigDecimal price;

    @Column(name = "dish_date", nullable = false, columnDefinition = "timestamp default now()", updatable = false)
    @NotNull
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date dish_date = new Date();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rest_id", nullable = false, updatable = false)
    private Restaurant restaurant;
}
