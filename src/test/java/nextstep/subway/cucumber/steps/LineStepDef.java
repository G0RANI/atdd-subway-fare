package nextstep.subway.cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java8.En;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.cucumber.AcceptanceContext;
import nextstep.subway.line.application.dto.LineResponse;
import nextstep.subway.station.application.dto.StationResponse;
import nextstep.subway.testhelper.apicaller.LineApiCaller;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LineStepDef implements En {
    @Autowired
    private AcceptanceContext context;

    public LineStepDef() {
        Given("지하철 노선들을 생성 요청하고", (DataTable table) -> {
            List<Map<String, String>> maps = table.asMaps();
            maps.stream()
                    .forEach(it -> {
                        Map<String, String> params = new HashMap<>();
                        params.put("name", it.get("name"));
                        params.put("color", it.get("color"));
                        params.put("upStationId", ((StationResponse) context.store.get(it.get("upStation"))).getId().toString());
                        params.put("downStationId", ((StationResponse) context.store.get(it.get("downStation"))).getId().toString());
                        params.put("distance", it.get("distance"));
                        params.put("duration", it.get("duration"));
                        params.put("surcharge", it.get("surcharge"));
                        ExtractableResponse<Response> response = LineApiCaller.지하철_노선_생성(params);
                        context.store.put(params.get("name"), (new ObjectMapper()).convertValue(response.jsonPath().get(), LineResponse.class));
                    });
        });

        Given("지하철 구간을 등록 요청하고", (DataTable table) -> {
            List<Map<String, String>> maps = table.asMaps();
            maps.stream()
                    .forEach(it -> {
                        String lineName = it.get("lineName");
                        Map<String, String> params = new HashMap<>();
                        params.put("upStationId", ((StationResponse) context.store.get(it.get("upStation"))).getId().toString());
                        params.put("downStationId", ((StationResponse) context.store.get(it.get("downStation"))).getId().toString());
                        params.put("distance", it.get("distance"));
                        params.put("duration", it.get("duration"));
                        params.put("surcharge", it.get("surcharge"));
                        LineResponse line = (LineResponse) context.store.get(lineName);
                        LineApiCaller.지하철_노선에_구간_추가(params, "/lines/" + line.getId());
                    });
        });
    }
}
