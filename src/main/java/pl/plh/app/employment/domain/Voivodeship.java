package pl.plh.app.employment.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode(doNotUseGetters = true)
@Table(name = "voivodeship")
@SequenceGenerator(name = "gen_voivodeship_seq", allocationSize = 1, sequenceName = "voivodeship_seq")
@Entity
public class Voivodeship {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen_voivodeship_seq")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 2, max = 255)
    @Column(name = "name", unique = true)
    private String name;
}
