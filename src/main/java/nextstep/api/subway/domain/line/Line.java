package nextstep.api.subway.domain.line;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nextstep.api.subway.domain.station.Station;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private Long fare = 0L;

    @Embedded
    private LineSections sections;

    public Line(final String name, final String color, final long fare, final Section section) {
        this.name = name;
        this.color = color;
        this.fare = fare;
        this.sections = LineSections.init(section);
    }

    public void update(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    public void appendSection(final Section section) {
        sections.append(section);
    }

    public void removeSection(final Station station) {
        sections.remove(station);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getFare() {
        return fare;
    }

    public List<Section> getSections() {
        return sections.getValue();
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public Station getFirstStation() {
        return sections.getFirstStation();
    }

    public Station getLastStation() {
        return sections.getLastStation();
    }
}