package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Lines;
import nextstep.subway.member.domain.Member;
import nextstep.subway.testhelper.fixture.StationFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PathTest {

    @CsvSource({"1, 1250", "10, 1250"})
    @ParameterizedTest(name = "10㎞ 이내면 1Km 마다 기본운임(1,250원) 이 부과된다. (거리: {0}, 요금: {1} )")
    void calculateDefaultFare(Long distance,
                              Long fare) {
        Path path = new Path(Collections.emptyList(), distance, 1L);

        Long actual = path.fare(Lines.from(Collections.emptyList()));
        assertThat(actual).isEqualTo(fare);
    }

    @CsvSource({"11, 1350", "50, 2050"})
    @ParameterizedTest(name = "10km 초과 ∼ 50km 까지 5km 마다 추가운임(100원) 부과된다. (거리: {0}, 요금: {1} )")
    void calculate10To50tFare(Long distance,
                              Long fare) {
        Path path = new Path(Collections.emptyList(), distance, 1L);

        Long actual = path.fare(Lines.from(Collections.emptyList()));
        assertThat(actual).isEqualTo(fare);
    }

    @CsvSource({"50, 2050", "51, 2150", "56, 2150", "59, 2250"})
    @ParameterizedTest(name = "50km 초과 시 8km 마다 추가운임(100원) 부과된다. (거리: {0}, 요금: {1} )")
    void calculate50OverFare(Long distance,
                             Long fare) {
        Path path = new Path(Collections.emptyList(), distance, 1L);

        Long actual = path.fare(Lines.from(Collections.emptyList()));
        assertThat(actual).isEqualTo(fare);
    }

    @Test
    @DisplayName("추가 요금 노선을 지나면 요금이 부과 된다")
    void surcharge() {
        Line 이호선 = new Line("이호선", "green", StationFixture.교대역, StationFixture.강남역, 10L, 10L, 0L);
        Line 분당선 = new Line("분당선", "red", StationFixture.강남역, StationFixture.양재역, 10L, 10L, 900L);
        Lines lines = Lines.from(List.of(이호선, 분당선));
        PathFinder pathFinder = new JGraphPathFinder();
        Path path = pathFinder.shortcut(lines, StationFixture.강남역, StationFixture.양재역, PathType.DISTANCE);

        Long actual = path.fare(lines);
        Long expected = 2150L;
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("추가 요금 노선들을 지나가면 최대 요금이 부과 된다")
    void maxSurcharge() {
        Line 이호선 = new Line("이호선", "green", StationFixture.교대역, StationFixture.강남역, 10L, 10L, 200L);
        Line 분당선 = new Line("분당선", "red", StationFixture.강남역, StationFixture.양재역, 10L, 10L, 500L);
        Lines lines = Lines.from(List.of(이호선, 분당선));
        PathFinder pathFinder = new JGraphPathFinder();
        Path path = pathFinder.shortcut(lines, StationFixture.강남역, StationFixture.양재역, PathType.DISTANCE);

        Long actual = path.fare(lines);
        Long expected = 1750L;
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("사용자의 나이가 13세 이상~19세 미만 이면 운임에서 350원을 공제한 금액의 20%할인 된다 (요금: 720)")
    void teenagerFare() {
        Path path = new Path(Collections.emptyList(), 10L, 1L);
        Member member = new Member("age15@test.com", "password", 15);

        Long actual = path.fare(Lines.from(Collections.emptyList()), member);
        Long expected = 720L;
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("사용자의 나이가 6세 이상~ 13세 미만 이면 운임에서 350원을 공제한 금액의 50%할인 된다 (요금: 450)")
    void childrenFare() {
        Path path = new Path(Collections.emptyList(), 10L, 1L);
        Member member = new Member("age7@test.com", "password", 7);

        Long actual = path.fare(Lines.from(Collections.emptyList()), member);
        Long expected = 450L;
        assertThat(actual).isEqualTo(expected);
    }

}
