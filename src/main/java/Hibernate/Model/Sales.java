package Hibernate.Model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Sales {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private double price;
    @Column(nullable = false)
    private double quantity;
    @CreationTimestamp
    private LocalDateTime addTime;

    @ManyToOne
    @EqualsAndHashCode.Exclude
    private Product product;
}
