package Hibernate.Model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;

import java.util.Set;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id // PRIMARY KEY
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Enumerated(value = EnumType.STRING)
    private Category category;
    //  @Formula("(SELECT SUM(s.price*s.quantity)/SUM(s.quantity)) FROM sales s WHERE s.product_id=id)")
    // private Double avgWeighted;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "product") // nazwa pola
    private Set<Sales> sales;



}
